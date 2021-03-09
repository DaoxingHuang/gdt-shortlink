package com.gdtc.deeplink.manager.service;

import com.gdtc.deeplink.manager.BaseTest;
import com.gdtc.deeplink.manager.convertor.ShortLinkMessageConvertor;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.dao.ShortLinkMapper;
import com.gdtc.deeplink.manager.dao.ShortLinkRelationMapper;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.GDTOneLink;
import com.gdtc.deeplink.manager.model.ShortLink;
import com.gdtc.deeplink.manager.model.ShortLinkRelation;
import com.gdtc.deeplink.manager.utils.Constants;
import com.gdtc.deeplink.manager.utils.ShortCodeGenerator;
import com.gdtc.deeplink.manager.utils.ShortLinkInfoEnum;
import com.gdtc.deeplink.manager.utils.StatusEnum;
import com.gdtc.deeplink.manager.vo.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ShortLinkBusinessServiceImplTest extends BaseTest {
    private static final Integer DEFAULT_SAA_LANDING_PAGE_ID = 4;

    @Autowired
    private ShortLinkBusinessService shortLinkBusinessService;
    @Autowired
    private ShortLinkService shortLinkService;

    @Autowired
    private ShortLinkRelationMapper shortLinkRelationMapper;
    @Autowired
    private ShortLinkMapper shortLinkMapper;

    @Autowired
    private DeepLinkService deepLinkService;
    @Autowired
    private GDTOneLinkService gdtOneLinkService;

    @Test
    public void testGenerateForDeepLink() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForDeepLink();
        this.shortLinkBusinessService.generateForDeepLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);

        ShortLink shortLink = this.shortLinkService.findAll().get(0);
        ShortLinkRelation shortLinkRelation = this.shortLinkRelationMapper.selectByLinkTypeAndOriginalId(ShortLinkInfoEnum.DEEPLINK.getLinkType(), shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());
        Assert.assertEquals(shortLink.getId(), shortLinkRelation.getShortLinkId());

        Assert.assertTrue(shortLink.getLink().startsWith("https://short.gooddoctor.co.od/d/"));
    }

    @Test(expected = ServiceException.class)
    public void testGenerateForDeepLinkWithNullExpiredTime() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForDeepLink();
        shortLinkVo.setExpiredTime(null);
        this.shortLinkBusinessService.generateForDeepLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void testGenerateForDeepLinkWithErrorDeepLinkId() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForDeepLink();
        this.shortLinkBusinessService.generateForDeepLink(100000, shortLinkVo);
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void testGenerateForDeepLinkWithErrorDeepLinkPlatform() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForDeepLink();

        DeepLinkVo defaultVo = new DeepLinkVo();
        defaultVo.setLandingPageId(2);
        defaultVo.setStatus("ACTIVE");
        defaultVo.setPlatform("GH");

        long ms = System.currentTimeMillis();
        defaultVo.setUtmSource("source" + ms);
        defaultVo.setUtmMedium("medium" + ms);
        defaultVo.setUtmCampaign("campaign" + ms);
        defaultVo.setUtmContent("content" + ms);
        defaultVo.setName("test-name-" + System.currentTimeMillis());

        Integer deepLinkId = null;
        try {
            this.deepLinkService.saveByVo(defaultVo);
            deepLinkId = this.deepLinkService.findAll().get(0).getId();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        shortLinkVo.getOriginalLinkVoList().get(0).setOriginalId(deepLinkId);
        this.shortLinkBusinessService.generateForDeepLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);
        Assert.fail();
    }

    @Test
    public void testGenerateForDeepLinkWithExistingDeepLink() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForDeepLink();
        this.shortLinkBusinessService.generateForDeepLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);

        DeepLink deepLink = this.deepLinkService.findAll().get(0);
        Integer shortLinkId = this.shortLinkBusinessService.findShortLinkIdByOriginalIdForDeepLink(deepLink.getId());
        Assert.assertNotNull(shortLinkId);

        this.shortLinkBusinessService.generateForDeepLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);
        Integer tempShortLinkId = this.shortLinkBusinessService.findShortLinkIdByOriginalIdForDeepLink(deepLink.getId());
        Assert.assertEquals(shortLinkId, tempShortLinkId);
    }

    @Test
    public void testListDeepLinkVoWithoutRelation() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForDeepLink();
        this.shortLinkBusinessService.generateForDeepLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);

        String[] utmParamArray = new String[]{"aaa", "bbb", "ccc", "ddd"};

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

        List<DeepLinkVo> deepLinkVoList = this.shortLinkBusinessService.listDeepLinkVoWithoutRelation();
        Assert.assertEquals(1, deepLinkVoList.size());
        Assert.assertEquals(deepLinkId, deepLinkVoList.get(0).getId());
        Assert.assertEquals(DEFAULT_SAA_LANDING_PAGE_ID, deepLinkVoList.get(0).getLandingPageVo().getId());
    }

    @Test
    public void testListAllDeepLinkVo() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForDeepLink();
        this.shortLinkBusinessService.generateForDeepLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);

        ShortLink shortLink = this.shortLinkService.findAll().get(0);

        List<DeepLinkVo> deepLinkVoList = this.shortLinkBusinessService.listAllDeepLinkVo();
        Assert.assertEquals(1, deepLinkVoList.size());
        Assert.assertEquals(shortLink.getId(), deepLinkVoList.get(0).getShortLinkId());
    }

    @Test
    public void testGenerateForGDTOneLink() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForGDTOneLink();
        this.shortLinkBusinessService.generateForGDTOneLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);

        ShortLink shortLink = this.shortLinkService.findAll().get(0);
        ShortLinkRelation shortLinkRelation = this.shortLinkRelationMapper.selectByLinkTypeAndOriginalId(ShortLinkInfoEnum.GDT_ONE_LINK.getLinkType(), shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());
        Assert.assertEquals(shortLink.getId(), shortLinkRelation.getShortLinkId());

        Assert.assertTrue(shortLink.getLink().startsWith("https://short.gooddoctor.co.od/o/"));
    }

    @Test(expected = ServiceException.class)
    public void testGenerateForGDTOneLinkWithNullExpiredTime() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForGDTOneLink();
        shortLinkVo.setExpiredTime(null);
        this.shortLinkBusinessService.generateForGDTOneLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void testGenerateForGDTOneLinkWithErrorGDTOneLinkId() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForGDTOneLink();
        this.shortLinkBusinessService.generateForGDTOneLink(100000, shortLinkVo);
        Assert.fail();
    }

    @Test
    public void testGenerateForGDTOneLinkWithExistingGDTOneLink() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForGDTOneLink();
        this.shortLinkBusinessService.generateForGDTOneLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);

        GDTOneLink gdtOneLink = this.gdtOneLinkService.findAll().get(0);
        Integer shortLinkId = this.shortLinkBusinessService.findShortLinkIdByOriginalIdForOneLink(gdtOneLink.getId());
        Assert.assertNotNull(shortLinkId);

        this.shortLinkBusinessService.generateForGDTOneLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);
        Integer tempShortLinkId = this.shortLinkBusinessService.findShortLinkIdByOriginalIdForOneLink(gdtOneLink.getId());
        Assert.assertEquals(shortLinkId, tempShortLinkId);
    }

    @Test
    public void testGenerateForCommon() {
        ShortLinkAPIVo apiVo = this.generateDefaultShortLinkAPIVo();
        ShortLinkVo shortLinkVo = this.shortLinkBusinessService.generateForCommon(apiVo);
        Assert.assertNotNull(shortLinkVo.getCode());
        Assert.assertEquals(apiVo.getExpiredTime(), shortLinkVo.getExpiredTime().getTime());
        Assert.assertEquals(apiVo.getOriginalLink(), shortLinkVo.getOriginalLinkVoList().get(0).getOriginalLink());
        Assert.assertEquals(1, shortLinkVo.getOriginalLinkVoList().size());
        Assert.assertEquals(ShortLinkInfoEnum.COMMON.getLinkType(), shortLinkVo.getLinkType());
    }

    @Test(expected = ServiceException.class)
    public void testGenerateForCommonWithErrorExpiredTime() {
        ShortLinkAPIVo apiVo = this.generateDefaultShortLinkAPIVo();
        apiVo.setExpiredTime(System.currentTimeMillis());
        ShortLinkVo shortLinkVo = this.shortLinkBusinessService.generateForCommon(apiVo);
    }

    @Test(expected = ServiceException.class)
    public void testGenerateForCommonWithNullCreator() {
        ShortLinkAPIVo apiVo = this.generateDefaultShortLinkAPIVo();
        apiVo.setCreator(null);
        ShortLinkVo shortLinkVo = this.shortLinkBusinessService.generateForCommon(apiVo);
    }

    @Test
    public void testListGDTOneLinkVoWithoutRelation() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForGDTOneLink();
        this.shortLinkBusinessService.generateForGDTOneLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);

        String[] utmParamArray = new String[]{"aaa", "bbb", "ccc", "ddd"};
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

        List<GDTOneLinkVo> gdtOneLinkVoList = this.shortLinkBusinessService.listGDTOneLinkVoWithoutRelation();
        Assert.assertEquals(1, gdtOneLinkVoList.size());
        Assert.assertEquals(gdtOneLinkId, gdtOneLinkVoList.get(0).getId());
        Assert.assertEquals(DEFAULT_SAA_LANDING_PAGE_ID, gdtOneLinkVoList.get(0).getLandingPageVo().getId());
    }

    @Test
    public void testListAllGDTOneLinkVo() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForGDTOneLink();
        this.shortLinkBusinessService.generateForGDTOneLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);

        ShortLink shortLink = this.shortLinkService.findAll().get(0);

        List<GDTOneLinkVo> gdtOneLinkVoList = this.shortLinkBusinessService.listAllGDTOneLinkVo();
        Assert.assertEquals(1, gdtOneLinkVoList.size());
        Assert.assertEquals(shortLink.getId(), gdtOneLinkVoList.get(0).getShortLinkId());
    }

    @Test
    public void testUpdateShortLinkByVo() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVoForDeepLink();
        this.shortLinkBusinessService.generateForDeepLink(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId(), shortLinkVo);

        Integer shortLinkId = this.shortLinkService.findAll().get(0).getId();

        List<DeepLink> deepLinkList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            DeepLinkVo defaultVo = new DeepLinkVo();
            defaultVo.setLandingPageId(DEFAULT_SAA_LANDING_PAGE_ID);
            defaultVo.setStatus("ACTIVE");
            defaultVo.setPlatform("SAA");

            long ms = System.currentTimeMillis() + i;
            defaultVo.setUtmSource("source" + ms);
            defaultVo.setUtmMedium("medium" + ms);
            defaultVo.setUtmCampaign("campaign" + ms);
            defaultVo.setUtmContent("content" + ms);
            defaultVo.setName("test-name-" + System.currentTimeMillis());

            DeepLink deepLink = null;
            try {
                this.deepLinkService.saveByVo(defaultVo);
                deepLink = this.deepLinkService.findAll().get(0);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            deepLinkList.add(deepLink);
        }


        Assert.assertEquals(1, this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId).size());

        shortLinkVo = new ShortLinkVo();
        shortLinkVo.setId(shortLinkId);
        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());

        shortLinkVo.setOriginalLinkVoList(new ArrayList<>());

        ShortLinkVo finalShortLinkVo = shortLinkVo;
        deepLinkList.forEach(deepLink -> {
            OriginalLinkVo vo = new OriginalLinkVo();
            vo.setOriginalId(deepLink.getId());
            finalShortLinkVo.getOriginalLinkVoList().add(vo);

        });

        this.shortLinkBusinessService.updateShortLinkByVo(shortLinkVo);

        Assert.assertEquals(3, shortLinkVo.getOriginalLinkVoList().size());

        List<ShortLinkRelation> shortLinkRelationList = this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId);

        Assert.assertEquals(3, shortLinkRelationList.size());
        Assert.assertEquals(deepLinkList.get(0).getLink(), shortLinkRelationList.get(0).getOriginalLink());
        Assert.assertEquals(deepLinkList.get(1).getLink(), shortLinkRelationList.get(1).getOriginalLink());
        Assert.assertEquals(deepLinkList.get(2).getLink(), shortLinkRelationList.get(2).getOriginalLink());
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

    private ShortLinkAPIVo generateDefaultShortLinkAPIVo() {
        ShortLinkAPIVo apiVo = new ShortLinkAPIVo();
        apiVo.setCreator("share-service");
        apiVo.setOriginalLink("https://wwww.gooddoctor.co.id");
        apiVo.setExpiredTime(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
        return apiVo;
    }

    private ShortLink saveShortLinkVo(ShortLinkVo vo) {
        ShortLink shortLink = ShortLinkMessageConvertor.voToModel(vo);
        String code = ShortCodeGenerator.generateCode(vo.getOriginalLinkVoList().get(0).getOriginalLink());
        shortLink.setCode(code);
        this.shortLinkMapper.insert(shortLink);
        vo.setId(shortLink.getId());

        ShortLinkRelation relation = new ShortLinkRelation();
        relation.setShortLinkId(shortLink.getId());
        relation.setLinkType(shortLink.getLinkType());
        relation.setOriginalId(vo.getOriginalLinkVoList().get(0).getOriginalId());
        relation.setOriginalLink(vo.getOriginalLinkVoList().get(0).getOriginalLink());
        this.shortLinkRelationMapper.insert(relation);

        return shortLink;
    }
}
