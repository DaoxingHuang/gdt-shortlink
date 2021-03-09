package com.gdtc.deeplink.manager.service.impl;

import com.gdtc.deeplink.manager.configuration.ConfigParam;
import com.gdtc.deeplink.manager.convertor.DeepLinkMessageConvertor;
import com.gdtc.deeplink.manager.convertor.LandingPageMessageConvertor;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.dao.DeepLinkMapper;
import com.gdtc.deeplink.manager.event.DeepLinkUpdateEvent;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import com.gdtc.deeplink.manager.handler.DeepLinkBusinessHandler;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.service.DeepLinkService;
import com.gdtc.deeplink.manager.core.AbstractService;
import com.gdtc.deeplink.manager.service.LandingPageService;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.utils.StatusEnum;
import com.gdtc.deeplink.manager.vo.DeepLinkVo;
import com.gdtc.deeplink.manager.vo.LandingPageVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by GDTC on 2020/11/30.
 */
@Service
@Transactional
public class DeepLinkServiceImpl extends AbstractService<DeepLink> implements DeepLinkService {
    private static final Logger logger = LoggerFactory.getLogger(DeepLinkServiceImpl.class);

    @Resource
    private DeepLinkMapper deepLinkMapper;
    @Resource
    private LandingPageService landingPageService;

    @Autowired
    private ConfigParam configParam;
    @Autowired
    private DeepLinkBusinessHandler deepLinkBusinessHandler;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void saveByVo(DeepLinkVo deepLinkVo) {
        if (null == deepLinkVo.getLandingPageId()) {
            throw new ServiceException("invalid landing page.");
        }

        LandingPage landingPage = this.landingPageService.findById(deepLinkVo.getLandingPageId());

        if (null == landingPage) {
            throw new ServiceException("invalid landing page.");
        }

        if (!landingPage.getPlatform().equals(deepLinkVo.getPlatform())) {
            throw new ServiceException("must be same platform");
        }

        List<DeepLink> deepLinkWithUtmList = this.deepLinkMapper.selectByUtm(deepLinkVo.getUtmSource(), deepLinkVo.getUtmMedium(), deepLinkVo.getUtmCampaign(), deepLinkVo.getUtmContent());
        if (!CollectionUtils.isEmpty(deepLinkWithUtmList) && deepLinkWithUtmList.size() >= this.configParam.getUtmSameTimes()) {
            throw new ServiceException("A utm can be associated with " + this.configParam.getUtmSameTimes() + " deepLink at most.");
        }
        DeepLink deepLink = DeepLinkMessageConvertor.voToModel(deepLinkVo);

       this.deepLinkBusinessHandler.generateLink(deepLink, landingPage, deepLinkVo.getParamMap());

        super.save(deepLink);
    }

    @Override
    public void updateUTMByVo(DeepLinkVo deepLinkVo) {
        if (null == deepLinkVo.getId()) {
            throw new ServiceException("id is required.");
        }

        List<DeepLink> deepLinkWithUtmList = this.deepLinkMapper.selectByUtm(deepLinkVo.getUtmSource(), deepLinkVo.getUtmMedium(), deepLinkVo.getUtmCampaign(), deepLinkVo.getUtmContent());
        if (!CollectionUtils.isEmpty(deepLinkWithUtmList) && deepLinkWithUtmList.size() >= this.configParam.getUtmSameTimes()) {
            throw new ServiceException("A utm can be associated with " + this.configParam.getUtmSameTimes() + " deepLink at most.");
        }

        DeepLink updateDeepLink = DeepLinkMessageConvertor.voToModelForUpdate(deepLinkVo, true);

        DeepLink deepLink = this.deepLinkMapper.selectByPrimaryKey(deepLinkVo.getId());
        deepLink.setUtmSource(deepLinkVo.getUtmSource());
        deepLink.setUtmMedium(deepLinkVo.getUtmMedium());
        deepLink.setUtmCampaign(deepLinkVo.getUtmCampaign());
        deepLink.setUtmContent(deepLinkVo.getUtmContent());
        deepLink.setName(updateDeepLink.getName());

        LandingPage landingPage = this.landingPageService.findById(deepLink.getLandingPageId());
        this.deepLinkBusinessHandler.updateUTM(deepLink, landingPage);

        deepLink.setUpdateTime(new Date());
        this.deepLinkMapper.updateByPrimaryKeySelective(deepLink);

        this.applicationContext.publishEvent(new DeepLinkUpdateEvent(deepLink.getLink(), deepLink.getId()));
    }

