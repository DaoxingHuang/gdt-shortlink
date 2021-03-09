package com.gdtc.deeplink.manager.controller;

import com.gdtc.deeplink.manager.configuration.ConfigParam;
import com.gdtc.deeplink.manager.core.ResultGenerator;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.model.SAAScheme;
import com.gdtc.deeplink.manager.service.LandingPageService;
import com.gdtc.deeplink.manager.utils.PlatformEnum;
import com.gdtc.deeplink.manager.vo.LandingPageVo;
import com.gdtc.deeplink.manager.vo.ShortLinkVo;
import com.gdtc.link.api.core.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * LandingPage APIs
 * @apiNote 这是落地页的API列表，包括：创建、删除、查询等
 * @author frank
 * @date 2020/11/30.
 */
@RestController
@RequestMapping("landing_page")
@Api(tags = "LandingPage APIs")
public class LandingPageController {
    @Resource
    private LandingPageService landingPageService;
    @Autowired
    private ConfigParam configParam;

    /**
     * Create LandingPage
     * @apiNote 这是关于这个接口的说明
     * @author Frank
     * @param landingPageVo com.gdtc.deeplink.manager.vo.LandingPageVo
     * @return 操作结果
     *
     */
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add a LandingPage")
    public Result add(@RequestBody LandingPageVo landingPageVo) {
        landingPageService.saveByVo(landingPageVo);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * delete LandingPage
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation(value = "delete a LandingPage")
    public Result delete(@RequestParam Integer id) {
        landingPageService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

//    @GetMapping("/detail")
//    public Result detail(@RequestParam Integer id) {
//        LandingPage landingPage = landingPageService.findById(id);
//        return ResultGenerator.genSuccessResult(landingPage);
//    }

    /**
     * list all LandingPage with paignation.
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "list all LandingPage by pagination")
    public Result<PageInfo<LandingPageVo>> list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<LandingPage> list = landingPageService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * list all LandingPage
     *
     * @author Frank
     * @param platform SAA or GH
     * @return
     */
    @GetMapping(value = "/listAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "list all LandingPage")
    public Result<List<LandingPageVo>> listAll(@RequestParam(name = "platform") String platform) {
        List<LandingPageVo> list = landingPageService.findAllVoByPlatform(PlatformEnum.valueOf(platform));
        return ResultGenerator.genSuccessResult(list);
    }

    /**
     * list all modules
     * @return
     */
    @GetMapping(value = "/module", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "list all modules")
    public Result<List<String>> listModules() {
        return ResultGenerator.genSuccessResult(this.configParam.getModuleList());
    }

    /**
     * list SAA's scheme
     *
     * @return
     */
    @GetMapping("/saascheme")
    @ApiOperation(value = "list all SAA's scheme")
    public Result<List<SAAScheme>> listSAAScheme() {
        return ResultGenerator.genSuccessResult(this.configParam.getSAASchemeList());
    }
}
