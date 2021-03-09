package com.gdtc.deeplink.manager.service;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.core.Service;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.vo.DeepLinkVo;

import java.util.List;


/**
 * Created by GDTC on 2020/11/30.
 */
public interface DeepLinkService extends Service<DeepLink> {
    void saveByVo(DeepLinkVo deepLinkVo);

    void updateUTMByVo(DeepLinkVo deepLinkVo);

    void expireById(Integer id);

    void activateById(Integer id);

    /**
     * @param landingPageId
     * @return
     */
    List<DeepLinkVo> findByLandingPage(Integer landingPageId);


    List<DeepLinkVo> findAllVo();

    List<DeepLinkVo> findVoByPlatform(PlatformEnum platformEnum);

    DeepLinkVo findVoById(Integer id);
}
