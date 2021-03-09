package com.gdtc.deeplink.manager.api;

import com.gdtc.deeplink.manager.core.ResultGenerator;
import com.gdtc.deeplink.manager.filter.SSOUserInfo;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import com.gdtc.deeplink.manager.service.ShortLinkBusinessService;
import com.gdtc.deeplink.manager.vo.ShortLinkAPIVo;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;
import com.gdtc.link.api.ShortLinkAPIService;
import com.gdtc.link.api.core.Result;
import com.gdtc.link.api.vo.ShortLinkResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortLinkAPIServiceImpl implements ShortLinkAPIService {

    @Autowired
    private ShortLinkBusinessService shortLinkBusinessService;

    @Override
    public Result<ShortLinkResultVo> createForCommon(String originalLink, long expiredTime, String creator) {
        if (StringUtils.isAnyBlank(originalLink, creator) || expiredTime <= 0) {
            return ResultGenerator.genFailResult("invalid params");
        }

        SSOUserInfo ssoUserInfo = new SSOUserInfo();
        ssoUserInfo.setUsername(creator);
        ssoUserInfo.setChineseName(creator);

        ThreadUserInfo.setUserInfo(ssoUserInfo);
        try {
            ShortLinkAPIVo shortLinkAPIVo = new ShortLinkAPIVo();
            shortLinkAPIVo.setCreator(creator);
            shortLinkAPIVo.setOriginalLink(originalLink);
            shortLinkAPIVo.setExpiredTime(expiredTime);
            ShortLinkVo shortLinkVo = this.shortLinkBusinessService.generateForCommon(shortLinkAPIVo);

            ShortLinkResultVo shortLinkResultVo = this.convert(shortLinkVo);
            shortLinkResultVo.setOriginalLink(originalLink);
            return ResultGenerator.genSuccessResult(shortLinkResultVo);
        } finally {
            ThreadUserInfo.clear();
        }
    }

    private ShortLinkResultVo convert(ShortLinkVo shortLinkVo) {
        ShortLinkResultVo resultVo = new ShortLinkResultVo();
        resultVo.setCode(shortLinkVo.getCode());
        resultVo.setCreator(shortLinkVo.getCreator());
        resultVo.setExpiredTime(shortLinkVo.getExpiredTime().getTime());
        resultVo.setLink(shortLinkVo.getLink());
        resultVo.setLinkType(shortLinkVo.getLinkType());
        return resultVo;
    }
}
