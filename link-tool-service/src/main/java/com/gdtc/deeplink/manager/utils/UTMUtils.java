package com.gdtc.deeplink.manager.utils;

import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.vo.UTMVo;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

public class UTMUtils {

    private static final Pattern utmParamPattern = Pattern.compile("^[0-9a-zA-Z]+$");

    public static void checkUtmParam(UTMVo vo) {
        Arrays.asList(vo.getUtmSource(), vo.getUtmMedium(), vo.getUtmCampaign(), vo.getUtmContent())
                .forEach(item -> {
                    if (StringUtils.isBlank(item) || !utmParamPattern.matcher(item).find()) {
                        throw new ServiceException("invalid utm.");
                    }
                });
    }
}
