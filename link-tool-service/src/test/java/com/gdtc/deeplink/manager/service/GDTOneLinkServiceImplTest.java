package com.gdtc.deeplink.manager.service;

import com.gdtc.deeplink.manager.BaseTest;
import com.gdtc.deeplink.manager.configuration.ConfigParam;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.model.GDTOneLink;
import com.gdtc.deeplink.manager.utils.StatusEnum;
import com.gdtc.deeplink.manager.vo.GDTOneLinkVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GDTOneLinkServiceImplTest extends BaseTest {
    private static final Integer DEFAULT_SAA_LANDING_PAGE_ID = 4;

    @Autowired
    private GDTOneLinkService gdtOneLinkService;

    @Autowired
    private ConfigParam configParam;


    @Test
    public void testSaveByVoForSAANativeWithNoneParam() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        this.gdtOneLinkService.saveByVo(vo);

        String expectedLink = "https://gooddoctor.onelink.com/Cmiw/?pid=intransit&c=shihlin&scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult&key1=value1&key2=value2&key3=value3";

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(expectedLink, gdtOneLink.getLink());
    }

    @Test
    public void testSaveByVoForSAANativeWithParam() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setLandingPageId(6);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("param1", "abc");
        paramMap.put("param2", "def");
        paramMap.put("param3", "jhi");
        vo.setParamMap(paramMap);

        this.gdtOneLinkService.saveByVo(vo);

        String expectedLink = "https://gooddoctor.onelink.com/Cmiw/?pid=intransit&c=shihlin&scheme=global_show_tab_healthMall%3Fcontent%3D%257B%2522param1%2522%253A%2522abc%2522%252C%2522param2%2522%253A%2522def%2522%252C%2522param3%2522%253A%2522jhi%2522%252C%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult&key1=value1&key2=value2&key3=value3";

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(expectedLink, gdtOneLink.getLink());
    }

    @Test
    public void testSaveByVoForSAANativeWithParamAndNoneCustomizeParam() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setLandingPageId(6);
        vo.setCustomizeParamList(new ArrayList<>());
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("param1", "abc");
        paramMap.put("param2", "def");
        paramMap.put("param3", "jhi");
        vo.setParamMap(paramMap);

        this.gdtOneLinkService.saveByVo(vo);

        String expectedLink = "https://gooddoctor.onelink.com/Cmiw/?pid=intransit&c=shihlin&scheme=global_show_tab_healthMall%3Fcontent%3D%257B%2522param1%2522%253A%2522abc%2522%252C%2522param2%2522%253A%2522def%2522%252C%2522param3%2522%253A%2522jhi%2522%252C%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult";

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(expectedLink, gdtOneLink.getLink());
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoForSAANativeWithErrorParam() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setLandingPageId(6);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("param1", "abc");
        paramMap.put("param2", "def");
        vo.setParamMap(paramMap);

        this.gdtOneLinkService.saveByVo(vo);

        Assert.fail();
    }

    @Test
    public void testSaveByVoForSAANativeWithNoneParamAndFacebookSource() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setUtmSource("facebook");
        this.gdtOneLinkService.saveByVo(vo);

        String expectedLink = "https://gooddoctor.onelink.com/Cmiw/?pid=facebookfilter&c=shihlin&scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522facebook_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult&key1=value1&key2=value2&key3=value3";

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(expectedLink, gdtOneLink.getLink());
    }

    @Test
    public void testSaveByVoForSAAH5WithParam() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setLandingPageId(5);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptId", "10092514");
        paramMap.put("entrance", "FASTCONSULT.RA");
        vo.setParamMap(paramMap);

        this.gdtOneLinkService.saveByVo(vo);

        String expectedLink = "https://gooddoctor.onelink.com/Cmiw/?pid=intransit&c=shihlin&scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult&key1=value1&key2=value2&key3=value3";

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(expectedLink, gdtOneLink.getLink());
    }


    @Test
    public void testSaveByVoForSAAH5WithParamAndNoneCustomizeParam() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setLandingPageId(5);
        vo.setCustomizeParamList(new ArrayList<>());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptId", "10092514");
        paramMap.put("entrance", "FASTCONSULT.RA");
        vo.setParamMap(paramMap);

        this.gdtOneLinkService.saveByVo(vo);

        String expectedLink = "https://gooddoctor.onelink.com/Cmiw/?pid=intransit&c=shihlin&scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult";

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(expectedLink, gdtOneLink.getLink());
    }

    @Test
    public void testSaveByVoForSAAH5WithParamAndFacebookSource() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setUtmSource("fb");
        vo.setLandingPageId(5);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptId", "10092514");
        paramMap.put("entrance", "FASTCONSULT.RA");
        vo.setParamMap(paramMap);

        this.gdtOneLinkService.saveByVo(vo);

        String expectedLink = "https://gooddoctor.onelink.com/Cmiw/?pid=facebookfilter&c=shihlin&scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522fb_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult&key1=value1&key2=value2&key3=value3";

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(expectedLink, gdtOneLink.getLink());
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoForSAAH5WithErrorParam() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setLandingPageId(5);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptId", "10092514");
        vo.setParamMap(paramMap);

        this.gdtOneLinkService.saveByVo(vo);
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoForMaxUtmCount() {
        for (int i = 0; i < this.configParam.getUtmSameTimes(); i++) {
            try {
                GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
                this.gdtOneLinkService.saveByVo(vo);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }

        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        this.gdtOneLinkService.saveByVo(vo);

        Assert.fail();
    }


    @Test
    public void testUpdateByVoForSAANative() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        this.gdtOneLinkService.saveByVo(vo);

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        vo.setId(gdtOneLink.getId());

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
        {
            Map<String, String> tempParamMap = new HashMap<>();
            tempParamMap.put("key", "key4");
            tempParamMap.put("value", "value4");
            customizeParamList.add(tempParamMap);
        }
        vo.setCustomizeParamList(customizeParamList);

        this.gdtOneLinkService.updateUTMByVo(vo);

        String expectedLink = "https://gooddoctor.onelink.com/Cmiw/?pid=intransit&c=shihlin&scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult&key2=value2&key3=value3&key4=value4";

        gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(expectedLink, gdtOneLink.getLink());
    }

    @Test
    public void testUpdateByVoForSAAH5() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setLandingPageId(5);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptId", "10092514");
        paramMap.put("entrance", "FASTCONSULT.RA");
        vo.setParamMap(paramMap);

        this.gdtOneLinkService.saveByVo(vo);

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        vo.setId(gdtOneLink.getId());

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
        {
            Map<String, String> tempParamMap = new HashMap<>();
            tempParamMap.put("key", "key4");
            tempParamMap.put("value", "value4");
            customizeParamList.add(tempParamMap);
        }
        vo.setCustomizeParamList(customizeParamList);

        this.gdtOneLinkService.updateUTMByVo(vo);

        String expectedLink = "https://gooddoctor.onelink.com/Cmiw/?pid=intransit&c=shihlin&scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult&key2=value2&key3=value3&key4=value4";
        gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(expectedLink, gdtOneLink.getLink());
    }

    @Test
    public void testUpdateByVoForSAAH5WithUpdateLandingPageParam() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setLandingPageId(5);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptId", "10092514");
        paramMap.put("entrance", "FASTCONSULT.RA");
        vo.setParamMap(paramMap);

        this.gdtOneLinkService.saveByVo(vo);

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        vo.setId(gdtOneLink.getId());

        paramMap = new HashMap<>();
        paramMap.put("deptId", "10092514");
        paramMap.put("entrance", "FASTCONSULT.RA123");
        vo.setParamMap(paramMap);

        this.gdtOneLinkService.updateUTMByVo(vo);

        String expectedLink = "https://gooddoctor.onelink.com/Cmiw/?pid=intransit&c=shihlin&scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D&af_adset=shihlin&af_ad=consult&key1=value1&key2=value2&key3=value3";

        gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(expectedLink, gdtOneLink.getLink());
    }

    @Test
    public void testExpiredById() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        this.gdtOneLinkService.saveByVo(vo);

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        this.gdtOneLinkService.expireById(gdtOneLink.getId());

        gdtOneLink = this.gdtOneLinkService.findById(gdtOneLink.getId());
        Assert.assertEquals(StatusEnum.OFF.getCode(), gdtOneLink.getStatus());
    }

    @Test
    public void testExpiredByIdWithExpiredGDTOneLink() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setStatus(StatusEnum.OFF.getCode());
        this.gdtOneLinkService.saveByVo(vo);

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(StatusEnum.OFF.getCode(), gdtOneLink.getStatus());
        this.gdtOneLinkService.expireById(gdtOneLink.getId());

        gdtOneLink = this.gdtOneLinkService.findById(gdtOneLink.getId());
        Assert.assertEquals(StatusEnum.OFF.getCode(), gdtOneLink.getStatus());
    }

    @Test(expected = ServiceException.class)
    public void testExpiredByIdWithNullId() {
        this.gdtOneLinkService.expireById(null);
    }

    @Test
    public void testActivateById() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        vo.setStatus(StatusEnum.OFF.getCode());
        this.gdtOneLinkService.saveByVo(vo);

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(StatusEnum.OFF.getCode(), gdtOneLink.getStatus());
        this.gdtOneLinkService.activateById(gdtOneLink.getId());

        gdtOneLink = this.gdtOneLinkService.findById(gdtOneLink.getId());
        Assert.assertEquals(StatusEnum.ACTIVE.getCode(), gdtOneLink.getStatus());
    }

    @Test
    public void testActivateByIdWithActivateGDTOneLike() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        this.gdtOneLinkService.saveByVo(vo);

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Assert.assertEquals(StatusEnum.ACTIVE.getCode(), gdtOneLink.getStatus());
        this.gdtOneLinkService.activateById(gdtOneLink.getId());

        gdtOneLink = this.gdtOneLinkService.findById(gdtOneLink.getId());
        Assert.assertEquals(StatusEnum.ACTIVE.getCode(), gdtOneLink.getStatus());
    }

    @Test(expected = ServiceException.class)
    public void testActivateByIdWithNullId() {
        this.gdtOneLinkService.activateById(null);
    }

    @Test
    public void testFindVoById() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        this.gdtOneLinkService.saveByVo(vo);

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);

        vo = this.gdtOneLinkService.findVoById(gdtOneLink.getId());
        Assert.assertEquals(3, vo.getCustomizeParamList().size());
        Assert.assertEquals(DEFAULT_SAA_LANDING_PAGE_ID, vo.getLandingPageVo().getId());
    }

    @Test
    public void testFindAllVo() {
        GDTOneLinkVo vo = this.generateDefaultGDTOneLink();
        this.gdtOneLinkService.saveByVo(vo);

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);

        List<GDTOneLinkVo> voList = this.gdtOneLinkService.findAllVo();
        Assert.assertEquals(1, voList.size());
        Assert.assertEquals(DEFAULT_SAA_LANDING_PAGE_ID, voList.get(0).getLandingPageVo().getId());
    }

    private GDTOneLinkVo generateDefaultGDTOneLink() {
        String[] utmParamArray = new String[]{"intransit", "acquisition", "shihlin", "consult"};

        GDTOneLinkVo defaultVo = new GDTOneLinkVo();
        defaultVo.setLandingPageId(DEFAULT_SAA_LANDING_PAGE_ID);
        defaultVo.setStatus("ACTIVE");

        defaultVo.setUtmSource(utmParamArray[0]);
        defaultVo.setUtmMedium(utmParamArray[1]);
        defaultVo.setUtmCampaign(utmParamArray[2]);
        defaultVo.setUtmContent(utmParamArray[3]);
        defaultVo.setName("test-name-" + System.currentTimeMillis());

        List<Map<String, String>> customizeParamList = new ArrayList<>();
        {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("key", "key1");
            paramMap.put("value", "value1");
            customizeParamList.add(paramMap);
        }
        {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("key", "key2");
            paramMap.put("value", "value2");
            customizeParamList.add(paramMap);
        }
        {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("key", "key3");
            paramMap.put("value", "value3");
            customizeParamList.add(paramMap);
        }
        defaultVo.setCustomizeParamList(customizeParamList);

        return defaultVo;
    }
}
