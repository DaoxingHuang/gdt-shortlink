package com.gdtc.deeplink.manager.service;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.core.Service;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.vo.LandingPageVo;

import java.util.List;
import java.util.Map;


/**
 * Created by GDTC on 2020/11/30.
 */
public interface LandingPageService extends Service<LandingPage> {
    void saveByVo(LandingPageVo vo);

    LandingPageVo findVoById(Integer id);

    List<LandingPageVo> findAllVo();

    Map<Integer, LandingPageVo> findAllVoWithMap();

    List<LandingPageVo> findAllVoByPlatform(PlatformEnum platformEnum);
}
