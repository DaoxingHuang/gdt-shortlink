package com.gdtc.deeplink.manager.convertor;

import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.vo.LandingPageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

public class LandingPageMessageConvertor {
    public static LandingPage voToModel(LandingPageVo vo) {
        if (null == vo) {
            throw new ServiceException("invalid vo.");
        }

        LandingPage model = new LandingPage();

        BeanUtils.copyProperties(vo, model);
        model.setPlatform(PlatformEnum.valueOf(vo.getPlatform()).getCode());

        model.setCreator(ThreadUserInfo.getUserInfo().getUsername());
        model.setEditor(ThreadUserInfo.getUserInfo().getUsername());
        return model;
    }

    public static LandingPageVo modelToVo(LandingPage model) {
        if (null == model) {
            throw new ServiceException("invalid model.");
        }

        LandingPageVo vo = new LandingPageVo();
        BeanUtils.copyProperties(model, vo);
        vo.setPlatform(PlatformEnum.codeOf(model.getPlatform()).name());
        if (vo.getPlatform().equals(PlatformEnum.GH.name())) {
            vo.setSchemeName(null);
        }
        vo.setParamList(model.parseParam());
        vo.setNative(model.getIsNative());

        return vo;
    }
}
