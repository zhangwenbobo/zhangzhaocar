package com.zhangzhao.web.aop;

import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.zhangzhao.web.controller.*.*(..))")
    public void log(){}

    @Around("log()")
    public Object aroundManagerLogPoint(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("拦截到了" + pjp.getSignature().getName() +"方法...");
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method method = methodSignature.getMethod();
        System.out.println("classname:" + method.isAnnotationPresent(ApiOperation.class));
        System.out.println(method.getAnnotation(ApiOperation.class).value());
        return pjp.proceed();
    }
}
