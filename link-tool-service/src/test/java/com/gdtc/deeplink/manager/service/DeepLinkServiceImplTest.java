package com.gdtc.deeplink.manager.service;

import com.gdtc.deeplink.manager.BaseTest;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.dao.DeepLinkMapper;
import com.gdtc.deeplink.manager.dao.LandingPageMapper;
import com.gdtc.deeplink.manager.filter.SSOUserInfo;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.utils.StatusEnum;
import com.gdtc.deeplink.manager.vo.DeepLinkVo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeepLinkServiceImplTest extends BaseTest {
    @Autowired
    private DeepLinkService deepLinkService;

    @Autowired
    private DeepLinkMapper deepLinkMapper;

    @Autowired
    private LandingPageMapper landingPageMapper;

    @Value("${deeplink.utm.same-times}")
    private int maxUtmSameTimes;

    private static final Integer DEFAULT_LANDING_PAGE_ID = 2;

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
    public void testSaveByVoForGHNoneParam() {
        String[][] utmParamArray = new String[][]{{"intransit", "acquisition", "shihlin", "consult"},
        {"grabads", "acquisition", "shihlin", "consult"},
        {"intransit", "acquisition", "yoshinoya", "consult"},
        {"grabads", "acquisition", "yoshinoya", "consult"},
        {"intransit", "acquisition", "bonusgajian", "consult"},
        {"grabads", "acquisition", "bonusgajian", "consult"},
        {"intransit", "acquisition", "gariskuku", "consult"},
        {"grabads", "acquisition", "gariskuku", "consult"},
        {"intransit", "acquisition", "maag", "consult"},
        {"grabads", "acquisition", "maag", "consult"},
        {"intransit", "acquisition", "telingapanas", "consult"},
        {"grabads", "acquisition", "telingapanas", "consult"},
        {"intransit", "acquisition", "dietmalam", "consult"},
        {"grabads", "acquisition", "dietmalam", "consult"},
        {"intransit", "acquisition", "dietsiang", "consult"},
        {"grabads", "acquisition", "dietsiang", "consult"}};

        String[] expectedLinkArray = new String[]{
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dintransit_acquisition_shihlin_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dgrabads_acquisition_shihlin_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dintransit_acquisition_yoshinoya_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dgrabads_acquisition_yoshinoya_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dintransit_acquisition_bonusgajian_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dgrabads_acquisition_bonusgajian_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dintransit_acquisition_gariskuku_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dgrabads_acquisition_gariskuku_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dintransit_acquisition_maag_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dgrabads_acquisition_maag_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dintransit_acquisition_telingapanas_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dgrabads_acquisition_telingapanas_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dintransit_acquisition_dietmalam_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dgrabads_acquisition_dietmalam_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dintransit_acquisition_dietsiang_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-psychic%25252F%252523%25252Fprovider%25253Futm%25253Dgrabads_acquisition_dietsiang_consult"};
        DeepLinkVo vo = new DeepLinkVo();
        vo.setLandingPageId(2);
        vo.setStatus("ACTIVE");
        vo.setPlatform("GH");
        int index = 0;
        for (String[] utmParam : utmParamArray) {
            vo.setUtmSource(utmParam[0]);
            vo.setUtmMedium(utmParam[1]);
            vo.setUtmCampaign(utmParam[2]);
            vo.setUtmContent(utmParam[3]);
            vo.setName("test-name-" + index);
            this.deepLinkService.saveByVo(vo);

            List<DeepLink> deepLinkList = this.deepLinkService.findAll();
            DeepLink deepLink = deepLinkList.get(0);
            Assert.assertEquals(expectedLinkArray[index], deepLink.getLink());

            index++;
        }
    }


    @Test
    public void testSaveByVoForGHWithParam() {
        String[][] utmParamArray = new String[][]{
                {"grabadsfit", "retention", "tkbentoallsegments", "consult"},
                {"grabads", "retention", "tkbentoallsegments", "consult"},
                {"masthead", "retention", "tkbentoallsegments", "consult"},
                {"masthead", "retention", "tkchatimeallsegments", "consult"},
                {"masthead", "retention", "ovotgh25allsegments", "consult"},
                {"grabadsfit", "retention", "tkchatimeallsegments", "consult"},
                {"grabads", "retention", "tkchatimeallsegments", "consult"},
                {"grabads", "retention", "musimhujanallsegments", "consult"},
                {"grabadsfit", "retention", "musimhujanallsegments", "consult"},
                {"grabadsfit", "retention", "ovotgh25allsegments", "consult"},
                {"grabads", "retention", "ovotgh25allsegments", "consult"},
                {"grabads", "retention", "ovotgh25awareg", "consult"},
                {"grabads", "retention", "ovotgh25awanoreg", "consult"},
                {"grabads", "retention", "ovotgh25puidcreated", "consult"},
                {"grabadsfit", "retention", "tkchatimetrialconsultomni", "consult"},
                {"grabadsfit", "retention", "tkchatimeconsnoreg", "consult"},
                {"grabadsfit", "retention", "tkchatimeawareg", "consult"},
                {"grabads", "retention", "tkchatimetrialconsultomni", "consult"},
                {"grabads", "retention", "tkchatimeconsnoreg", "consult"},
                {"grabads", "retention", "tkchatimeawareg", "consult"},
                {"grabads", "retention", "tkchatimeawanoreg", "consult"},
                {"grabadsfit", "retention", "tkchatimeawanoreg", "consult"},
                {"grabadsfit", "retention", "tkchatimepuidcreated", "consult"},
                {"grabads", "retention", "tkchatimepuidcreated", "consult"}
        };

        String[] expectedLinkArray = new String[]{
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabadsfit_retention_tkbentoallsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_tkbentoallsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dmasthead_retention_tkbentoallsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dmasthead_retention_tkchatimeallsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dmasthead_retention_ovotgh25allsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabadsfit_retention_tkchatimeallsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_tkchatimeallsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_musimhujanallsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabadsfit_retention_musimhujanallsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabadsfit_retention_ovotgh25allsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_ovotgh25allsegments_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_ovotgh25awareg_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_ovotgh25awanoreg_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_ovotgh25puidcreated_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabadsfit_retention_tkchatimetrialconsultomni_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabadsfit_retention_tkchatimeconsnoreg_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabadsfit_retention_tkchatimeawareg_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_tkchatimetrialconsultomni_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_tkchatimeconsnoreg_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_tkchatimeawareg_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_tkchatimeawanoreg_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabadsfit_retention_tkchatimeawanoreg_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabadsfit_retention_tkchatimepuidcreated_consult",
                "grab://open?screenType=CONSENT&webviewUrl=https%3A%2F%2Fapi.grab.com%2Fgrabid%2Fv1%2Foauth2%2Fgrablet%2Fconfig%3Fclient_id%3D699cc1833d044f07bc6593cd1ff67a6a%26redirect_url%3Dhttps%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-site%252F%253FreturnUrl%253Dhttps%25253A%25252F%25252Fwww.gooddoctor.co.id%25252Fhealth-cable%25252F%252523%25252Fguide%25253FdeptId%25253D10092514%252526entrance%25253DFASTCONSULT.RA%252526utm%25253Dgrabads_retention_tkchatimepuidcreated_consult"};
        DeepLinkVo vo = new DeepLinkVo();
        vo.setLandingPageId(3);
        vo.setStatus("ACTIVE");
        vo.setPlatform("GH");

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptId", "10092514");
        paramMap.put("entrance", "FASTCONSULT.RA");
        vo.setParamMap(paramMap);
        int index = 0;
        for (String[] utmParam : utmParamArray) {
            vo.setUtmSource(utmParam[0]);
            vo.setUtmMedium(utmParam[1]);
            vo.setUtmCampaign(utmParam[2]);
            vo.setUtmContent(utmParam[3]);
            vo.setName("test-name-" + index);
            this.deepLinkService.saveByVo(vo);

            List<DeepLink> deepLinkList = this.deepLinkService.findAll();
            DeepLink deepLink = deepLinkList.get(0);
            Assert.assertEquals(expectedLinkArray[index], deepLink.getLink());

            index++;
        }
    }

    @Test
    public void testSaveByVoForSAANativeNoneParam() {
        String[][] utmParamArray = new String[][]{
                {"intransit", "acquisition", "shihlin", "consult"},
                {"grabads", "acquisition", "shihlin", "consult"},
                {"intransit", "acquisition", "yoshinoya", "consult"},
                {"grabads", "acquisition", "yoshinoya", "consult"},
                {"intransit", "acquisition", "bonusgajian", "consult"},
                {"grabads", "acquisition", "bonusgajian", "consult"},
                {"intransit", "acquisition", "gariskuku", "consult"},
                {"grabads", "acquisition", "gariskuku", "consult"},
                {"intransit", "acquisition", "maag", "consult"},
                {"grabads", "acquisition", "maag", "consult"},
                {"intransit", "acquisition", "telingapanas", "consult"},
                {"grabads", "acquisition", "telingapanas", "consult"},
                {"intransit", "acquisition", "dietmalam", "consult"},
                {"grabads", "acquisition", "dietmalam", "consult"},
                {"intransit", "acquisition", "dietsiang", "consult"},
                {"grabads", "acquisition", "dietsiang", "consult"}};

        String[] expectedLinkArray = new String[]{
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522grabads_acquisition_shihlin_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_yoshinoya_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522grabads_acquisition_yoshinoya_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_bonusgajian_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522grabads_acquisition_bonusgajian_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_gariskuku_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522grabads_acquisition_gariskuku_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_maag_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522grabads_acquisition_maag_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_telingapanas_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522grabads_acquisition_telingapanas_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_dietmalam_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522grabads_acquisition_dietmalam_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522intransit_acquisition_dietsiang_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_consult%3Fcontent%3D%257B%2522utm%2522%253A%2522grabads_acquisition_dietsiang_consult%2522%257D"
        };

        DeepLinkVo vo = new DeepLinkVo();
        vo.setLandingPageId(4);
        vo.setStatus("ACTIVE");
        vo.setPlatform("SAA");

        int index = 0;
        for (String[] utmParam : utmParamArray) {
            vo.setUtmSource(utmParam[0]);
            vo.setUtmMedium(utmParam[1]);
            vo.setUtmCampaign(utmParam[2]);
            vo.setUtmContent(utmParam[3]);
            vo.setName("test-name-" + index);
            this.deepLinkService.saveByVo(vo);

            List<DeepLink> deepLinkList = this.deepLinkService.findAll();
            DeepLink deepLink = deepLinkList.get(0);
            Assert.assertEquals(expectedLinkArray[index], deepLink.getLink());

            index++;
        }
    }

    @Test
    public void testSaveByVoForSAANativeWithParam() {
        String[][] utmParamArray = new String[][]{
                {"intransit", "acquisition", "shihlin", "consult"},
        };

        String[] expectedLinkArray = new String[]{
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_show_tab_healthMall%3Fcontent%3D%257B%2522param1%2522%253A%25221%2522%252C%2522param2%2522%253A%25222%2522%252C%2522param3%2522%253A%25223%2522%252C%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D",
        };

        DeepLinkVo vo = new DeepLinkVo();
        vo.setLandingPageId(6);
        vo.setStatus("ACTIVE");
        vo.setPlatform("SAA");

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("param1", "1");
        paramMap.put("param2", "2");
        paramMap.put("param3", "3");

        vo.setParamMap(paramMap);

        int index = 0;
        for (String[] utmParam : utmParamArray) {
            vo.setUtmSource(utmParam[0]);
            vo.setUtmMedium(utmParam[1]);
            vo.setUtmCampaign(utmParam[2]);
            vo.setUtmContent(utmParam[3]);
            vo.setName("test-name-" + index);
            this.deepLinkService.saveByVo(vo);

            List<DeepLink> deepLinkList = this.deepLinkService.findAll();
            DeepLink deepLink = deepLinkList.get(0);
            Assert.assertEquals(expectedLinkArray[index], deepLink.getLink());

            index++;
        }
    }

    @Test
    public void testSaveByVoForSAAH5WithParam() {
        String[][] utmParamArray = new String[][]{
                {"intransit", "acquisition", "shihlin", "consult"},
                {"grabads", "acquisition", "shihlin", "consult"},
                {"intransit", "acquisition", "yoshinoya", "consult"},
                {"grabads", "acquisition", "yoshinoya", "consult"},
                {"intransit", "acquisition", "bonusgajian", "consult"},
                {"grabads", "acquisition", "bonusgajian", "consult"},
                {"intransit", "acquisition", "gariskuku", "consult"},
                {"grabads", "acquisition", "gariskuku", "consult"},
                {"intransit", "acquisition", "maag", "consult"},
                {"grabads", "acquisition", "maag", "consult"},
                {"intransit", "acquisition", "telingapanas", "consult"},
                {"grabads", "acquisition", "telingapanas", "consult"},
                {"intransit", "acquisition", "dietmalam", "consult"},
                {"grabads", "acquisition", "dietmalam", "consult"},
                {"intransit", "acquisition", "dietsiang", "consult"},
                {"grabads", "acquisition", "dietsiang", "consult"}};

        String[] expectedLinkArray = new String[]{
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_shihlin_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522grabads_acquisition_shihlin_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_yoshinoya_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522grabads_acquisition_yoshinoya_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_bonusgajian_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522grabads_acquisition_bonusgajian_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_gariskuku_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522grabads_acquisition_gariskuku_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_maag_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522grabads_acquisition_maag_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_telingapanas_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522grabads_acquisition_telingapanas_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_dietmalam_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522grabads_acquisition_dietmalam_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522intransit_acquisition_dietsiang_consult%2522%257D",
                "https://ulink.gooddoctor.co.id/plugin-share/?scheme=global_h5_opennewpage%3Fcontent%3D%257B%2522url%2522%253A%2522https%253A%252F%252Fwww.gooddoctor.co.id%252Fhealth-cable%252F%2523%252Fguide%253FdeptId%253D10092514%2526entrance%253DFASTCONSULT.RA%2522%252C%2522utm%2522%253A%2522grabads_acquisition_dietsiang_consult%2522%257D"
        };

        DeepLinkVo vo = new DeepLinkVo();
        vo.setLandingPageId(5);
        vo.setStatus("ACTIVE");
        vo.setPlatform("SAA");

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptId", "10092514");
        paramMap.put("entrance", "FASTCONSULT.RA");
        vo.setParamMap(paramMap);

        int index = 0;
        for (String[] utmParam : utmParamArray) {
            vo.setUtmSource(utmParam[0]);
            vo.setUtmMedium(utmParam[1]);
            vo.setUtmCampaign(utmParam[2]);
            vo.setUtmContent(utmParam[3]);
            vo.setName("test-name-" + index);
            this.deepLinkService.saveByVo(vo);

            List<DeepLink> deepLinkList = this.deepLinkService.findAll();
            DeepLink deepLink = deepLinkList.get(0);
            Assert.assertEquals(expectedLinkArray[index], deepLink.getLink());

            index++;
        }
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoForGHWithoutPathParam() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        vo.setLandingPageId(3);
        vo.setParamMap(null);

        this.deepLinkService.saveByVo(vo);
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoForGHWithErrorPathParam() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptId", "mock-deptId");

        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        vo.setLandingPageId(3);
        vo.setParamMap(paramMap);

        this.deepLinkService.saveByVo(vo);
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoForSAANativeWithErrorPathParam() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("param1", "abc");

        DeepLinkVo vo = this.generateDefaultSAADeepLinkVo();
        vo.setLandingPageId(6);
        vo.setParamMap(paramMap);

        this.deepLinkService.saveByVo(vo);
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoForSAANativeWithoutPathParam() {
        DeepLinkVo vo = this.generateDefaultSAADeepLinkVo();
        vo.setLandingPageId(6);
        vo.setParamMap(null);

        this.deepLinkService.saveByVo(vo);
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoWithErrorPlatform() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        vo.setPlatform(PlatformEnum.SAA.getCode());
        this.deepLinkService.saveByVo(vo);
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoForMaxUtmCount() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        for (int i = 0; i < maxUtmSameTimes; i++) {
            this.deepLinkService.saveByVo(vo);
        }

        this.deepLinkService.saveByVo(vo);
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoWithTooLongLandingPagePath() {
        LandingPage landingPage = new LandingPage();
        landingPage.setPlatform(PlatformEnum.SAA.getCode());
        landingPage.setSchemeName("global_h5_opennewpage");
        landingPage.setNative(false);
        landingPage.setPathTemplate("/health-user/list/{id}");
        landingPage.setModule("mine");
        landingPage.setName("test");
        landingPage.setPlatform("SAA");
        landingPage.setNative(false);
        landingPage.setCreateTime(new Date());
        landingPage.setUpdateTime(new Date());
        landingPage.setCreator("frank");
        landingPage.setEditor("frank");
        try {
            this.landingPageMapper.insert(landingPage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<LandingPage> landingPageList = null;
        try {
            landingPageList = this.landingPageMapper.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int landingPageId = landingPageList.get(landingPageList.size() - 1).getId();

        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        vo.setLandingPageId(landingPageId);
        vo.setStatus("ACTIVE");
        vo.setPlatform("SAA");

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("id", "[30961112,30961425,30960971,30960952,30961105,30961424,30960689,30958242,30960970,30960691,30960951,30960938,30961066,30961002,30960887,30960840,30960845,30960948,30961008,30960936,30961040,30961047,30961010,30960974,30960411,30961001,30958253,30961057,30961141,30961142,30961223,30960944,30960952,30960949,30961000,30961217,30961007,30961234,30961425,30958464,30961215,30961015,30961056,30960975,30960834,30960841,30960842,30960843,30960844,30961144,30961145,30961146,30960969,30960969,30960945,30960946,30960947,30960956,30960957,30960958,30961147,30961148,30961149,30960950,30960953,30960954,30960955,30960959,30961009,30961011,30961012,30961216,30961218,30961219,30958463,30960871,30960937,30960972,30960973,30960695,30961228,30961229,30961230,30961236,30960879,30960980,30960981,30960849,30960692,30960693,30960694,30960696,30960697,30960698,30960968,30960699,30961140,30958494,30960638,30961109,30961112,30961425,30960971,30960952,30961105,30961424,30960689,30958242,30960970,30960691,30960951,30960938,30961066,30961002,30960887,30960840,30960845,30960948,30961008,30960936,30961040,30961047,30961010,30960974,30960411,30961001,30958253,30961057,30961141,30961142,30961223,30960944,30960952,30960949,30961000,30961217,30961007,30961234,30961425,30958464,30961215,30961015,30961056,30960975,30960834,30960841,30960842,30960843,30960844,30961144,30961145,30961146,30960969,30960969,30960945,30960946,30960947,30960956,30960957,30960958,30961147,30961148,30961149,30960950,30960953,30960954,30960955,30960959,30961009,30961011,30961012,30961216,30961218,30961219,30958463,30960871,30960937,30960972,30960973,30960695,30961228,30961229,30961230,30961236,30960879,30960980,30960981,30960849,30960692,30960693,30960694,30960696,30960697,30960698,30960968,30960699,30961140,30958494,30960638,30961109]");
//        paramMap.put("entrance", "FASTCONSULT.RA");
        vo.setParamMap(paramMap);
        this.deepLinkService.saveByVo(vo);
        Assert.fail();
    }

    @Test
    public void testUpdateByVoForGH() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        this.deepLinkService.saveByVo(vo);

        Integer id = this.deepLinkService.findAll().get(0).getId();
        DeepLinkVo updateVo = new DeepLinkVo();
        updateVo.setId(id);
        updateVo.setUtmSource("utmsource1");
        updateVo.setUtmMedium("utmmedium1");
        updateVo.setUtmCampaign("utmcampaign1");
        updateVo.setUtmContent("utmcontent1");

        this.deepLinkService.updateUTMByVo(updateVo);

        DeepLink deepLink = this.deepLinkService.findById(id);
        String utm = deepLink.buildUtm();
        Assert.assertEquals("utmsource1_utmmedium1_utmcampaign1_utmcontent1", utm);
    }

    @Test
    public void testUpdateByVoForSAA() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        vo.setPlatform(PlatformEnum.SAA.getCode());
        vo.setLandingPageId(4);
        this.deepLinkService.saveByVo(vo);

        Integer id = this.deepLinkService.findAll().get(0).getId();
        DeepLinkVo updateVo = new DeepLinkVo();
        updateVo.setId(id);
        updateVo.setUtmSource("utmsource1");
        updateVo.setUtmMedium("utmmedium1");
        updateVo.setUtmCampaign("utmcampaign1");
        updateVo.setUtmContent("utmcontent1");

        this.deepLinkService.updateUTMByVo(updateVo);

        DeepLink deepLink = this.deepLinkService.findById(id);
        String utm = deepLink.buildUtm();
        Assert.assertEquals("utmsource1_utmmedium1_utmcampaign1_utmcontent1", utm);
    }

    @Test
    public void testUpdateByVoForSAAWithoutParam() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        vo.setPlatform(PlatformEnum.SAA.getCode());
        vo.setLandingPageId(5);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptId", "10092514");
        paramMap.put("entrance", "FASTCONSULT.RA");
        vo.setParamMap(paramMap);
        this.deepLinkService.saveByVo(vo);

        Integer id = this.deepLinkService.findAll().get(0).getId();
        DeepLinkVo updateVo = new DeepLinkVo();
        updateVo.setId(id);
        updateVo.setUtmSource("utmsource1");
        updateVo.setUtmMedium("utmmedium1");
        updateVo.setUtmCampaign("utmcampaign1");
        updateVo.setUtmContent("utmcontent1");

        this.deepLinkService.updateUTMByVo(updateVo);

        DeepLink deepLink = this.deepLinkService.findById(id);
        String utm = deepLink.buildUtm();
        Assert.assertEquals("utmsource1_utmmedium1_utmcampaign1_utmcontent1", utm);
    }

    @Test(expected = ServiceException.class)
    public void testUpdateByVoWithNullId() {
        DeepLinkVo updateVo = new DeepLinkVo();
        updateVo.setUtmSource("utmsource1");
        updateVo.setUtmMedium("utmmedium1");
        updateVo.setUtmCampaign("utmcampaign1");
        updateVo.setUtmContent("utmcontent1");

        this.deepLinkService.updateUTMByVo(updateVo);
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void testUpdateByVoWithMaxUtmSameTimes() {
        for (int i = 0; i < maxUtmSameTimes; i++) {
            DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
            vo.setUtmSource("utmsource1");
            vo.setUtmMedium("utmmedium1");
            vo.setUtmCampaign("utmcampaign1");
            vo.setUtmContent("utmcontent1");
            this.deepLinkService.saveByVo(vo);
        }

        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        this.deepLinkService.saveByVo(vo);

        Integer id = this.deepLinkService.findAll().get(0).getId();
        DeepLinkVo updateVo = new DeepLinkVo();
        updateVo.setId(id);
        updateVo.setUtmSource("utmsource1");
        updateVo.setUtmMedium("utmmedium1");
        updateVo.setUtmCampaign("utmcampaign1");
        updateVo.setUtmContent("utmcontent1");

        this.deepLinkService.updateUTMByVo(updateVo);
        Assert.fail();
    }

    @Test
    public void testExpireById() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        this.deepLinkService.saveByVo(vo);

        List<DeepLink> deepLinkList = this.deepLinkService.findAll();
        DeepLink deepLink = deepLinkList.get(0);

        Assert.assertEquals(StatusEnum.ACTIVE.getCode(), deepLink.getStatus());
        this.deepLinkService.expireById(deepLink.getId());

        deepLink = this.deepLinkService.findById(deepLink.getId());
        Assert.assertEquals(StatusEnum.OFF.getCode(), deepLink.getStatus());
    }

    @Test(expected = ServiceException.class)
    public void testExpireByIdWithNullId() {
        this.deepLinkService.expireById(null);
        Assert.fail();
    }

    @Test
    public void testExpireByIdWithExpiredDeepLink() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        vo.setStatus("OFF");
        this.deepLinkService.saveByVo(vo);

        List<DeepLink> deepLinkList = this.deepLinkService.findAll();
        DeepLink deepLink = deepLinkList.get(0);

        Assert.assertEquals(StatusEnum.OFF.getCode(), deepLink.getStatus());
        this.deepLinkService.expireById(deepLink.getId());

        deepLink = this.deepLinkService.findById(deepLink.getId());
        Assert.assertEquals(StatusEnum.OFF.getCode(), deepLink.getStatus());
    }

    @Test
    public void testActivateById() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        this.deepLinkService.saveByVo(vo);

        List<DeepLink> deepLinkList = this.deepLinkService.findAll();
        DeepLink deepLink = deepLinkList.get(0);

        Assert.assertEquals(StatusEnum.ACTIVE.getCode(), deepLink.getStatus());
        this.deepLinkService.expireById(deepLink.getId());

        deepLink = this.deepLinkService.findById(deepLink.getId());
        Assert.assertEquals(StatusEnum.OFF.getCode(), deepLink.getStatus());

        this.deepLinkService.activateById(deepLink.getId());
        deepLink = this.deepLinkService.findById(deepLink.getId());
        Assert.assertEquals(StatusEnum.ACTIVE.getCode(), deepLink.getStatus());
    }

    @Test(expected = ServiceException.class)
    public void testActivateByIdWithNullId() {
        this.deepLinkService.activateById(null);
        Assert.fail();
    }

    @Test
    public void testActivateByIdWithActivatedDeepLink() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        this.deepLinkService.saveByVo(vo);

        List<DeepLink> deepLinkList = this.deepLinkService.findAll();
        DeepLink deepLink = deepLinkList.get(0);

        Assert.assertEquals(StatusEnum.ACTIVE.getCode(), deepLink.getStatus());
        this.deepLinkService.activateById(deepLink.getId());
        deepLink = this.deepLinkService.findById(deepLink.getId());

        Assert.assertEquals(StatusEnum.ACTIVE.getCode(), deepLink.getStatus());
    }

    @Test
    public void testFindByLandingPage() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        this.deepLinkService.saveByVo(vo);
        List<DeepLinkVo> deepLinkList = this.deepLinkService.findByLandingPage(DEFAULT_LANDING_PAGE_ID);
        Assert.assertEquals(1, deepLinkList.size());
        Assert.assertEquals("intransit_acquisition_shihlin_consult", deepLinkList.get(0).getUtm());
        Assert.assertEquals(DEFAULT_LANDING_PAGE_ID, deepLinkList.get(0).getLandingPageVo().getId());
    }

    @Test
    public void testFindByLandingPageWithoutRecord() {
//        this.saveDeepLink(null);
        List<DeepLinkVo> deepLinkList = this.deepLinkService.findByLandingPage(DEFAULT_LANDING_PAGE_ID);
        Assert.assertEquals(0, deepLinkList.size());
    }

    @Test(expected = ServiceException.class)
    public void testFindByLandingPageWithNullLandingPage() {
        this.deepLinkService.findByLandingPage(100);
        Assert.fail();
    }

    @Test
    public void testFindAllVo() {
        String[][] utmParamArray = new String[][]{
                {"intransit", "acquisition", "shihlin", "consult"},
                {"grabads", "acquisition", "shihlin", "consult"},
                {"intransit", "acquisition", "yoshinoya", "consult"},
                {"grabads", "acquisition", "yoshinoya", "consult"},
                {"intransit", "acquisition", "bonusgajian", "consult"},
                {"grabads", "acquisition", "bonusgajian", "consult"},
                {"intransit", "acquisition", "gariskuku", "consult"},
                {"grabads", "acquisition", "gariskuku", "consult"},
                {"intransit", "acquisition", "maag", "consult"},
                {"grabads", "acquisition", "maag", "consult"},
                {"intransit", "acquisition", "telingapanas", "consult"},
                {"grabads", "acquisition", "telingapanas", "consult"},
                {"intransit", "acquisition", "dietmalam", "consult"},
                {"grabads", "acquisition", "dietmalam", "consult"},
                {"intransit", "acquisition", "dietsiang", "consult"},
                {"grabads", "acquisition", "dietsiang", "consult"}};
        DeepLinkVo vo = new DeepLinkVo();
        vo.setLandingPageId(2);
        vo.setStatus("ACTIVE");
        vo.setPlatform("GH");
        int index = 0;
        for (String[] utmParam : utmParamArray) {
            vo.setUtmSource(utmParam[0]);
            vo.setUtmMedium(utmParam[1]);
            vo.setUtmCampaign(utmParam[2]);
            vo.setUtmContent(utmParam[3]);
            vo.setName("test-name-" + index);
            this.deepLinkService.saveByVo(vo);
        }
        List<DeepLinkVo> deepLinkVoList = this.deepLinkService.findAllVo();
        Assert.assertEquals(utmParamArray.length, deepLinkVoList.size());
        Assert.assertEquals(utmParamArray.length, deepLinkVoList.get(0).getId() - deepLinkVoList.get(deepLinkVoList.size() - 1).getId() + 1);
    }


    @Test(expected = ServiceException.class)
    public void testFindVoByPlatformWithNullPlatform() {
        this.deepLinkService.findVoByPlatform(null);
        Assert.fail();
    }

    @Test
    public void testFindVoByPlatform() {
        String[][] utmParamArray = new String[][]{
                {"intransit", "acquisition", "shihlin", "consult"},
                {"grabads", "acquisition", "shihlin", "consult"},
                {"intransit", "acquisition", "yoshinoya", "consult"},
                {"grabads", "acquisition", "yoshinoya", "consult"},
                {"intransit", "acquisition", "bonusgajian", "consult"},
                {"grabads", "acquisition", "bonusgajian", "consult"},
                {"intransit", "acquisition", "gariskuku", "consult"},
                {"grabads", "acquisition", "gariskuku", "consult"},
                {"intransit", "acquisition", "maag", "consult"},
                {"grabads", "acquisition", "maag", "consult"},
                {"intransit", "acquisition", "telingapanas", "consult"},
                {"grabads", "acquisition", "telingapanas", "consult"},
                {"intransit", "acquisition", "dietmalam", "consult"},
                {"grabads", "acquisition", "dietmalam", "consult"},
                {"intransit", "acquisition", "dietsiang", "consult"},
                {"grabads", "acquisition", "dietsiang", "consult"}};
        DeepLinkVo vo = new DeepLinkVo();
        vo.setLandingPageId(2);
        vo.setStatus("ACTIVE");
        vo.setPlatform("GH");
        int index = 0;
        for (String[] utmParam : utmParamArray) {
            vo.setUtmSource(utmParam[0]);
            vo.setUtmMedium(utmParam[1]);
            vo.setUtmCampaign(utmParam[2]);
            vo.setUtmContent(utmParam[3]);
            vo.setName("test-name-" + index);
            this.deepLinkService.saveByVo(vo);
        }

        List<DeepLinkVo> ghDeepLinkVoList = this.deepLinkService.findVoByPlatform(PlatformEnum.GH);
        Assert.assertEquals(utmParamArray.length, ghDeepLinkVoList.size());

        List<DeepLinkVo> saaDeepLinkVoList = this.deepLinkService.findVoByPlatform(PlatformEnum.SAA);
        Assert.assertEquals(0, saaDeepLinkVoList.size());
    }

    @Test
    public void testFindVoById() {
        DeepLinkVo vo = this.generateDefaultGHDeepLinkVo();
        this.deepLinkService.saveByVo(vo);

        DeepLink deepLink = this.deepLinkService.findAll().get(0);
        vo = this.deepLinkService.findVoById(deepLink.getId());
        Assert.assertEquals(DEFAULT_LANDING_PAGE_ID, vo.getLandingPageVo().getId());
    }



    private DeepLinkVo generateDefaultGHDeepLinkVo() {
        String[] utmParamArray = new String[]{"intransit", "acquisition", "shihlin", "consult"};

        DeepLinkVo defaultVo = new DeepLinkVo();
        defaultVo.setLandingPageId(DEFAULT_LANDING_PAGE_ID);
        defaultVo.setStatus("ACTIVE");
        defaultVo.setPlatform("GH");

        defaultVo.setUtmSource(utmParamArray[0]);
        defaultVo.setUtmMedium(utmParamArray[1]);
        defaultVo.setUtmCampaign(utmParamArray[2]);
        defaultVo.setUtmContent(utmParamArray[3]);
        defaultVo.setName("test-name-" + System.currentTimeMillis());
        return defaultVo;
    }

    private DeepLinkVo generateDefaultSAADeepLinkVo() {
        String[] utmParamArray = new String[]{"intransit", "acquisition", "shihlin", "consult"};

        DeepLinkVo defaultVo = new DeepLinkVo();
        defaultVo.setLandingPageId(4);
        defaultVo.setStatus("ACTIVE");
        defaultVo.setPlatform("SAA");

        defaultVo.setUtmSource(utmParamArray[0]);
        defaultVo.setUtmMedium(utmParamArray[1]);
        defaultVo.setUtmCampaign(utmParamArray[2]);
        defaultVo.setUtmContent(utmParamArray[3]);
        defaultVo.setName("test-name-" + System.currentTimeMillis());
        return defaultVo;
    }
}
