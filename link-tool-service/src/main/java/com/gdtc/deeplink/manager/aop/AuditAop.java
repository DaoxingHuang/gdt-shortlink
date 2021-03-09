package com.gdtc.deeplink.manager.aop;

import com.alibaba.fastjson.JSON;
import com.gdtc.deeplink.manager.filter.ThreadUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class AuditAop {
    private static final Logger auditLogger = LoggerFactory.getLogger("audit");
    private static final List<String> READ_METHOD_PREFIX = Arrays.asList("select", "find", "list");

    @Pointcut("execution(public * com.gdtc.deeplink.manager.*.*Service.*(..)))")
    public void BrokerAspect(){

    }

    @Before("BrokerAspect()")
    public void doBefore(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();

        if (READ_METHOD_PREFIX.stream().anyMatch(prefix -> methodName.startsWith(prefix))) {
            return;
        }

        String userName = ThreadUserInfo.getUserInfo().getUsername();
        String className = signature.getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();
        String auditLog = StringUtils.join(new String[]{userName, className, methodName, JSON.toJSONString(args)}, " | ");
        auditLogger.info(auditLog);
    }
}
