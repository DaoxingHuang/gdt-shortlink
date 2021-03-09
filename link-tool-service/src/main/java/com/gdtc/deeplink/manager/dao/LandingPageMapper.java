package com.gdtc.deeplink.manager.dao;

import com.gdtc.deeplink.manager.core.Mapper;
import com.gdtc.deeplink.manager.model.LandingPage;

import java.util.List;

public interface LandingPageMapper extends Mapper<LandingPage> {
    List<LandingPage> selectByPlatform(String platform);
}