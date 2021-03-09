package com.gdtc.deeplink.manager.controller;

import com.gdtc.deeplink.manager.core.ResultGenerator;
import com.gdtc.deeplink.manager.service.ShortLinkService;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;
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
@RequestMapping("short_link")
@Api(tags = "ShortLink APIs")
public class ShortLinkController {
    @Resource
    private ShortLinkService shortLinkService;

//    @PostMapping("/update")
//    @ApiOperation(value = "update the ShortLink's info, exclude \"code\"")
//    public Result updateShortLink(@RequestBody ShortLinkVo shortLinkVo) {
//        this.shortLinkService.updateByVo(shortLinkVo);
//        return ResultGenerator.genSuccessResult();
//    }


    @GetMapping("/list")
    @ApiOperation(value = "list all ShortLink")
    public Result<List<ShortLinkVo>> list() {
        List<ShortLinkVo> voList = shortLinkService.listAllVo();
        return ResultGenerator.genSuccessResult(voList);
    }
}
