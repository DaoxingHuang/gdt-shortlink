package com.gdtc.deeplink.manager.service.impl;

import com.gdtc.deeplink.manager.configuration.ConfigParam;
import com.gdtc.deeplink.manager.convertor.DeepLinkMessageConvertor;
import com.gdtc.deeplink.manager.convertor.ShortLinkMessageConvertor;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.dao.ShortLinkRelationMapper;
import com.gdtc.deeplink.manager.dao.ShortLinkMapper;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.ShortLinkRelation;
import com.gdtc.deeplink.manager.model.ShortLink;
import com.gdtc.deeplink.manager.service.ShortLinkService;
import com.gdtc.deeplink.manager.core.AbstractService;
import com.gdtc.deeplink.manager.utils.ShortCodeGenerator;
import com.gdtc.deeplink.manager.vo.OriginalLinkVo;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import javax.management.relation.Relation;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by GDTC on 2020/11/30.
 */
@Service
@Transactional
public class ShortLinkServiceImpl extends AbstractService<ShortLink> implements ShortLinkService {
    private static final Logger logger = LoggerFactory.getLogger(ShortLinkServiceImpl.class);

    @Autowired
    private ConfigParam configParam;
    @Resource
    private ShortLinkMapper shortLinkMapper;
    @Resource
    private ShortLinkRelationMapper shortLinkRelationMapper;
//    @Resource
//    private DeepLinkMapper deepLinkMapper;
//    @Resource
//    private ShortLinkRelationMapper shortLinkRelationMapper;

    @Override
    public List<ShortLinkVo> listAllVo() {
        List<ShortLink> shortLinkList = super.findAll();
        List<ShortLinkRelation> shortLinkRelationList = this.shortLinkRelationMapper.selectAll();

        MultiValueMap<Integer, ShortLinkRelation> shortLinkRelationMap = new LinkedMultiValueMap<>();
        shortLinkRelationList.forEach(relation -> {
            shortLinkRelationMap.putIfAbsent(relation.getShortLinkId(), new ArrayList<>());
            shortLinkRelationMap.get(relation.getShortLinkId()).add(relation);
        });

        List<ShortLinkVo> shortLinkVoList = shortLinkList.stream().map(shortLink -> {
            ShortLinkVo vo = ShortLinkMessageConvertor.modelToVo(shortLink);

            if (shortLinkRelationMap.containsKey(shortLink.getId())) {
                vo.setOriginalLinkVoList(shortLinkRelationMap.get(shortLink.getId()).stream().map(relation -> {
                    OriginalLinkVo originalLinkVo = new OriginalLinkVo();
                    originalLinkVo.setId(relation.getId());
                    originalLinkVo.setOriginalId(relation.getOriginalId());
                    originalLinkVo.setOriginalLink(relation.getOriginalLink());
                    return originalLinkVo;
                }).collect(Collectors.toList()));
            }
            return vo;
        }).collect(Collectors.toList());

        return shortLinkVoList;
    }

    @Override
    public ShortLinkVo findVoById(Integer id) {
        if (null == id) {
            throw new ServiceException("invalid id.");
        }
        ShortLink shortLink = super.findById(id);
        List<ShortLinkRelation> shortLinkRelationList = this.shortLinkRelationMapper.selectByShortLinkId(shortLink.getId());

        ShortLinkVo vo = ShortLinkMessageConvertor.modelToVo(shortLink);
        if (null == shortLinkRelationList) {
            return vo;
        }
//        List<DeepLink> deepLinkList = this.deepLinkMapper.selectByIds(StringUtils.join(shortLinkRelationList.stream().map(ShortLinkRelation::getDeepLinkId).collect(Collectors.toList()), ","));
        vo.setOriginalLinkVoList(shortLinkRelationList.stream().map(relation -> {
            OriginalLinkVo originalLinkVo = new OriginalLinkVo();
            originalLinkVo.setId(relation.getId());
            originalLinkVo.setOriginalId(relation.getOriginalId());
            originalLinkVo.setOriginalLink(relation.getOriginalLink());
            return originalLinkVo;
        }).collect(Collectors.toList()));

        return vo;
    }

//    @Override
//    public List<ShortLinkVo> findVoByDeepLink(Integer deepLinkId) {
//        List<ShortLink> shortLinkList = this.findIdByLinkTypeAndOriginalId(deepLinkId).stream().map(shortLinkMapper::selectByPrimaryKey).collect(Collectors.toList());
//        DeepLink deepLink = this.deepLinkMapper.selectByPrimaryKey(deepLinkId);
//        return shortLinkList.stream().map(shortLink -> {
//            List<ShortLinkRelation> shortDeepLinkList = this.shortLinkRelationMapper.selectByShortLinkId(shortLink.getId());
//
//            ShortLinkVo vo = ShortLinkMessageConvertor.modelToVo(shortLink);
//            if (shortDeepLinkList.size() == 1) {
//                vo.setOriginalLinkVoList(Arrays.asList(deepLink).stream().map(DeepLinkMessageConvertor::modelToVo).collect(Collectors.toList()));
//            } else {
//                List<DeepLink> deepLinkList = this.deepLinkMapper.selectByIds(StringUtils.join(shortDeepLinkList.stream().map(ShortLinkRelation::getDeepLinkId).collect(Collectors.toList()), ","));
//                vo.setOriginalLinkVoList(deepLinkList.stream().map(DeepLinkMessageConvertor::modelToVo).collect(Collectors.toList()));
//            }
//            return vo;
//        }).collect(Collectors.toList());
//    }

