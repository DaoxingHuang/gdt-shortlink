package com.gdtc.deeplink.manager.service;

import com.gdtc.deeplink.manager.BaseTest;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.filter.SSOUserInfo;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.utils.Constants;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.vo.DeepLinkVo;
import com.gdtc.deeplink.manager.vo.LandingPageVo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.List;

public class LandingPageServiceImplTest extends BaseTest {
    @Autowired
    private LandingPageService landingPageService;

    @Autowired
    private DeepLinkService deepLinkService;

    public static final int EXISTING_LANDING_PAGE_COUNT = 6;

    @Before
    public void setUp() {
        SSOUserInfo userInfo = new SSOUserInfo();
        userInfo.setUsername("frank.zhao");
        ThreadUserInfo.setUserInfo(userInfo);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSaveByVo() {
        LandingPageVo vo = this.generateDefaultGHLandingPageVo();
        this.landingPageService.saveByVo(vo);

        List<LandingPage> landingPageList = this.landingPageService.findAll();
        Assert.assertEquals(EXISTING_LANDING_PAGE_COUNT + 1, landingPageList.size());
    }

    @Test
    public void testSaveByVoForSAANative() {
        LandingPageVo vo = this.generateDefaultSAALandingPageVo();
        this.landingPageService.saveByVo(vo);
    }

    @Test
    public void testSaveByVoForSAAH5() {
        LandingPageVo vo = this.generateDefaultSAALandingPageVo();
        vo.setSchemeName("global_h5_opennewpage");
        vo.setPathTemplate("/newpage/h5/{id}");
        this.landingPageService.saveByVo(vo);
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoForSAAH5WithNullScheme() {
        LandingPageVo vo = this.generateDefaultSAALandingPageVo();
        vo.setSchemeName("");
        vo.setPathTemplate("/newpage/h5/{id}");
        this.landingPageService.saveByVo(vo);
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoForSAAH5WithNullPath() {
        LandingPageVo vo = this.generateDefaultSAALandingPageVo();
        vo.setSchemeName("global_h5_opennewpage");
        vo.setPathTemplate("");
        this.landingPageService.saveByVo(vo);
    }

    @Test
    public void testSaveByVoForSAANativeWithNullPath() {
        LandingPageVo vo = this.generateDefaultSAALandingPageVo();
        vo.setSchemeName("global_show_tab_home");
        vo.setPathTemplate("  ");
        this.landingPageService.saveByVo(vo);

        Assert.assertEquals(Constants.BLANK_STRING, this.landingPageService.findAll().get(0).getPathTemplate());
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoWithNullPlatform() {
        LandingPageVo vo = this.generateDefaultGHLandingPageVo();
        vo.setPlatform(null);
        this.landingPageService.saveByVo(vo);
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoWithInvalidPathTemplate() {
        LandingPageVo vo = this.generateDefaultGHLandingPageVo();
        vo.setPathTemplate("/consult/{{id}");
        this.landingPageService.saveByVo(vo);
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoWithSamePathTemplate() {
        LandingPageVo vo = this.generateDefaultGHLandingPageVo();
        this.landingPageService.saveByVo(vo);

        this.landingPageService.saveByVo(vo);
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoWithSameName() {
        LandingPageVo vo = this.generateDefaultGHLandingPageVo();
        this.landingPageService.saveByVo(vo);

        vo.setPathTemplate("/gdt" + vo.getPathTemplate());
        this.landingPageService.saveByVo(vo);
        Assert.fail();
    }

    @Test
    public void testFindVoById() {
        LandingPageVo selectVo = this.landingPageService.findVoById(3);
        Assert.assertEquals("deptId", selectVo.getParamList().get(0));
        Assert.assertEquals("entrance", selectVo.getParamList().get(1));
    }

    @Test(expected = ServiceException.class)
    public void testFindVoByIdWithNotExistingId() {
        this.landingPageService.findVoById(null);
        Assert.fail();
    }

    @Test
    public void testFindAllVo() {
        List<LandingPageVo> landingPageVoList = this.landingPageService.findAllVo();

        Assert.assertEquals(EXISTING_LANDING_PAGE_COUNT, landingPageVoList.size());
        Assert.assertEquals(EXISTING_LANDING_PAGE_COUNT, landingPageVoList.get(0).getId() - landingPageVoList.get(landingPageVoList.size() - 1).getId() + 1);

    }

    @Test
    public void testFindAllVoByPlatform() {
        Assert.assertEquals(2, this.landingPageService.findAllVoByPlatform(PlatformEnum.GH).size());

        Assert.assertEquals(4, this.landingPageService.findAllVoByPlatform(PlatformEnum.SAA).size());
    }

    @Test(expected = ServiceException.class)
    public void testFindAllVoByPlatformWithNullPlatform() {
        this.landingPageService.findAllVoByPlatform(null);
    }

    @Test
    public void testDeleteById() {
        Assert.assertNotNull(this.landingPageService.findById(3));
        this.landingPageService.deleteById(3);
        Assert.assertNull(this.landingPageService.findById(3));
    }

    @Test(expected = ServiceException.class)
    public void testDeleteByIdWithBeUsed() {
        Assert.assertNotNull(this.landingPageService.findById(2));
        String[] utmParamArray = new String[]{"intransit", "acquisition", "shihlin", "consult"};

        DeepLinkVo defaultVo = new DeepLinkVo();
        defaultVo.setLandingPageId(2);
        defaultVo.setStatus("ACTIVE");
        defaultVo.setPlatform("GH");

        defaultVo.setUtmSource(utmParamArray[0]);
        defaultVo.setUtmMedium(utmParamArray[1]);
        defaultVo.setUtmCampaign(utmParamArray[2]);
        defaultVo.setUtmContent(utmParamArray[3]);
        defaultVo.setName("test-name-" + System.currentTimeMillis());
        this.deepLinkService.saveByVo(defaultVo);

        this.landingPageService.deleteById(2);
    }

    @Test
    public void testDeleteByIdWithNullId() {
        int landingPageSizeBeforeDelete = this.landingPageService.findAll().size();
        this.landingPageService.deleteById(null);
        Assert.assertEquals(landingPageSizeBeforeDelete, this.landingPageService.findAll().size());
    }


    private LandingPageVo generateDefaultGHLandingPageVo() {
        LandingPageVo vo = new LandingPageVo();
        vo.setName("junit-test-name");
        vo.setPlatform(PlatformEnum.GH.name());
        vo.setModule("consult");
        vo.setSchemeName("consult-schema");
        vo.setPathTemplate("/consult/{doctorId}");

        return vo;
    }

    private LandingPageVo generateDefaultSAALandingPageVo() {
        LandingPageVo vo = new LandingPageVo();
        vo.setName("junit-test-name");
        vo.setPlatform(PlatformEnum.SAA.name());
        vo.setModule("mall");
        vo.setSchemeName("global_show_tab_healthMall");
//        vo.setPathTemplate("/consult/{doctorId}");
        return vo;
    }

}
