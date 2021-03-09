package com.gdtc.deeplink.manager.controller;

import com.gdtc.deeplink.manager.core.ResultGenerator;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.service.DeepLinkService;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.vo.DeepLinkVo;
import com.gdtc.link.api.core.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by GDTC on 2020/11/30.
*/
@RestController
@RequestMapping("deep_link")
@Api(tags = "DeepLink APIs")
public class DeepLinkController {
    @Resource
    private DeepLinkService deepLinkService;

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add a DeepLink", consumes = "application/json")
    public Result add(@RequestBody DeepLinkVo deepLinkVo) {
        deepLinkService.saveByVo(deepLinkVo);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/expire")
    @ApiOperation(value = "expire a DeepLink")
    public Result expireById(@RequestParam(name = "id") Integer id) {
        this.deepLinkService.expireById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/activate")
    @ApiOperation(value = "activate a GDTOneLink")
    public Result reactiveById(@RequestParam(name = "id") Integer id) {
        this.deepLinkService.activateById(id);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping(value = "/detail")
    @ApiOperation(value = "get a DeepLink's detail info")
    public Result<DeepLinkVo> getDetail(@RequestParam("id") Integer id) {
        DeepLinkVo deepLinkVo = this.deepLinkService.findVoById(id);
        return ResultGenerator.genSuccessResult(deepLinkVo);
    }

    @PostMapping(value = "/updateUTM", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update a DeepLink's UTM param", consumes = "application/json")
    public Result updateUTM(@RequestBody DeepLinkVo deepLinkVo) {
        deepLinkService.updateUTMByVo(deepLinkVo);
        return ResultGenerator.genSuccessResult();
    }
//
//    @GetMapping("/list")
//    @ApiOperation(value = "list all DeepLink", produces = "application/json")
//    public Result<List<DeepLinkVo>> list() {
////        PageHelper.startPage(page, size);
//        List<DeepLinkVo> list = deepLinkService.findAllVo();
////        PageInfo pageInfo = new PageInfo(list);
//        return ResultGenerator.genSuccessResult(list);
//    }

    @GetMapping("/listByPlatform")
    @ApiOperation(value = "list all DeepLink by platform", produces = "application/json")
    public Result<List<DeepLinkVo>> listByPlatform(@RequestParam(name = "platform") String platform) {
        List<DeepLinkVo> list = deepLinkService.findVoByPlatform(PlatformEnum.valueOf(platform));
        return ResultGenerator.genSuccessResult(list);
    }
}
