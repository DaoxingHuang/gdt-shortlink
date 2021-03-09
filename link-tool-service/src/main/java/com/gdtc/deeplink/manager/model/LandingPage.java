package com.gdtc.deeplink.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.*;

@Table(name = "LANDING_PAGE")
public class LandingPage {
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{[a-zA-Z0-9_-]+}");
    public static final String PARAM_PREFIX = "\\{";
    public static final String PARAM_SUFFIX = "}";
    private static final Pattern PARAM_PREFIX_PATTERN = Pattern.compile(PARAM_PREFIX);
    private static final Pattern PARAM_SUFFIX_PATTERN = Pattern.compile(PARAM_SUFFIX);

    /**
     * id
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 模块
     */
    @Column(name = "MODULE")
    private String module;

    @Column(name = "NAME")
    private String name;

    /**
     * 子模块
     */
    @Column(name = "SCHEME_NAME")
    private String schemeName;

    /**
     * path模板
     */
    @Column(name = "PATH_TEMPLATE")
    private String pathTemplate;

    /**
     * 名称
     */
//    @Column(name = "NAME")
//    private String name;
    @Column(name = "IS_NATIVE")
    private Boolean isNative;

    /**
     * 业务平台
     */
    @Column(name = "PLATFORM")
    private String platform;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 创建者
     */
    @Column(name = "CREATOR")
    private String creator;

    /**
     * 修改者
     */
    @Column(name = "EDITOR")
    private String editor;

    /**
     * 获取id
     *
     * @return ID - id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取模块
     *
     * @return MODULE - 模块
     */
    public String getModule() {
        return module;
    }

    /**
     * 设置模块
     *
     * @param module 模块
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * 获取子模块
     *
     * @return SUB_MODULE - 子模块
     */
    public String getSchemeName() {
        return schemeName;
    }

    /**
     * 设置子模块
     *
     * @param schemeName 子模块
     */
    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    /**
     * 获取path模板
     *
     * @return PATH_TEMPLATE - path模板
     */
    public String getPathTemplate() {
        return pathTemplate;
    }

    /**
     * 设置path模板
     *
     * @param pathTemplate path模板
     */
    public void setPathTemplate(String pathTemplate) {
        this.pathTemplate = pathTemplate;
    }

    public Boolean getIsNative() {
        return isNative;
    }

    public Boolean isNative() {
        return isNative;
    }

    public void setNative(Boolean aNative) {
        isNative = aNative;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取业务平台
     *
     * @return PLATFORM - 业务平台
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 设置业务平台
     *
     * @param platform 业务平台
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * 获取创建时间
     *
     * @return CREATE_TIME - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return UPDATE_TIME - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取创建者
     *
     * @return CREATOR - 创建者
     */
    public String getCreator() {
        return creator;
    }

    /**
     * 设置创建者
     *
     * @param creator 创建者
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * 获取修改者
     *
     * @return EDITOR - 修改者
     */
    public String getEditor() {
        return editor;
    }

    /**
     * 设置修改者
     *
     * @param editor 修改者
     */
    public void setEditor(String editor) {
        this.editor = editor;
    }


    /**
     * check whether is a valid path template.
     * if the template need params, the param will be around by {},
     * such as /health-mall/#/market/{param}, /eval-id/#/config/home?countryCode={countryCode}&version={version}
     * @return
     */
    @JsonIgnore
    public boolean isValidPathTemplate() {
        int prefixCount = this.getMatcherGroupCount(this.pathTemplate, PARAM_PREFIX_PATTERN);
        int suffixCount = this.getMatcherGroupCount(this.pathTemplate, PARAM_SUFFIX_PATTERN);

        if (prefixCount == suffixCount) {
            // no param
            if (prefixCount == 0) {
                return true;
            }

            int paramCount = this.getMatcherGroupCount(this.pathTemplate, PARAM_PATTERN);
            if (prefixCount == paramCount) {
                return true;
            }
        }

        return false;
    }

    private int getMatcherGroupCount(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     *
     * @param paramMap
     * @return
     */
    public String generatePath(Map<String, String> paramMap) {
        Matcher paramMatcher = PARAM_PATTERN.matcher(this.pathTemplate);
        if (!paramMatcher.find()) {
            return this.pathTemplate;
        }

        if (null == paramMap) {
            return null;
        }

        String path = this.pathTemplate;
        do {
            String param = paramMatcher.group();
            String paramKey = param.substring(1, param.length() - 1);
            String value = paramMap.get(paramKey);
            if (StringUtils.isBlank(value)) {
                return null;
            }
            path = path.replace(param, value);
        } while (paramMatcher.find());

        return path;
    }

    public List<String> parseParam() {
        Matcher paramMatcher = PARAM_PATTERN.matcher(this.pathTemplate);
        List<String> paramList = new ArrayList<>();
        while (paramMatcher.find()) {
            String paramLabel = paramMatcher.group();
            paramList.add(paramLabel.substring(1, paramLabel.length() - 1));
        }
        return paramList;
    }
}