package com.gdtc.deeplink.manager.convertor;

import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import com.gdtc.deeplink.manager.model.ShortLink;
import com.gdtc.deeplink.manager.model.ShortLink;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.utils.StatusEnum;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ShortLinkMessageConvertor {

    public static ShortLink voToModel(ShortLinkVo vo) {
        if (null == vo) {
            throw new ServiceException("invalid vo.");
        }

        ShortLink model = new ShortLink();
        BeanUtils.copyProperties(vo, model);

        model.setStatus(StatusEnum.ACTIVE.getCode());

        if (StringUtils.isNotBlank(vo.getStatus())) {
            model.setStatus(StatusEnum.valueOf(vo.getStatus()).getCode());
        }

        if (StringUtils.isBlank(model.getEditor())) {
            model.setEditor(model.getCreator());
        }

        if (StringUtils.isAnyBlank(vo.getCreator(), vo.getEditor())) {
            model.setCreator(ThreadUserInfo.getUserInfo().getUsername());
            model.setEditor(ThreadUserInfo.getUserInfo().getUsername());
        }
        return model;
    }

    public static ShortLinkVo modelToVo(ShortLink model) {
        if (null == model) {
            throw new ServiceException("invalid model.");
        }

        ShortLinkVo vo = new ShortLinkVo();
        BeanUtils.copyProperties(model, vo);
        vo.setStatus(StatusEnum.codeOf(model.getStatus()).name());
        return vo;
    }
}
