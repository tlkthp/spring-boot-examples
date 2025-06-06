package com.example.querylogs;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class QueryLogAspect {
    private static final Logger log = LoggerFactory.getLogger(QueryLogAspect.class);

    // lowest methods to target for AOP logging for JdbcTemplate and NamedParameterJdbcTemplate
    @Around("execution(* org.springframework.jdbc.core.JdbcTemplate.*(String, ..))" +
            "|| execution(* org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate.*(String, ..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            start = System.currentTimeMillis();
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;

            String sql = "";
            try {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0 && args[0] instanceof String) {
                    sql = (String) args[0];
                    if (sql != null && !sql.isBlank()) {
                        log.info("Query = {}, Time = {} milli sec", sql, duration);
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
