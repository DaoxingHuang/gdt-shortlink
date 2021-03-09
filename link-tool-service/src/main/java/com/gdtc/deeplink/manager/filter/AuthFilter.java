package com.gdtc.deeplink.manager.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gdtc.deeplink.manager.core.ResultGenerator;
import com.gdtc.deeplink.manager.utils.EnvEnum;
import com.gdtc.link.api.core.Result;
import com.gdtc.link.api.core.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.gdtc.deeplink.manager.utils.Constants.*;

@WebFilter(filterName = "ssoFilter", urlPatterns = "/*")
@Component
public class AuthFilter implements Filter {
    public static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    private static final String USER_INFO_URL = "http://sso.gdtidtool.com/v2/auth/auth_sso_token_api?token_cookie=";
    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("/sso.html", "/public/.*", "/dist/.*", "/vendor/.*", "/api/.*")));

    private static final Set<String> API_DOC_PATHS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("/swagger-ui.html", "/v2/api-docs", "/webjars/*", "/swagger*")));

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${spring.profiles.active}")
    private String env;

    @Override
    public void destroy() {

    }

    private boolean matchAllowedPath(String path) {
        for (String regPath : ALLOWED_PATHS) {
            if (path.matches(regPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchAPIDocsPath(String path) {
        for (String regPath : API_DOC_PATHS) {
            if (path.matches(regPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "/");
        boolean allowedPath = matchAllowedPath(path);
        if (allowedPath) {
            filterChain.doFilter(request, response);
        } else {
            if (matchAPIDocsPath(path)) {
                // only in dev and test environment, api doc is allowed to request.
                if (!(EnvEnum.DEV.getName().equals(this.env) || EnvEnum.TEST.getName().equals(this.env))) {
                    this.writeResponse(BLANK_STRING, response);
                    return;
                }
            }

            SSOUserInfo user = this.getSSOUser(request);
            if (null == user) {
                String fullUrl = request.getRequestURL().toString();
                if (fullUrl.endsWith("/")) {
                    String url = contextPath + "/sso.html?redirect_url=" + fullUrl;
                    response.sendRedirect(url);
                } else {
                    String url = this.contextPath + "/sso.html?redirect_url=" + fullUrl.substring(0, fullUrl.indexOf(this.contextPath)) + this.contextPath + "/";
                    Result<String> result = ResultGenerator.genUnauthorizedResult(url);
                    response.setStatus(ResultCode.UNAUTHORIZED.code());
                    this.writeResponse(JSON.toJSONString(result), response);
                }
            } else {
                HttpSession session = request.getSession();
                if (null == session.getAttribute(USER_INFO_KEY)) {
                    session.setAttribute(USER_INFO_KEY, user);
                }
                ThreadUserInfo.setUserInfo(user);
                filterChain.doFilter(request, response);
                ThreadUserInfo.clear();
            }
        }
    }

    public SSOUserInfo getSSOUser(HttpServletRequest request) {
        if (null != request.getSession().getAttribute(USER_INFO_KEY)) {
            SSOUserInfo userInfo = (SSOUserInfo) request.getSession().getAttribute(USER_INFO_KEY);
            return userInfo;
        }

        String ssoToken = this.getCookie(request.getCookies());
        if (StringUtils.isEmpty(ssoToken)) {
            return null;
        }

        return this.getUserInfoByToken(ssoToken);
    }

    private String getCookie(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (SSO_TOKEN_KEY.equalsIgnoreCase(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private SSOUserInfo getUserInfoByToken(String token) {
        try {
            String url = USER_INFO_URL + token;
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(url, String.class);
            logger.info(result);
            JSONObject ssoResult = JSON.parseObject(result);
            return ssoResult.getObject(SSO_USER_INFO_KEY, SSOUserInfo.class);
        } catch (Exception e) {
            logger.error("get sso error, token=" + token, e);
        }
        return null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    private void writeResponse(String responseContext, HttpServletResponse response) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(responseContext);
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
