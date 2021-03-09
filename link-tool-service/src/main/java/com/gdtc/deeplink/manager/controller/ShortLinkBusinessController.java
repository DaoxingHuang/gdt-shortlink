package com.gdtc.deeplink.manager.controller;


import com.gdtc.deeplink.manager.core.ResultGenerator;
import com.gdtc.deeplink.manager.service.ShortLinkBusinessService;
import com.gdtc.deeplink.manager.utils.Constants;
import com.gdtc.deeplink.manager.vo.DeepLinkVo;
import com.gdtc.deeplink.manager.vo.GDTOneLinkVo;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;
import com.gdtc.link.api.core.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("short_link_business")
public class ShortLinkBusinessController {
    @Resource
    private ShortLinkBusinessService shortLinkBusinessService;

    @PostMapping(value = "/generateForDeepLink", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generation ShortLink for a DeepLink", notes = "If the DeepLink had connect to a ShortLink, this API will not create new ShortLink.")
    public Result generateForDeepLink(@RequestParam(name = "deepLinkId") Integer deeplinkId, @RequestBody ShortLinkVo shortLinkVo) {
        this.shortLinkBusinessService.generateForDeepLink(deeplinkId, shortLinkVo);
        return ResultGenerator.genSuccessResult();
    }


    @PostMapping(value = "/generateForOneLink", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generation ShortLink for a GDTOneLink", notes = "If the GDTOneLink had connect to a ShortLink, this API will not create new ShortLink.")
    public Result generateForOneLink(@RequestParam(name = "gdtOneLinkId") Integer gdtOneLinkId, @RequestBody ShortLinkVo shortLinkVo) {
        this.shortLinkBusinessService.generateForGDTOneLink(gdtOneLinkId, shortLinkVo);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/findIdByDeepLink")
    @ApiOperation(value = "find all connected ShortLink's Id by DeepLink")
    public Result<Integer> findIdByDeepLink(@RequestParam(name = "deepLinkId") Integer deepLinkId) {
        Integer shortLinkId = this.shortLinkBusinessService.findShortLinkIdByOriginalIdForDeepLink(deepLinkId);
        return ResultGenerator.genSuccessResult(shortLinkId);
    }

    @GetMapping("/findIdByGDTOneLink")
    @ApiOperation(value = "find all connected ShortLink's Id by GDTOneLink")
    public Result<Integer> findIdByGDTOneLink(@RequestParam(name = "gdtOneLinkId") Integer gdtOneLinkId) {
        Integer shortLinkId = this.shortLinkBusinessService.findShortLinkIdByOriginalIdForOneLink(gdtOneLinkId);
        return ResultGenerator.genSuccessResult(shortLinkId);
    }

    @GetMapping(value = "/listDeepLinkWithoutRelation")
    @ApiOperation(value = "list all DeppLink that don't relate to any ShortLink")
    public Result<List<DeepLinkVo>> listDeepLinkWithoutRelation() {
        List<DeepLinkVo> deepLinkVoList = this.shortLinkBusinessService.listDeepLinkVoWithoutRelation();
        return ResultGenerator.genSuccessResult(deepLinkVoList);
    }

    @GetMapping(value = "/listGDTOneLinkWithoutRelation")
    @ApiOperation(value = "list all DeppLink that don't relate to any ShortLink")
    public Result<List<GDTOneLinkVo>> listGDTOneLinkWithoutRelation() {
        List<GDTOneLinkVo> gdtOneLinkVoList = this.shortLinkBusinessService.listGDTOneLinkVoWithoutRelation();
        return ResultGenerator.genSuccessResult(gdtOneLinkVoList);
    }

    @GetMapping("/listDeepLink")
    @ApiOperation(value = "list all DeepLink", produces = "application/json")
    public Result<List<DeepLinkVo>> listAllDeepLink() {
        List<DeepLinkVo> list = this.shortLinkBusinessService.listAllDeepLinkVo();
        return ResultGenerator.genSuccessResult(list);
    }

    @GetMapping("/listGDTOneLink")
    @ApiOperation(value = "list all GDTOneLink", produces = "application/json")
    public Result<List<GDTOneLinkVo>> listGDTOneLink() {
        List<GDTOneLinkVo> list = this.shortLinkBusinessService.listAllGDTOneLinkVo();
        return ResultGenerator.genSuccessResult(list);
    }

    @PostMapping("/updateShortLink")
    @ApiOperation(value = "update the ShortLink's info, exclude \"code\"")
    public Result updateShortLink(@RequestBody ShortLinkVo shortLinkVo) {
        this.shortLinkBusinessService.updateShortLinkByVo(shortLinkVo);
        return ResultGenerator.genSuccessResult();
    }
}
