package com.gdtc.deeplink.manager.service.impl;

import com.gdtc.deeplink.manager.configuration.ConfigParam;
import com.gdtc.deeplink.manager.convertor.LandingPageMessageConvertor;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.dao.DeepLinkMapper;
import com.gdtc.deeplink.manager.dao.LandingPageMapper;
import com.gdtc.deeplink.manager.handler.LandingPageBusinessHandler;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.service.LandingPageService;
import com.gdtc.deeplink.manager.core.AbstractService;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.vo.LandingPageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by GDTC on 2020/11/30.
 */
@Service
@Transactional
public class LandingPageServiceImpl extends AbstractService<LandingPage> implements LandingPageService {

    @Resource
    private LandingPageMapper landingPageMapper;
    @Resource
    private DeepLinkMapper deepLinkMapper;
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private LandingPageBusinessHandler langdingPageBusinessHandler;

    @Override
    public void saveByVo(LandingPageVo vo) {
        if (StringUtils.isAnyBlank(vo.getModule(), vo.getPlatform())) {
            throw new ServiceException("module, platform is required.");
        }

        LandingPage model = LandingPageMessageConvertor.voToModel(vo);

        model.setNative(this.configParam.isNativeScheme(model.getSchemeName()));

        this.langdingPageBusinessHandler.validateLandingPage(model);

        // check whether template is valid.
        if (!model.isValidPathTemplate()) {
            throw new ServiceException("invalid path template.");
        }

        // check whether path template is duplicate.
        LandingPage queryLandingPage = new LandingPage();
        queryLandingPage.setModule(model.getModule());
        queryLandingPage.setSchemeName(model.getSchemeName());
        queryLandingPage.setPathTemplate(model.getPathTemplate());
        int samePathCount = this.landingPageMapper.selectCount(queryLandingPage);
        if (samePathCount > 0) {
            throw new ServiceException("path existing.");
        }

        // check whether name is duplicate
        queryLandingPage = new LandingPage();
        queryLandingPage.setName(model.getName());
        int sameNameCount = this.landingPageMapper.selectCount(queryLandingPage);
        if (sameNameCount > 0) {
            throw new ServiceException("name existing.");
        }

        super.save(model);
    }

    @Override
    public LandingPageVo findVoById(Integer id) {
        if (null == id) {
            throw new ServiceException("id is null.");
        }
        return LandingPageMessageConvertor.modelToVo(super.findById(id));
    }

    @Override
    public List<LandingPageVo> findAllVo() {
        return super.findAll().stream().map(LandingPageMessageConvertor::modelToVo).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, LandingPageVo> findAllVoWithMap() {
        Map<Integer, LandingPageVo> resultMap = new HashMap<>();
        super.findAll().forEach(landingPage -> {
                    LandingPageVo vo = LandingPageMessageConvertor.modelToVo(landingPage);
                    resultMap.put(landingPage.getId(), vo);
                });
        return resultMap;
    }

    @Override
    public List<LandingPageVo> findAllVoByPlatform(PlatformEnum platformEnum) {
        if (null == platformEnum) {
            throw new ServiceException("platform is null.");
        }
        return this.landingPageMapper.selectByPlatform(platformEnum.getCode()).stream().map(LandingPageMessageConvertor::modelToVo).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        if (null == id) {
            return;
        }

        int referDeepLinkCount = this.deepLinkMapper.countByLandingPage(id);
        if (referDeepLinkCount > 0) {
            throw new ServiceException("existing deeplink refer this landing page.");
        }
        super.deleteById(id);
    }
}
