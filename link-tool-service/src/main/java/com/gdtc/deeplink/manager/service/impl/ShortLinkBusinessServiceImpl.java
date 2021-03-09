package com.gdtc.deeplink.manager.service.impl;

import com.gdtc.deeplink.manager.convertor.DeepLinkMessageConvertor;
import com.gdtc.deeplink.manager.convertor.GDTOneLinkMessageConvertor;
import com.gdtc.deeplink.manager.convertor.LandingPageMessageConvertor;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.dao.DeepLinkMapper;
import com.gdtc.deeplink.manager.dao.GDTOneLinkMapper;
import com.gdtc.deeplink.manager.dao.LandingPageMapper;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.GDTOneLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.service.LandingPageService;
import com.gdtc.deeplink.manager.service.ShortLinkBusinessService;
import com.gdtc.deeplink.manager.service.ShortLinkService;
import com.gdtc.deeplink.manager.utils.DateUtils;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.utils.ShortLinkInfoEnum;
import com.gdtc.deeplink.manager.vo.*;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShortLinkBusinessServiceImpl implements ShortLinkBusinessService {
    public static final Logger logger = LoggerFactory.getLogger(ShortLinkBusinessServiceImpl.class);

    private static final int SEED = 20210126;
    private static final HashFunction MURMUR_HASH = Hashing.murmur3_32(SEED);

    @Autowired
    private ShortLinkService shortLinkService;
    @Autowired
    private LandingPageService landingPageService;
    @Autowired
    private DeepLinkMapper deepLinkMapper;
    @Autowired
    private GDTOneLinkMapper gdtOneLinkMapper;
    @Autowired
    private LandingPageMapper landingPageMapper;

    @Override
    public void generateForDeepLink(Integer deepLinkId, ShortLinkVo shortLinkVo) {
        if (null == deepLinkId || null == shortLinkVo
                || StringUtils.isBlank(shortLinkVo.getName())
                || null == shortLinkVo.getExpiredTime()) {
            throw new ServiceException("invalid param.");
        }

        DeepLink deepLink = this.deepLinkMapper.selectByPrimaryKey(deepLinkId);
        if (null == deepLink || PlatformEnum.GH.getCode().equalsIgnoreCase(deepLink.getPlatform())) {
            throw new ServiceException("invalid deepLink.");
        }

        Integer shortLinkId = this.findShortLinkIdByOriginalIdForDeepLink(deepLinkId);
        if (null != shortLinkId) {
            logger.warn("deepLink {} had map to shortLink {}", deepLink, shortLinkId);
            return;
        }

        shortLinkVo.setLinkType(ShortLinkInfoEnum.DEEPLINK.getLinkType());
        shortLinkVo.setLinkPrefix(ShortLinkInfoEnum.DEEPLINK.getPrefix());

        OriginalLinkVo originalLinkVo = new OriginalLinkVo();
        originalLinkVo.setOriginalLink(deepLink.getLink());
        originalLinkVo.setOriginalId(deepLinkId);

        this.shortLinkService.saveByVo(shortLinkVo, originalLinkVo);
    }

    @Override
    public void generateForGDTOneLink(Integer gdtOneLinkId, ShortLinkVo shortLinkVo) {
        if (null == gdtOneLinkId || null == shortLinkVo
                || StringUtils.isBlank(shortLinkVo.getName())
                || null == shortLinkVo.getExpiredTime()) {
            throw new ServiceException("invalid param.");
        }

        GDTOneLink gdtOneLink = this.gdtOneLinkMapper.selectByPrimaryKey(gdtOneLinkId);
        if (null == gdtOneLink) {
            throw new ServiceException("invalid deepLink.");
        }

        shortLinkVo.setLinkType(ShortLinkInfoEnum.GDT_ONE_LINK.getLinkType());
        shortLinkVo.setLinkPrefix(ShortLinkInfoEnum.GDT_ONE_LINK.getPrefix());

        OriginalLinkVo originalLinkVo = new OriginalLinkVo();
        originalLinkVo.setOriginalLink(gdtOneLink.getLink());
        originalLinkVo.setOriginalId(gdtOneLinkId);

        this.shortLinkService.saveByVo(shortLinkVo, originalLinkVo);
    }

    @Override
    public ShortLinkVo generateForCommon(ShortLinkAPIVo shortLinkAPIVo) {
        if (null == shortLinkAPIVo || shortLinkAPIVo.getExpiredTime() <= System.currentTimeMillis()
                || StringUtils.isAnyBlank(shortLinkAPIVo.getCreator(), shortLinkAPIVo.getOriginalLink())) {
            throw new ServiceException("invalid param.");
        }

        ShortLinkVo shortLinkVo = new ShortLinkVo();
        shortLinkVo.setLinkType(ShortLinkInfoEnum.COMMON.getLinkType());
        shortLinkVo.setLinkPrefix(ShortLinkInfoEnum.COMMON.getPrefix());
        shortLinkVo.setExpiredTime(new Date(shortLinkAPIVo.getExpiredTime()));

        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append("common-").append(DateUtils.getDateHour(new Date()))
                .append("-").append(Math.abs(MURMUR_HASH.hashString(shortLinkAPIVo.getOriginalLink(), Charset.defaultCharset()).asInt() / 1000));
        shortLinkVo.setName(nameBuilder.toString());
        shortLinkVo.setCreateTime(new Date());
        shortLinkVo.setUpdateTime(new Date());

        OriginalLinkVo originalLinkVo = new OriginalLinkVo();
        originalLinkVo.setOriginalLink(shortLinkAPIVo.getOriginalLink());

        Integer shortLinkId = this.shortLinkService.saveByVo(shortLinkVo, originalLinkVo);
        return this.shortLinkService.findVoById(shortLinkId);
    }

    @Override
    public Integer findShortLinkIdByOriginalIdForDeepLink(Integer originalId) {
        return this.findShortLinkIdByLinkTypeAndOriginalId(ShortLinkInfoEnum.DEEPLINK.getLinkType(), originalId);
    }

    @Override
    public Integer findShortLinkIdByOriginalIdForOneLink(Integer originalId) {
        return this.findShortLinkIdByLinkTypeAndOriginalId(ShortLinkInfoEnum.GDT_ONE_LINK.getLinkType(), originalId);
    }

    private Integer findShortLinkIdByLinkTypeAndOriginalId(String linkType, Integer originalId) {
        return this.shortLinkService.findIdByLinkTypeAndOriginalId(linkType, originalId);
    }

    @Override
    public List<DeepLinkVo> listDeepLinkVoWithoutRelation() {
        return this.deepLinkMapper.selectWithoutRelation().stream()
                .map(DeepLinkMessageConvertor::modelToVo)
                .map(deepLinkVo -> {
                    LandingPage landingPage = this.landingPageMapper.selectByPrimaryKey(deepLinkVo.getLandingPageId());
                    deepLinkVo.setLandingPageVo(LandingPageMessageConvertor.modelToVo(landingPage));
                    return deepLinkVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<GDTOneLinkVo> listGDTOneLinkVoWithoutRelation() {
        return this.gdtOneLinkMapper.selectWithoutRelation().stream()
                .map(GDTOneLinkMessageConvertor::modelToVo)
                .map(gdtOneLinkVo -> {
                    LandingPage landingPage = this.landingPageMapper.selectByPrimaryKey(gdtOneLinkVo.getLandingPageId());
                    gdtOneLinkVo.setLandingPageVo(LandingPageMessageConvertor.modelToVo(landingPage));
                    return gdtOneLinkVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DeepLinkVo> listAllDeepLinkVo() {
        // landingPage总数不会很多，所以可以全部加载。
        Map<Integer, LandingPageVo> landingPageVoMap = this.landingPageService.findAllVoWithMap();
        return this.deepLinkMapper.selectAll().stream()
                .map(DeepLinkMessageConvertor::modelToVo)
                .map(deepLinkVo -> {
                    // deepLink和shortLink基本上会保持一对一的（支持多对一，但是应该比较少用到）映射关系，所以没一个单独加载。
                    deepLinkVo.setShortLinkId(this.findShortLinkIdByOriginalIdForDeepLink(deepLinkVo.getId()));
                    deepLinkVo.setLandingPageVo(landingPageVoMap.get(deepLinkVo.getLandingPageId()));
                    return deepLinkVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<GDTOneLinkVo> listAllGDTOneLinkVo() {
        Map<Integer, LandingPageVo> landingPageVoMap = this.landingPageService.findAllVoWithMap();
        return this.gdtOneLinkMapper.selectAll().stream()
                .map(GDTOneLinkMessageConvertor::modelToVo)
                .map(gdtOneLinkVo -> {
                    gdtOneLinkVo.setShortLinkId(this.findShortLinkIdByOriginalIdForOneLink(gdtOneLinkVo.getId()));
                    gdtOneLinkVo.setLandingPageVo(landingPageVoMap.get(gdtOneLinkVo.getLandingPageId()));
                    return gdtOneLinkVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateShortLinkByVo(ShortLinkVo shortLinkVo) {
        if (null == shortLinkVo.getId() || StringUtils.isBlank(shortLinkVo.getLinkType())) {
            throw new ServiceException("invalid param.");
        }

        if (!CollectionUtils.isEmpty(shortLinkVo.getOriginalLinkVoList())) {
            if (ShortLinkInfoEnum.DEEPLINK.getLinkType().equalsIgnoreCase(shortLinkVo.getLinkType())) {
                shortLinkVo.getOriginalLinkVoList().forEach(originalLinkVo -> {
                    originalLinkVo.setOriginalLink(this.deepLinkMapper.selectByPrimaryKey(originalLinkVo.getOriginalId()).getLink());
                });
            } else if (ShortLinkInfoEnum.GDT_ONE_LINK.getLinkType().equalsIgnoreCase(shortLinkVo.getLinkType())) {
                shortLinkVo.getOriginalLinkVoList().forEach(originalLinkVo -> {
                    originalLinkVo.setOriginalLink(this.gdtOneLinkMapper.selectByPrimaryKey(originalLinkVo.getOriginalId()).getLink());
                });
            }
        }

        this.shortLinkService.updateByVo(shortLinkVo);
    }
}
