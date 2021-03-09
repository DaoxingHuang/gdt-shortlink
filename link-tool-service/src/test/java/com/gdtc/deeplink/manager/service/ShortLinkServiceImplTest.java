package com.gdtc.deeplink.manager.service;

import com.gdtc.deeplink.manager.BaseTest;
import com.gdtc.deeplink.manager.configuration.ConfigParam;
import com.gdtc.deeplink.manager.convertor.ShortLinkMessageConvertor;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.dao.ShortLinkMapper;
import com.gdtc.deeplink.manager.dao.ShortLinkRelationMapper;
import com.gdtc.deeplink.manager.dao.ShortLinkRelationMapper;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.ShortLinkRelation;
import com.gdtc.deeplink.manager.model.ShortLink;
import com.gdtc.deeplink.manager.utils.Constants;
import com.gdtc.deeplink.manager.utils.ShortCodeGenerator;
import com.gdtc.deeplink.manager.utils.ShortLinkInfoEnum;
import com.gdtc.deeplink.manager.utils.StatusEnum;
import com.gdtc.deeplink.manager.vo.DeepLinkVo;
import com.gdtc.deeplink.manager.vo.OriginalLinkVo;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ShortLinkServiceImplTest extends BaseTest {
    private static final Integer DEFAULT_SAA_LANDING_PAGE_ID = 4;

    @Autowired
    private ShortLinkService shortLinkService;

    @Autowired
    private ShortLinkMapper shortLinkMapper;
    
    @Autowired
    private ShortLinkRelationMapper shortLinkRelationMapper;

    @Autowired
    private DeepLinkService deepLinkService;

    @Autowired
    private ConfigParam configParam;

    @Test
    public void testSaveByVo() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        OriginalLinkVo originalLinkVo = shortLinkVo.getOriginalLinkVoList().get(0);

        this.shortLinkService.saveByVo(shortLinkVo, originalLinkVo);

        ShortLink shortLink = this.shortLinkService.findAll().get(0);
        Assert.assertTrue(shortLink.getLink().startsWith("https://short.gooddoctor.co.od/d/"));

        ShortLinkRelation relation = this.shortLinkRelationMapper.selectByLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType()).get(0);
        Assert.assertNotNull(relation);
        Assert.assertNotNull(relation.getOriginalId());
        Assert.assertNotNull(relation.getOriginalLink());
        Assert.assertNotNull(relation.getShortLinkId());
    }

    @Test
    public void testSaveByVoWithExistingCode() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        {
            DeepLink deepLink = this.deepLinkService.findById(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());
            String link = deepLink.getLink();
            String code = ShortCodeGenerator.generateCode(link);
            ShortLink shortLink = new ShortLink();
            shortLink.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
            shortLink.setCode(code);
            shortLink.setExpiredTime(new Date());
            shortLink.setName("test-short-name-" + System.currentTimeMillis());
            shortLink.setStatus(StatusEnum.ACTIVE.getCode());
            shortLink.setCreateTime(new Date());
            shortLink.setUpdateTime(new Date());
            shortLink.setCreator("frank");
            shortLink.setEditor("frank");

            this.shortLinkService.save(shortLink);
        }

        this.shortLinkService.saveByVo(shortLinkVo, shortLinkVo.getOriginalLinkVoList().get(0));
        List<ShortLinkRelation> relationList = this.shortLinkRelationMapper.selectByLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        Assert.assertEquals(1, relationList.size());

        List<ShortLink> shortLinkList = this.shortLinkService.findAll();
        Assert.assertEquals(2, shortLinkList.size());

        Assert.assertEquals(shortLinkList.get(0).getId(), relationList.get(0).getShortLinkId());
    }

    @Test
    public void testSaveByVoWithOriginalIdAndDuplicateRelation() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        OriginalLinkVo originalLinkVo = shortLinkVo.getOriginalLinkVoList().get(0);

        this.shortLinkService.saveByVo(shortLinkVo, originalLinkVo);

        this.shortLinkService.saveByVo(shortLinkVo, originalLinkVo);
        List<ShortLinkRelation> relationList = this.shortLinkRelationMapper.selectByLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        Assert.assertEquals(1, relationList.size());

        List<ShortLink> shortLinkList = this.shortLinkService.findAll();
        Assert.assertEquals(1, shortLinkList.size());
    }

    @Test
    public void testSaveByVoWithOriginalIdIsNullAndDuplicateRelation() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        OriginalLinkVo originalLinkVo = shortLinkVo.getOriginalLinkVoList().get(0);
        originalLinkVo.setOriginalId(null);

        this.shortLinkService.saveByVo(shortLinkVo, originalLinkVo);

        this.shortLinkService.saveByVo(shortLinkVo, originalLinkVo);
        List<ShortLinkRelation> relationList = this.shortLinkRelationMapper.selectByLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        Assert.assertEquals(2, relationList.size());

        List<ShortLink> shortLinkList = this.shortLinkService.findAll();
        Assert.assertEquals(2, shortLinkList.size());
    }

    @Test(expected = ServiceException.class)
    public void testSaveByVoWithOriginalLinkIsNull() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        OriginalLinkVo originalLinkVo = shortLinkVo.getOriginalLinkVoList().get(0);
        originalLinkVo.setOriginalLink(null);

        this.shortLinkService.saveByVo(shortLinkVo, originalLinkVo);
    }

    @Test
    public void testUpdateVo() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();

        List<Integer> deepLinkIdList = new ArrayList<>();
        deepLinkIdList.add(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());

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

            Integer deepLinkId = null;
            try {
                this.deepLinkService.saveByVo(defaultVo);
                deepLinkId = this.deepLinkService.findAll().get(0).getId();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            deepLinkIdList.add(deepLinkId);
        }


        Assert.assertEquals(1, this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId).size());
        deepLinkIdList.add(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());

        shortLinkVo = new ShortLinkVo();
        shortLinkVo.setId(shortLinkId);
        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());

        shortLinkVo.setOriginalLinkVoList(
                this.shortLinkService.findVoById(shortLinkId).getOriginalLinkVoList()
        );

        ShortLinkVo finalShortLinkVo = shortLinkVo;
        deepLinkIdList.forEach(id -> {
            OriginalLinkVo vo = new OriginalLinkVo();
            vo.setOriginalId(id);
            vo.setOriginalLink(this.deepLinkService.findById(id).getLink());
            finalShortLinkVo.getOriginalLinkVoList().add(vo);

        });

        this.shortLinkService.updateByVo(shortLinkVo);

        Assert.assertEquals(4, shortLinkVo.getOriginalLinkVoList().size());

        List<ShortLinkRelation> shortLinkRelationList = this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId);

        Assert.assertEquals(4, shortLinkRelationList.size());
        Assert.assertEquals(deepLinkIdList.get(0), shortLinkRelationList.get(0).getOriginalId());
        Assert.assertEquals(deepLinkIdList.get(1), shortLinkRelationList.get(1).getOriginalId());
    }

    @Test(expected = ServiceException.class)
    public void testUpdateVoWithOriginalLinkIsNull() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();

        List<Integer> deepLinkIdList = new ArrayList<>();
        deepLinkIdList.add(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());

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

            Integer deepLinkId = null;
            try {
                this.deepLinkService.saveByVo(defaultVo);
                deepLinkId = this.deepLinkService.findAll().get(0).getId();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            deepLinkIdList.add(deepLinkId);
        }


        Assert.assertEquals(1, this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId).size());
        deepLinkIdList.add(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());

        shortLinkVo = new ShortLinkVo();
        shortLinkVo.setId(shortLinkId);
        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());

        shortLinkVo.setOriginalLinkVoList(
                this.shortLinkService.findVoById(shortLinkId).getOriginalLinkVoList()
        );

        ShortLinkVo finalShortLinkVo = shortLinkVo;
        deepLinkIdList.forEach(id -> {
            OriginalLinkVo vo = new OriginalLinkVo();
            vo.setOriginalId(id);
//            vo.setOriginalLink(this.deepLinkService.findById(id).getLink());
            finalShortLinkVo.getOriginalLinkVoList().add(vo);

        });

        this.shortLinkService.updateByVo(shortLinkVo);
    }

    @Test(expected = ServiceException.class)
    public void testUpdateVoWithDuplicateDeepLink() {
        ShortLinkVo shortLinkVo1 = this.generateDefaultShortLinkVo();
        ShortLinkVo shortLinkVo2 = this.generateDefaultShortLinkVo();
        ShortLinkVo shortLinkVo = null;

        try {
            ShortLink shortLink1 = this.saveShortLinkVo(shortLinkVo1);
            Integer shortLinkId1 = shortLink1.getId();

            ShortLink shortLink2 = this.saveShortLinkVo(shortLinkVo2);
            Integer shortLinkId2 = shortLink1.getId();

            shortLinkVo = new ShortLinkVo();
            shortLinkVo.setId(shortLinkId2);
            shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());

            OriginalLinkVo originalLinkVo = new OriginalLinkVo();
            originalLinkVo.setOriginalId(shortLinkVo1.getOriginalLinkVoList().get(0).getOriginalId());
            originalLinkVo.setOriginalLink(shortLinkVo1.getOriginalLinkVoList().get(0).getOriginalLink());

            shortLinkVo.setOriginalLinkVoList(Arrays.asList(originalLinkVo));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        this.shortLinkService.updateByVo(shortLinkVo);

        Assert.fail();
    }

    @Test
    public void testUpdateVoWithAllAdded() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();
        this.shortLinkRelationMapper.deleteByShortLinkId(shortLinkId);

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


        Assert.assertEquals(0, this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId).size());

        shortLinkVo = new ShortLinkVo();
        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        shortLinkVo.setId(shortLinkId);
        shortLinkVo.setOriginalLinkVoList(new ArrayList<>());

        ShortLinkVo finalShortLinkVo = shortLinkVo;
        deepLinkList.forEach(deepLink -> {
            OriginalLinkVo vo = new OriginalLinkVo();
            vo.setOriginalId(deepLink.getId());
            vo.setOriginalLink(deepLink.getLink());
            finalShortLinkVo.getOriginalLinkVoList().add(vo);

        });

        this.shortLinkService.updateByVo(shortLinkVo);

        Assert.assertEquals(3, shortLinkVo.getOriginalLinkVoList().size());

        List<ShortLinkRelation> shortLinkRelationList = this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId);

        Assert.assertEquals(3, shortLinkRelationList.size());
        Assert.assertEquals(deepLinkList.get(0).getId(), shortLinkRelationList.get(0).getOriginalId());
        Assert.assertEquals(deepLinkList.get(1).getId(), shortLinkRelationList.get(1).getOriginalId());
    }

    @Test
    public void testUpdateVoWithAllDeepLinkDeleted() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();

        List<Integer> deepLinkIdList = new ArrayList<>();
        deepLinkIdList.add(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());

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

            Integer deepLinkId = null;
            try {
                this.deepLinkService.saveByVo(defaultVo);
                deepLinkId = this.deepLinkService.findAll().get(0).getId();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            this.addDeepLinkToShortLink(shortLinkId, deepLinkId);
            deepLinkIdList.add(deepLinkId);
        }

        Assert.assertEquals(4, this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId).size());

        shortLinkVo = new ShortLinkVo();
        shortLinkVo.setId(shortLinkId);
        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        shortLinkVo.setStatus(StatusEnum.OFF.getCode());

        this.shortLinkService.updateByVo(shortLinkVo);

        List<ShortLinkRelation> shortLinkRelationList = this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId);

        Assert.assertEquals(0, shortLinkRelationList.size());

        shortLink = this.shortLinkService.findById(shortLinkId);
        Assert.assertEquals(StatusEnum.OFF.getCode(), shortLink.getStatus());

    }

    @Test
    public void testUpdateVoWithSomeDeepLinkDeleted() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();

        List<Integer> deepLinkIdList = new ArrayList<>();
        deepLinkIdList.add(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());

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

            Integer deepLinkId = null;
            try {
                this.deepLinkService.saveByVo(defaultVo);
                deepLinkId = this.deepLinkService.findAll().get(0).getId();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            this.addDeepLinkToShortLink(shortLinkId, deepLinkId);
            deepLinkIdList.add(deepLinkId);
        }

        Assert.assertEquals(4, this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId).size());

        shortLinkVo = new ShortLinkVo();
        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        shortLinkVo.setId(shortLinkId);
        shortLinkVo.setOriginalLinkVoList(
                this.shortLinkService.findVoById(shortLinkId).getOriginalLinkVoList().subList(0, 2)
        );

        this.shortLinkService.updateByVo(shortLinkVo);

        List<ShortLinkRelation> shortLinkRelationList = this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId);

        Assert.assertEquals(2, shortLinkRelationList.size());
    }

    @Test
    public void testUpdateVoWithOriginalIdIsNull() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();

        this.shortLinkRelationMapper.deleteByShortLinkId(shortLinkId);

        shortLinkVo.setLinkType("other");
        shortLinkVo.setOriginalLinkVoList(
                Arrays.asList("http://www.gooddoctor.co.id/article/1", "http://www.gooddoctor.co.id/article/2", "http://www.gooddoctor.co.id/article/3")
                    .stream().map(link -> {
                        OriginalLinkVo originalLinkVo = new OriginalLinkVo();
                        originalLinkVo.setOriginalLink(link);
                        return originalLinkVo;
                    }).collect(Collectors.toList())
        );

        this.shortLinkService.updateByVo(shortLinkVo);


        List<ShortLinkRelation> shortLinkRelationList = this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId);

        Assert.assertEquals(3, shortLinkRelationList.size());
        Assert.assertNull(shortLinkRelationList.get(0).getOriginalId());
    }

    @Test
    public void testUpdateVoWithShortLinkConnectionIsNull() {
        ShortLink shortLink = new ShortLink();
        shortLink.setCode("abc123");
        shortLink.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        shortLink.setExpiredTime(new Date());
        shortLink.setName("test-short-name-" + System.currentTimeMillis());
        shortLink.setStatus(StatusEnum.ACTIVE.getCode());
        shortLink.setCreateTime(new Date());
        shortLink.setUpdateTime(new Date());
        shortLink.setCreator("frank");
        shortLink.setEditor("frank");

        this.shortLinkService.save(shortLink);

        Integer shortLinkId = this.shortLinkService.findAll().get(0).getId();

        List<Integer> deepLinkIdList = new ArrayList<>();

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

            Integer deepLinkId = null;
            try {
                this.deepLinkService.saveByVo(defaultVo);
                deepLinkId = this.deepLinkService.findAll().get(0).getId();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            deepLinkIdList.add(deepLinkId);
        }

        Assert.assertEquals(0, this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId).size());

        ShortLinkVo shortLinkVo = new ShortLinkVo();
        shortLinkVo.setId(shortLinkId);
        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        shortLinkVo.setOriginalLinkVoList(
                this.shortLinkService.findVoById(shortLinkId).getOriginalLinkVoList()
        );

        ShortLinkVo finalShortLinkVo = shortLinkVo;
        deepLinkIdList.forEach(id -> {
            OriginalLinkVo vo = new OriginalLinkVo();
            vo.setOriginalId(id);
            vo.setOriginalLink(this.deepLinkService.findById(id).getLink());
            finalShortLinkVo.getOriginalLinkVoList().add(vo);

        });

        this.shortLinkService.updateByVo(shortLinkVo);

        List<ShortLinkRelation> shortDeepLinkList = this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId);
        Assert.assertEquals(3, shortDeepLinkList.size());
    }

    @Test
    public void testUpdateVoWithSomeDeepLinkAdded() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();

        List<Integer> deepLinkIdList = new ArrayList<>();
        deepLinkIdList.add(shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());

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

            Integer deepLinkId = null;
            try {
                this.deepLinkService.saveByVo(defaultVo);
                deepLinkId = this.deepLinkService.findAll().get(0).getId();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            deepLinkIdList.add(deepLinkId);
        }

        Assert.assertEquals(1, this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId).size());

        shortLinkVo = new ShortLinkVo();
        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        shortLinkVo.setId(shortLinkId);

        shortLinkVo.setOriginalLinkVoList(
                this.shortLinkService.findVoById(shortLinkId).getOriginalLinkVoList()
        );

        ShortLinkVo finalShortLinkVo = shortLinkVo;
        deepLinkIdList.forEach(id -> {
            OriginalLinkVo vo = new OriginalLinkVo();
            vo.setOriginalId(id);
            vo.setOriginalLink(this.deepLinkService.findById(id).getLink());
            finalShortLinkVo.getOriginalLinkVoList().add(vo);

        });

        this.shortLinkService.updateByVo(shortLinkVo);

        List<ShortLinkRelation> shortLinkRelationList = this.shortLinkRelationMapper.selectByShortLinkId(shortLinkId);

        Assert.assertEquals(4, shortLinkRelationList.size());
        Assert.assertEquals(deepLinkIdList.get(0), shortLinkRelationList.get(0).getOriginalId());
        Assert.assertEquals(deepLinkIdList.get(1), shortLinkRelationList.get(1).getOriginalId());
    }

    @Test(expected = ServiceException.class)
    public void testUpdateByVoWithMaxRelationCount() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();

        List<DeepLink> deepLinkList = new ArrayList<>();

        for (int i = 0; i < this.configParam.getShortToOriginalLinkMaxCount(); i++) {
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

        try {
            shortLinkVo = new ShortLinkVo();
            shortLinkVo.setId(shortLinkId);
            shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());

            shortLinkVo.setOriginalLinkVoList(new ArrayList<>());

            ShortLinkVo finalShortLinkVo = shortLinkVo;
            deepLinkList.forEach(deepLink -> {
                OriginalLinkVo vo = new OriginalLinkVo();
                vo.setOriginalId(deepLink.getId());
                vo.setOriginalLink(deepLink.getLink());
                finalShortLinkVo.getOriginalLinkVoList().add(vo);

            });
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        this.shortLinkService.updateByVo(shortLinkVo);
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void testUpdateByVoWithIdIsNull() {
        this.shortLinkService.updateByVo(new ShortLinkVo());
    }

    @Test
    public void testListAllVo() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();

        List<ShortLinkVo> shortLinkVoList = this.shortLinkService.listAllVo();
        Assert.assertEquals(1, shortLinkVoList.size());
        Assert.assertEquals(1, shortLinkVoList.get(0).getOriginalLinkVoList().size());
    }

    @Test
    public void testFindIdByLinkTypeAndOriginalId() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();

        Integer id = this.shortLinkService.findIdByLinkTypeAndOriginalId(ShortLinkInfoEnum.DEEPLINK.getLinkType(), shortLinkVo.getOriginalLinkVoList().get(0).getOriginalId());
        Assert.assertEquals(shortLinkId, id);
    }

    @Test
    public void testFindIdByLinkType() {
        ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
        ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
        Integer shortLinkId = shortLink.getId();

        List<Integer> shortLinkIdList = this.shortLinkService.findIdByLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        Assert.assertEquals(1, shortLinkIdList.size());
        Assert.assertEquals(shortLinkId, shortLinkIdList.get(0));
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteById() {
        Integer shortLinkId = null;
        try {
            ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
            ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
            shortLinkId = shortLink.getId();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        this.shortLinkService.deleteById(shortLinkId);
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteByIds() {
        Integer shortLinkId = null;
        try {
            ShortLinkVo shortLinkVo = this.generateDefaultShortLinkVo();
            ShortLink shortLink = this.saveShortLinkVo(shortLinkVo);
            shortLinkId = shortLink.getId();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        this.shortLinkService.deleteByIds(String.valueOf(shortLinkId));
    }


    private ShortLinkVo generateDefaultShortLinkVo() {
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

        DeepLink deepLink = this.deepLinkService.findAll().get(0);

        ShortLinkVo shortLinkVo = new ShortLinkVo();
        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        shortLinkVo.setLinkPrefix(ShortLinkInfoEnum.DEEPLINK.getPrefix());

        OriginalLinkVo originalLinkVo = new OriginalLinkVo();
        originalLinkVo.setOriginalId(deepLink.getId());
        originalLinkVo.setOriginalLink(deepLink.getLink());
        shortLinkVo.setOriginalLinkVoList(Arrays.asList(originalLinkVo));
        shortLinkVo.setName("test-short-name");
        shortLinkVo.setExpiredTime(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000));
        shortLinkVo.setCreateTime(new Date());
        shortLinkVo.setUpdateTime(new Date());
        return shortLinkVo;
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

    private void addDeepLinkToShortLink(Integer shortLinkId, Integer deepLinkId) {
        DeepLink deepLink = this.deepLinkService.findById(deepLinkId);
        ShortLinkRelation relation = new ShortLinkRelation();
        relation.setShortLinkId(shortLinkId);
        relation.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        relation.setOriginalId(deepLinkId);
        relation.setOriginalLink(deepLink.getLink());
        this.shortLinkRelationMapper.insert(relation);
    }
}