    @Override
    public void expireById(Integer id) {
        if (null == id) {
            throw new ServiceException("invalid id.");
        }

        DeepLink deepLink = this.deepLinkMapper.selectByPrimaryKey(id);
        if (null == deepLink || StatusEnum.OFF.getCode().equals(deepLink.getStatus())) {
            return;
        }

        DeepLink newDeepLink = new DeepLink();
        newDeepLink.setId(deepLink.getId());
        newDeepLink.setStatus(StatusEnum.OFF.getCode());
        newDeepLink.setEditor(ThreadUserInfo.getUserInfo().getUsername());
        newDeepLink.setExpiredTime(new Date());
        this.deepLinkMapper.updateByPrimaryKeySelective(newDeepLink);
        logger.info("expire deepLink {}", id);
    }

    @Override
    public void activateById(Integer id) {
        if (null == id) {
            throw new ServiceException("invalid id.");
        }

        DeepLink deepLink = this.deepLinkMapper.selectByPrimaryKey(id);
        if (null == deepLink || StatusEnum.ACTIVE.getCode().equals(deepLink.getStatus())) {
            return;
        }

        deepLink.setId(deepLink.getId());
        deepLink.setStatus(StatusEnum.ACTIVE.getCode());
        deepLink.setEditor(ThreadUserInfo.getUserInfo().getUsername());
        deepLink.setExpiredTime(null);
        this.deepLinkMapper.updateByPrimaryKey(deepLink);
        logger.info("expire deepLink {}", id);
    }

    @Override
    public List<DeepLinkVo> findByLandingPage(Integer landingPageId) {
        LandingPage landingPage = this.landingPageService.findById(landingPageId);
        if (null == landingPageId || null == landingPage) {
            throw new ServiceException("invalid landing page.");
        }

        LandingPageVo landingPageVo = LandingPageMessageConvertor.modelToVo(landingPage);

        List<DeepLink> deepLinkList = this.deepLinkMapper.selectByLandingPage(landingPageId);
        List<DeepLinkVo> voList = deepLinkList.stream()
                .map(DeepLinkMessageConvertor::modelToVo)
                .map(deepLinkVo -> {
                    deepLinkVo.setLandingPageVo(landingPageVo);
                    return deepLinkVo;
                })
                .collect(Collectors.toList());
        return voList;
    }

    @Override
    public List<DeepLinkVo> findAllVo() {
        Map<Integer, LandingPageVo> landingPageVoMap = this.landingPageService.findAllVoWithMap();
        return super.findAll().stream()
                .map(DeepLinkMessageConvertor::modelToVo)
                .map(deepLinkVo -> {
                    deepLinkVo.setLandingPageVo(landingPageVoMap.get(deepLinkVo.getLandingPageId()));
                    return deepLinkVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DeepLinkVo> findVoByPlatform(PlatformEnum platformEnum) {
        if (null == platformEnum) {
            throw new ServiceException("platform is null.");
        }

        Map<Integer, LandingPageVo> landingPageVoMap = this.landingPageService.findAllVoWithMap();
        return this.deepLinkMapper.selectByPlatform(platformEnum.getCode()).stream()
                .map(DeepLinkMessageConvertor::modelToVo)
                .map(deepLinkVo -> {
                    deepLinkVo.setLandingPageVo(landingPageVoMap.get(deepLinkVo.getLandingPageId()));
                    return deepLinkVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public DeepLinkVo findVoById(Integer id) {
        if (null == id) {
            throw new ServiceException("invalid param.");
        }
        DeepLinkVo vo = DeepLinkMessageConvertor.modelToVo(super.findById(id));
        vo.setLandingPageVo(LandingPageMessageConvertor.modelToVo(this.landingPageService.findById(vo.getLandingPageId())));
        return vo;
    }
}
