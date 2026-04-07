package com.cafe.cafeapp.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.cafe.cafeapp.service.*.*(..))")
    public Object logAndMeasure(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();

        long start = System.currentTimeMillis();

        log.debug("Начало выполнения метода {}", methodName);

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;

            log.info("Метод {} выполнен за {} ms", methodName, duration);

            return result;

        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;

            log.error("Ошибка в методе {} ({} ms): {}",
                    methodName,
                    duration,
                    ex.getMessage(),
                    ex
            );

            throw ex;
        }
    }
}
