package com.gdtc.deeplink.manager.controller;

import com.gdtc.deeplink.manager.core.ResultGenerator;
import com.gdtc.deeplink.manager.filter.SSOUserInfo;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import com.gdtc.link.api.core.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Api(tags = "User APIs")
public class LoginController {

    @GetMapping(value = "/user/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("get current login user's info")
    public Result getUserInfo(HttpServletRequest request, HttpServletResponse response) {

        SSOUserInfo userInfo = ThreadUserInfo.getUserInfo();
        return ResultGenerator.genSuccessResult(userInfo);
    }

    @GetMapping("/logout")
    @ApiOperation("logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ThreadUserInfo.clear();
        request.getSession().removeAttribute("userInfo");
        response.sendRedirect("http://sso.gdtidtool.com/api/logout");
//        return ResultGenerator.genSuccessResult("http://sso.gdtidtool.com/api/logout");
    }
}
