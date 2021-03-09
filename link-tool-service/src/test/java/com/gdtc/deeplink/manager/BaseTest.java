package com.gdtc.deeplink.manager;

import com.gdtc.deeplink.manager.filter.SSOUserInfo;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@Rollback
public class BaseTest {
    @Before
    public void setUp() {
        SSOUserInfo userInfo = new SSOUserInfo();
        userInfo.setUsername("frank.zhao");
        ThreadUserInfo.setUserInfo(userInfo);
    }
}