    @Override
    public Integer findIdByLinkTypeAndOriginalId(String linkType, Integer originalId) {
        if (null == originalId || StringUtils.isBlank(linkType)) {
            throw new ServiceException("invalid param.");
        }

        ShortLinkRelation shortLinkRelation = this.shortLinkRelationMapper.selectByLinkTypeAndOriginalId(linkType, originalId);
        if (null == shortLinkRelation) {
            return null;
        }
        return shortLinkRelation.getShortLinkId();
    }

    @Override
    public List<Integer> findIdByLinkType(String linkType) {
        if (StringUtils.isBlank(linkType)) {
            throw new ServiceException("invalid param.");
        }

        List<ShortLinkRelation> shortDeepLinkList = this.shortLinkRelationMapper.selectByLinkType(linkType);
        if (CollectionUtils.isEmpty(shortDeepLinkList)) {
            return Collections.emptyList();
        }

        return shortDeepLinkList.stream().map(ShortLinkRelation::getShortLinkId).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        throw new RuntimeException("unsupported operation.");
    }

    @Override
    public void deleteByIds(String ids) {
        throw new RuntimeException("unsupported operation.");
    }

    @Override
    public Integer saveByVo(ShortLinkVo vo, OriginalLinkVo originalLinkVo) {
        if (null == vo
                || StringUtils.isBlank(vo.getName())
                || StringUtils.isBlank(vo.getLinkType())
                || StringUtils.isBlank(vo.getLinkPrefix())
                || null == vo.getExpiredTime()
                || null == originalLinkVo
                || StringUtils.isBlank(originalLinkVo.getOriginalLink())) {
            throw new ServiceException("invalid param.");
        }

        if (null != originalLinkVo.getOriginalId()) {
            Integer existingRelateShortLinkId = this.findIdByLinkTypeAndOriginalId(vo.getLinkType(), originalLinkVo.getOriginalId());
            if (null != existingRelateShortLinkId) {
                return existingRelateShortLinkId;
            }
        }

        String code = ShortCodeGenerator.generateCode(originalLinkVo.getOriginalLink());
        ShortLink sameCodeShortLink = this.shortLinkMapper.selectByCode(code);
        boolean isExisting = false;
        if (null != sameCodeShortLink) {
            logger.warn("existing code: {}, link type: {}, link: {}", code, vo.getLinkType(), originalLinkVo.getOriginalLink());
            code = ShortCodeGenerator.generateCode(System.currentTimeMillis() + originalLinkVo.getOriginalLink());
            sameCodeShortLink = this.shortLinkMapper.selectByCode(code);
            isExisting = null != sameCodeShortLink;
        }

        if (isExisting) {
            throw new ServiceException("generate code failed.");
        }
        logger.info("code: {}, link type: {}, link: {}", code, vo.getLinkType(), originalLinkVo.getOriginalLink());

        ShortLink shortLink = ShortLinkMessageConvertor.voToModel(vo);
        shortLink.setCode(code);
        StringBuilder linkBuilder = new StringBuilder();
        linkBuilder.append(this.configParam.getShortLinkDomain())
                .append(vo.getLinkPrefix())
                .append("/")
                .append(code);
        shortLink.setLink(linkBuilder.toString());
        this.shortLinkMapper.insert(shortLink);

        ShortLinkRelation relation = new ShortLinkRelation();
        relation.setShortLinkId(shortLink.getId());
        relation.setLinkType(shortLink.getLinkType());
        relation.setOriginalId(originalLinkVo.getOriginalId());
        relation.setOriginalLink(originalLinkVo.getOriginalLink());
        this.shortLinkRelationMapper.insert(relation);
//        this.buildConnection(shortLink.getId(), vo.getDeepLinkId());
        return shortLink.getId();
    }

