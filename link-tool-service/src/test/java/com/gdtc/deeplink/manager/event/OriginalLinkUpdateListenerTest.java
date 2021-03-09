package com.gdtc.deeplink.manager.event;

import com.gdtc.deeplink.manager.BaseTest;
import com.gdtc.deeplink.manager.model.ShortLink;
import com.gdtc.deeplink.manager.model.ShortLinkRelation;
import com.gdtc.deeplink.manager.service.*;
import com.gdtc.deeplink.manager.utils.ShortLinkInfoEnum;
import com.gdtc.deeplink.manager.vo.DeepLinkVo;
import com.gdtc.deeplink.manager.vo.GDTOneLinkVo;
import com.gdtc.deeplink.manager.vo.OriginalLinkVo;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class OriginalLinkUpdateListenerTest extends BaseTest {
    private static final Integer DEFAULT_SAA_LANDING_PAGE_ID = 4;

    @Autowired
    private ShortLinkBusinessService shortLinkBusinessService;
    @Autowired
    private ShortLinkService shortLinkService;
    @Autowired
    private DeepLinkService deepLinkService;
    @Autowired
    private GDTOneLinkService gdtOneLinkService;
    @Autowired
    private ShortLinkRelationService shortLinkRelationService;


    @Test
    public void testUpdateGDTOneLink() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForGDTOneLink();
        Integer oneLinkId = shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId();
        this.shortLinkBusinessService.generateForGDTOneLink(oneLinkId, shortLinkVo);

        GDTOneLinkVo vo = this.gdtOneLinkService.findVoById(oneLinkId);

        List<Map<String, String>> customizeParamList = new ArrayList<>();
        {
            Map<String, String> tempParamMap = new HashMap<>();
            tempParamMap.put("key", "key2");
            tempParamMap.put("value", "value2");
            customizeParamList.add(tempParamMap);
        }
        {
            Map<String, String> tempParamMap = new HashMap<>();
            tempParamMap.put("key", "key3");
            tempParamMap.put("value", "value3");
            customizeParamList.add(tempParamMap);
        }
        vo.setCustomizeParamList(customizeParamList);
        this.gdtOneLinkService.updateUTMByVo(vo);

        ShortLinkRelation relation = this.shortLinkRelationService.findByLinkTypeAndOriginalId(ShortLinkInfoEnum.GDT_ONE_LINK.getLinkType(), oneLinkId);

        String expectedOriginalLink = "https://gooddoctor.onelink.com/Cmiw/?pid=intransit&c=shihlin&scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult&key2=value2&key3=value3";
        Assert.assertEquals(expectedOriginalLink, relation.getOriginalLink());
    }



    @Test
    public void testUpdateDeepLink() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForDeepLink();
        Integer deepLinkId = shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId();
        this.shortLinkBusinessService.generateForDeepLink(deepLinkId, shortLinkVo);

        DeepLinkVo updateVo = new DeepLinkVo();
        updateVo.setId(deepLinkId);
        updateVo.setUtmSource("utmsource1");
        updateVo.setUtmMedium("utmmedium1");
        updateVo.setUtmCampaign("utmcampaign1");
        updateVo.setUtmContent("utmcontent1");

        this.deepLinkService.updateUTMByVo(updateVo);

        ShortLinkRelation relation = this.shortLinkRelationService.findByLinkTypeAndOriginalId(ShortLinkInfoEnum.DEEPLINK.getLinkType(), deepLinkId);
        String expectedOriginalLink = "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522utmsource1_utmmedium1_utmcampaign1_utmcontent1%2522%257D";
        Assert.assertEquals(expectedOriginalLink, relation.getOriginalLink());
    }

    private ShortLinkVo generateDefaultShortLinkVoForDeepLink() {
        String[] utmParamArray = new String[]{"intransit", "acquisition", "shihlin", "consult"};

        DeepLinkVo defaultVo = new DeepLinkVo();
        defaultVo.setLandingPageId(DEFAULT_SAA_LANDING_PAGE_ID);
        defaultVo.setStatus("ACTIVE");
        defaultVo.setPlatform("SAA");

        defaultVo.setUtmSource(utmParamArray[0]);
        defaultVo.setUtmMedium(utmParamArray[1]);
        defaultVo.setUtmCampaign(utmParamArray[2]);
        defaultVo.setUtmContent(utmParamArray[3]);
        defaultVo.setName("test-name-" + System.currentTimeMillis());

        this.deepLinkService.saveByVo(defaultVo);

        Integer deepLinkId = this.deepLinkService.findAll().get(0).getId();

        ShortLinkVo shortLinkVo = new ShortLinkVo();
        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        shortLinkVo.setLinkPrefix(ShortLinkInfoEnum.DEEPLINK.getPrefix());

        OriginalLinkVo originalLinkVo = new OriginalLinkVo();
        originalLinkVo.setOriginalId(deepLinkId);
        shortLinkVo.setOriginalLinkVoList(Arrays.asList(originalLinkVo));
        shortLinkVo.setName("test-short-name-deep");
        shortLinkVo.setExpiredTime(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000));
        shortLinkVo.setCreateTime(new Date());
        shortLinkVo.setUpdateTime(new Date());
        return shortLinkVo;
    }

    private ShortLinkVo generateDefaultShortLinkVoForGDTOneLink() {
        String[] utmParamArray = new String[]{"intransit", "acquisition", "shihlin", "consult"};

        GDTOneLinkVo defaultVo = new GDTOneLinkVo();
        defaultVo.setLandingPageId(DEFAULT_SAA_LANDING_PAGE_ID);
        defaultVo.setStatus("ACTIVE");

        defaultVo.setUtmSource(utmParamArray[0]);
        defaultVo.setUtmMedium(utmParamArray[1]);
        defaultVo.setUtmCampaign(utmParamArray[2]);
        defaultVo.setUtmContent(utmParamArray[3]);
        defaultVo.setName("test-name-" + System.currentTimeMillis());

        this.gdtOneLinkService.saveByVo(defaultVo);

        Integer gdtOneLinkId = this.gdtOneLinkService.findAll().get(0).getId();

        ShortLinkVo shortLinkVo = new ShortLinkVo();
        shortLinkVo.setLinkType(ShortLinkInfoEnum.GDT_ONE_LINK.getLinkType());
        shortLinkVo.setLinkPrefix(ShortLinkInfoEnum.GDT_ONE_LINK.getPrefix());

        OriginalLinkVo originalLinkVo = new OriginalLinkVo();
        originalLinkVo.setOriginalId(gdtOneLinkId);
        shortLinkVo.setOriginalLinkVoList(Arrays.asList(originalLinkVo));
        shortLinkVo.setName("test-short-name-one");
        shortLinkVo.setExpiredTime(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000));
        shortLinkVo.setCreateTime(new Date());
        shortLinkVo.setUpdateTime(new Date());
        return shortLinkVo;
    }
}
