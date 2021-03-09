package com.gdtc.deeplink.manager.service;
import com.gdtc.deeplink.manager.model.ShortLink;
import com.gdtc.deeplink.manager.core.Service;
import com.gdtc.deeplink.manager.vo.OriginalLinkVo;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;

import java.util.List;


/**
 * Created by GDTC on 2020/11/30.
 */
public interface ShortLinkService extends Service<ShortLink> {
    /**
     * 生成shortLink
     * 如果originalId存在，那么该接口是幂等的，
     * 如果originalId不存在，非幂等
     * @param vo
     * @param originalLinkVo
     */
    Integer saveByVo(ShortLinkVo vo, OriginalLinkVo originalLinkVo);

    void updateByVo(ShortLinkVo vo);

    ShortLinkVo findVoById(Integer id);

//    List<ShortLinkVo> findVoByDeepLink(Integer deepLinkId);

    Integer findIdByLinkTypeAndOriginalId(String linkType, Integer originalId);

    List<Integer> findIdByLinkType(String linkType);


    List<ShortLinkVo> listAllVo();

//    void generateForDeepLink(ShortLinkVo vo);
//
//    void generateForGDTOneLink(ShortLinkVo vo);
}
