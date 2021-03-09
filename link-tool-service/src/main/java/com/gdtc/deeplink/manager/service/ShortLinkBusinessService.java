package com.gdtc.deeplink.manager.service;

import com.gdtc.deeplink.manager.vo.DeepLinkVo;
import com.gdtc.deeplink.manager.vo.GDTOneLinkVo;
import com.gdtc.deeplink.manager.vo.ShortLinkAPIVo;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;

import java.util.List;

public interface ShortLinkBusinessService {
    void generateForDeepLink(Integer deepLinkId, ShortLinkVo shortLinkVo);


    void generateForGDTOneLink(Integer gdtOneLinkId, ShortLinkVo shortLinkVo);

    ShortLinkVo generateForCommon(ShortLinkAPIVo shortLinkAPIVo);

    Integer findShortLinkIdByOriginalIdForDeepLink(Integer originalId);

    Integer findShortLinkIdByOriginalIdForOneLink(Integer originalId);

    List<DeepLinkVo> listDeepLinkVoWithoutRelation();

    List<GDTOneLinkVo> listGDTOneLinkVoWithoutRelation();

    List<DeepLinkVo> listAllDeepLinkVo();

    List<GDTOneLinkVo> listAllGDTOneLinkVo();

    void updateShortLinkByVo(ShortLinkVo shortLinkVo);
}
