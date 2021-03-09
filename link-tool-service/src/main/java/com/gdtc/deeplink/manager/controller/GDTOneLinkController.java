package com.gdtc.deeplink.manager.controller;

import com.gdtc.deeplink.manager.core.ResultGenerator;
import com.gdtc.deeplink.manager.service.GDTOneLinkService;
import com.gdtc.deeplink.manager.vo.GDTOneLinkVo;
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
@RequestMapping("one_link")
@Api(tags = "GDTOneLink APIs")
public class GDTOneLinkController {
    @Resource
    private GDTOneLinkService oneLinkService;

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add a GDTOneLink", consumes = "application/json")
    public Result add(@RequestBody GDTOneLinkVo oneLinkVo) {
        oneLinkService.saveByVo(oneLinkVo);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/expire")
    @ApiOperation(value = "expire a GDTOneLink")
    public Result expireById(@RequestParam(name = "id") Integer id) {
        this.oneLinkService.expireById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/activate")
    @ApiOperation(value = "activate a GDTOneLink")
    public Result reactiveById(@RequestParam(name = "id") Integer id) {
        this.oneLinkService.activateById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping(value = "/updateUTM", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update a GDTOneLink's UTM param", consumes = "application/json")
    public Result updateUTM(@RequestBody GDTOneLinkVo oneLinkVo) {
        oneLinkService.updateUTMByVo(oneLinkVo);
        return ResultGenerator.genSuccessResult();
    }


    @GetMapping(value = "/detail")
    @ApiOperation(value = "get a DeepLink's detail info")
    public Result<GDTOneLinkVo> getDetail(@RequestParam("id") Integer id) {
        GDTOneLinkVo gdtOneLinkVo = this.oneLinkService.findVoById(id);
        return ResultGenerator.genSuccessResult(gdtOneLinkVo);
    }
}