    @Override
    public void updateByVo(ShortLinkVo vo) {
        if (null == vo.getId() || StringUtils.isBlank(vo.getLinkType())) {
            throw new ServiceException("invalid param.");
        }

        if (CollectionUtils.isEmpty(vo.getOriginalLinkVoList())) {
            this.shortLinkRelationMapper.deleteByShortLinkId(vo.getId());
//            this.shortLinkMapper.deleteByPrimaryKey(vo.getId());
            ShortLink newShortLink = ShortLinkMessageConvertor.voToModel(vo);
            this.shortLinkMapper.updateByPrimaryKeySelective(newShortLink);
            return;
        }

        if (vo.getOriginalLinkVoList().stream().anyMatch(originalLinkVo -> StringUtils.isBlank(originalLinkVo.getOriginalLink()))) {
            throw new ServiceException("originalLink can't be null.");
        }

        vo.setOriginalLinkVoList(vo.getOriginalLinkVoList().stream().distinct().collect(Collectors.toList()));

        ShortLink shortLink = super.findById(vo.getId());
        List<ShortLinkRelation> shortLinkRelationList = this.shortLinkRelationMapper.selectByShortLinkId(shortLink.getId());
        Set<Integer> deleteConnectionRelationIdSet = new HashSet<>();
        List<ShortLinkRelation> addConnectionRelationList = new ArrayList<>();
        if (CollectionUtils.isEmpty(shortLinkRelationList)) {
             addConnectionRelationList = vo.getOriginalLinkVoList().stream().map(originalLinkVo -> this.covert(shortLink, originalLinkVo)).collect(Collectors.toList());
        } else {
            Set<Integer> newRelationIdSet = vo.getOriginalLinkVoList().stream().map(OriginalLinkVo::getId).collect(Collectors.toSet());
            Set<Integer> oldRelationIdSet = shortLinkRelationList.stream().map(ShortLinkRelation::getId).collect(Collectors.toSet());
            addConnectionRelationList = vo.getOriginalLinkVoList().stream()
                    .filter(originalLinkVo -> null == originalLinkVo.getId())
                    .distinct()
                    .map(originalLinkVo -> this.covert(shortLink, originalLinkVo))
                    .collect(Collectors.toList());
            deleteConnectionRelationIdSet = oldRelationIdSet.stream().filter(relationId -> !newRelationIdSet.contains(relationId)).collect(Collectors.toSet());
        }

        for (ShortLinkRelation relation : addConnectionRelationList) {
            if (this.isHasRelation(relation.getOriginalId(), vo.getLinkType())) {
                throw new ServiceException("can't duplicate relate to ShortLink. originalId: " + relation.getOriginalId() + " linkType: " + vo.getLinkType());
            }
        }

        addConnectionRelationList.forEach(this::addOriginalLinkToShortLink);
        deleteConnectionRelationIdSet.forEach(relationId -> this.shortLinkRelationMapper.deleteByPrimaryKey(relationId));

        ShortLink newShortLink = ShortLinkMessageConvertor.voToModel(vo);
        this.shortLinkMapper.updateByPrimaryKeySelective(newShortLink);
    }

    /**
     * 保存relation，仅检查同一个shortLink下originalLink的个数，不再做参数校验
     * @param relation
     */
    private void addOriginalLinkToShortLink(ShortLinkRelation relation) {
        int existingRelationCount = this.shortLinkRelationMapper.countByShortLinkId(relation.getShortLinkId());
        if (existingRelationCount >= this.configParam.getShortToOriginalLinkMaxCount()) {
            throw new ServiceException("too many original link.");
        }

        this.shortLinkRelationMapper.insert(relation);
    }

    private ShortLinkRelation covert(ShortLink shortLink, OriginalLinkVo originalLinkVo) {
        ShortLinkRelation relation = new ShortLinkRelation();
        relation.setShortLinkId(shortLink.getId());
        relation.setOriginalId(originalLinkVo.getOriginalId());
        relation.setOriginalLink(originalLinkVo.getOriginalLink());
        relation.setLinkType(shortLink.getLinkType());
        return relation;
    }

    private boolean isHasRelation(Integer originalId, String linkType) {
        if (null == originalId) {
            return false;
        }

        Integer existingRelateShortLinkId = this.findIdByLinkTypeAndOriginalId(linkType, originalId);
        return null != existingRelateShortLinkId;
    }
}
