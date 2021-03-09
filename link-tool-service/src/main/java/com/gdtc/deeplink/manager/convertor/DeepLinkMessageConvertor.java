package com.gdtc.deeplink.manager.convertor;

import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.utils.*;
import com.gdtc.deeplink.manager.vo.DeepLinkVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

public class DeepLinkMessageConvertor {


    public static DeepLink voToModel(DeepLinkVo vo) {
        if (null == vo) {
            throw new ServiceException("invalid vo.");
        }

        DeepLink model = new DeepLink();
        BeanUtils.copyProperties(vo, model);
        model.setPlatform(PlatformEnum.valueOf(vo.getPlatform()).getCode());
        if (StringUtils.isBlank(model.getEditor())) {
            model.setEditor(model.getCreator());
        }

        model.setStatus(StatusEnum.ACTIVE.getCode());

        if (StringUtils.isNotBlank(vo.getStatus())) {
            model.setStatus(StatusEnum.valueOf(vo.getStatus()).getCode());
        }

        UTMUtils.checkUtmParam(vo);

        model.setName(autoGenerateName(vo));
        model.setCreator(ThreadUserInfo.getUserInfo().getUsername());
        model.setEditor(ThreadUserInfo.getUserInfo().getUsername());
        return model;
    }

    public static DeepLink voToModelForUpdate(DeepLinkVo vo, boolean checkUtm) {
        if (null == vo) {
            throw new ServiceException("invalid vo.");
        }

        DeepLink model = new DeepLink();
        BeanUtils.copyProperties(vo, model);
        if (null != vo.getPlatform()) {
            model.setPlatform(PlatformEnum.valueOf(vo.getPlatform()).getCode());
        }

        if (null != vo.getStatus()) {
            model.setStatus(StatusEnum.valueOf(vo.getStatus()).getCode());
        }

        if (checkUtm) {
            UTMUtils.checkUtmParam(vo);
            model.setName(autoGenerateName(vo));
        }

        model.setCreator(null);
        model.setEditor(ThreadUserInfo.getUserInfo().getUsername());
        return model;
    }

    public static DeepLinkVo modelToVo(DeepLink model) {
        if (null == model) {
            throw new ServiceException("invalid model.");
        }

        DeepLinkVo vo = new DeepLinkVo();
        BeanUtils.copyProperties(model, vo);
        vo.setPlatform(PlatformEnum.codeOf(model.getPlatform()).name());
        vo.setStatus(StatusEnum.codeOf(model.getStatus()).name());
        vo.setUtm(model.buildUtm());
        return vo;
    }

    private synchronized static String autoGenerateName(DeepLinkVo vo) {
        String utmParam = String.join(Constants.UTM_CONNECT_FLAG, Arrays.asList(vo.getUtmSource(), vo.getUtmMedium(), vo.getUtmCampaign(), vo.getUtmContent()));
        return new StringBuilder(utmParam).append(Constants.UTM_CONNECT_FLAG).append(DateUtils.getDateHour(new Date())).toString();
    }
}
