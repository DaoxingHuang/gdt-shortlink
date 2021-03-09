package com.gdtc.deeplink.manager.convertor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import com.gdtc.deeplink.manager.model.GDTOneLink;
import com.gdtc.deeplink.manager.utils.Constants;
import com.gdtc.deeplink.manager.utils.StatusEnum;
import com.gdtc.deeplink.manager.utils.UTMUtils;
import com.gdtc.deeplink.manager.vo.GDTOneLinkVo;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GDTOneLinkMessageConvertor {

    private static final SimpleDateFormat dateHourFormat = new SimpleDateFormat("MMddHHmm");

    public static GDTOneLink voToModel(GDTOneLinkVo vo) {
        if (null == vo) {
            throw new ServiceException("invalid vo.");
        }

        UTMUtils.checkUtmParam(vo);

        GDTOneLink model = new GDTOneLink();
        BeanUtils.copyProperties(vo, model);
        if (StringUtils.isBlank(model.getEditor())) {
            model.setEditor(model.getCreator());
        }

        model.setStatus(StatusEnum.ACTIVE.getCode());
        if (StringUtils.isNotBlank(vo.getStatus())) {
            model.setStatus(StatusEnum.valueOf(vo.getStatus()).getCode());
        }

        if (!CollectionUtils.isEmpty(vo.getCustomizeParamList())) {
            model.setParam(JSON.toJSONString(vo.getCustomizeParamList()));
        }
        model.setName(autoGenerateName(vo));
        model.setCreator(ThreadUserInfo.getUserInfo().getUsername());
        model.setEditor(ThreadUserInfo.getUserInfo().getUsername());
        return model;
    }


    public static GDTOneLink voToModelForUpdate(GDTOneLinkVo vo, boolean checkUtm) {
        if (null == vo) {
            throw new ServiceException("invalid vo.");
        }

        GDTOneLink model = new GDTOneLink();
        BeanUtils.copyProperties(vo, model);

        if (checkUtm) {
            UTMUtils.checkUtmParam(vo);
            model.setName(autoGenerateName(vo));
        }

        if (null != vo.getStatus()) {
            model.setStatus(StatusEnum.valueOf(vo.getStatus()).getCode());
        }

        if (!CollectionUtils.isEmpty(vo.getCustomizeParamList())) {
            model.setParam(JSON.toJSONString(vo.getCustomizeParamList()));
        }

        model.setCreator(null);
        model.setEditor(ThreadUserInfo.getUserInfo().getUsername());
        return model;
    }

    public static GDTOneLinkVo modelToVo(GDTOneLink model) {
        if (null == model) {
            throw new ServiceException("invalid model.");
        }

        GDTOneLinkVo vo = new GDTOneLinkVo();
        BeanUtils.copyProperties(model, vo);

        if (StringUtils.isNotBlank(model.getParam())) {
            vo.setCustomizeParamList(
                JSON.parseArray(model.getParam(), Map.class).stream().map(item -> {
                    HashMap<String, String> tempParamMap = Maps.newLinkedHashMap();
                    tempParamMap.put("key", String.valueOf(item.get("key")));
                    tempParamMap.put("value", String.valueOf(item.get("value")));
                    return tempParamMap;
                }).collect(Collectors.toList())
            );
        }
        vo.setStatus(StatusEnum.codeOf(model.getStatus()).name());
        vo.setUtm(model.buildUtm());
        return vo;
    }

    private synchronized static String autoGenerateName(GDTOneLinkVo vo) {
        String utmParam = String.join(Constants.UTM_CONNECT_FLAG, Arrays.asList(vo.getUtmSource(), vo.getUtmMedium(), vo.getUtmCampaign(), vo.getUtmContent()));
        return new StringBuilder(utmParam).append(Constants.UTM_CONNECT_FLAG).append(dateHourFormat.format(new Date())).toString();
    }
}
