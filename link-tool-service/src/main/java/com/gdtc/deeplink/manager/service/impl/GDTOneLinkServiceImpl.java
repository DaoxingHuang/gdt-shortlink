package com.gdtc.deeplink.manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.gdtc.deeplink.manager.configuration.ConfigParam;
import com.gdtc.deeplink.manager.convertor.GDTOneLinkMessageConvertor;
import com.gdtc.deeplink.manager.convertor.LandingPageMessageConvertor;
import com.gdtc.deeplink.manager.core.AbstractService;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.dao.GDTOneLinkMapper;
import com.gdtc.deeplink.manager.event.GDTOneLinkUpdateEvent;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import com.gdtc.deeplink.manager.handler.GDTOneLinkBusinessHandler;
import com.gdtc.deeplink.manager.model.GDTOneLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.service.GDTOneLinkService;
import com.gdtc.deeplink.manager.service.LandingPageService;
import com.gdtc.deeplink.manager.utils.PagePathUtils;
import com.gdtc.deeplink.manager.utils.StatusEnum;
import com.gdtc.deeplink.manager.utils.UTMUtils;
import com.gdtc.deeplink.manager.vo.GDTOneLinkVo;
import com.gdtc.deeplink.manager.vo.LandingPageVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GDTOneLinkServiceImpl extends AbstractService<GDTOneLink> implements GDTOneLinkService {
    public static final Logger logger = LoggerFactory.getLogger(GDTOneLinkServiceImpl.class);

    @Resource
    private GDTOneLinkMapper gdtOneLinkMapper;
    @Resource
    private LandingPageService landingPageService;

    @Autowired
    private GDTOneLinkBusinessHandler businessHandler;
    @Autowired
    private ConfigParam configParam;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void saveByVo(GDTOneLinkVo gdtOneLinkVo) {
        if (null == gdtOneLinkVo.getLandingPageId()) {
            throw new ServiceException("invalid landing page.");
        }

        LandingPage landingPage = this.landingPageService.findById(gdtOneLinkVo.getLandingPageId());

        if (null == landingPage) {
            throw new ServiceException("invalid landing page.");
        }

        int countWithSameUTM = this.gdtOneLinkMapper.countByUtm(gdtOneLinkVo.getUtmSource(), gdtOneLinkVo.getUtmMedium(), gdtOneLinkVo.getUtmCampaign(), gdtOneLinkVo.getUtmContent());
        if (countWithSameUTM >= this.configParam.getUtmSameTimes()) {
            throw new ServiceException("A utm can be associated with " + this.configParam.getUtmSameTimes() + " link at most.");
        }

        GDTOneLink gdtOneLink = GDTOneLinkMessageConvertor.voToModel(gdtOneLinkVo);

        this.businessHandler.generateLink(gdtOneLink, landingPage, gdtOneLinkVo.getParamMap());
        super.save(gdtOneLink);
    }

    @Override
    public void updateUTMByVo(GDTOneLinkVo gdtOneLinkVo) {
        if (null == gdtOneLinkVo.getId()) {
            throw new ServiceException("id is required.");
        }

        int countWithSameUTM = this.gdtOneLinkMapper.countByUtm(gdtOneLinkVo.getUtmSource(), gdtOneLinkVo.getUtmMedium(), gdtOneLinkVo.getUtmCampaign(), gdtOneLinkVo.getUtmContent());
        if (countWithSameUTM >= this.configParam.getUtmSameTimes()) {
            throw new ServiceException("A utm can be associated with " + this.configParam.getUtmSameTimes() + " link at most.");
        }

        UTMUtils.checkUtmParam(gdtOneLinkVo);

        GDTOneLink model = GDTOneLinkMessageConvertor.voToModel(gdtOneLinkVo);

        GDTOneLink gdtOneLink = this.gdtOneLinkMapper.selectByPrimaryKey(gdtOneLinkVo.getId());
        gdtOneLink.setUtmSource(gdtOneLinkVo.getUtmSource());
        gdtOneLink.setUtmMedium(gdtOneLinkVo.getUtmMedium());
        gdtOneLink.setUtmCampaign(gdtOneLinkVo.getUtmCampaign());
        gdtOneLink.setUtmContent(gdtOneLinkVo.getUtmContent());
        gdtOneLink.setParam(model.getParam());
        gdtOneLink.setUpdateTime(new Date());
        gdtOneLink.setName(model.getName());

        LandingPage landingPage = this.landingPageService.findById(gdtOneLink.getLandingPageId());
        this.businessHandler.updateUTM(gdtOneLink, landingPage);
        this.gdtOneLinkMapper.updateByPrimaryKeySelective(gdtOneLink);

        this.applicationContext.publishEvent(new GDTOneLinkUpdateEvent(gdtOneLink.getLink(), gdtOneLink.getId()));
    }

    @Override
    public void expireById(Integer id) {
        if (null == id) {
            throw new ServiceException("invalid id.");
        }

        GDTOneLink gdtOneLink = this.gdtOneLinkMapper.selectByPrimaryKey(id);
        if (null == gdtOneLink || StatusEnum.OFF.getCode().equals(gdtOneLink.getStatus())) {
            return;
        }

        GDTOneLink newGDTOneLink = new GDTOneLink();
        newGDTOneLink.setId(gdtOneLink.getId());
        newGDTOneLink.setStatus(StatusEnum.OFF.getCode());
        newGDTOneLink.setEditor(ThreadUserInfo.getUserInfo().getUsername());
        newGDTOneLink.setExpiredTime(new Date());
        this.gdtOneLinkMapper.updateByPrimaryKeySelective(newGDTOneLink);
        logger.info("expire GDTOneLink {}", id);
    }

    @Override
    public void activateById(Integer id) {
        if (null == id) {
            throw new ServiceException("invalid id.");
        }

        GDTOneLink gdtOneLink = this.gdtOneLinkMapper.selectByPrimaryKey(id);
        if (null == gdtOneLink || StatusEnum.ACTIVE.getCode().equals(gdtOneLink.getStatus())) {
            return;
        }

        gdtOneLink.setId(gdtOneLink.getId());
        gdtOneLink.setStatus(StatusEnum.ACTIVE.getCode());
        gdtOneLink.setEditor(ThreadUserInfo.getUserInfo().getUsername());
        gdtOneLink.setExpiredTime(null);
        this.gdtOneLinkMapper.updateByPrimaryKey(gdtOneLink);
        logger.info("reactive GDTOneLink {}", id);
    }

    @Override
    public List<GDTOneLinkVo> findAllVo() {
        Map<Integer, LandingPageVo> landingPageVoMap = this.landingPageService.findAllVoWithMap();
        return super.findAll().stream()
                .map(GDTOneLinkMessageConvertor::modelToVo)
                .map(gdtOneLinkVo -> {
                    gdtOneLinkVo.setLandingPageVo(landingPageVoMap.get(gdtOneLinkVo.getLandingPageId()));
                    return gdtOneLinkVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public GDTOneLinkVo findVoById(Integer id) {
        if (null == id) {
            throw new ServiceException("invalid param.");
        }
        GDTOneLinkVo vo = GDTOneLinkMessageConvertor.modelToVo(super.findById(id));
        vo.setLandingPageVo(LandingPageMessageConvertor.modelToVo(this.landingPageService.findById(vo.getLandingPageId())));
        return vo;
    }
}
