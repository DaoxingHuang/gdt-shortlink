package com.gdtc.deeplink.manager.service;

import com.gdtc.deeplink.manager.core.Service;
import com.gdtc.deeplink.manager.model.GDTOneLink;
import com.gdtc.deeplink.manager.vo.GDTOneLinkVo;

import java.util.List;


/**
 * Created by GDTC on 2020/11/30.
 */
public interface GDTOneLinkService extends Service<GDTOneLink> {
    void saveByVo(GDTOneLinkVo gdtOneLinkVo);

    void updateUTMByVo(GDTOneLinkVo gdtOneLinkVo);

    void expireById(Integer id);

    void activateById(Integer id);

    List<GDTOneLinkVo> findAllVo();

    GDTOneLinkVo findVoById(Integer id);
}
