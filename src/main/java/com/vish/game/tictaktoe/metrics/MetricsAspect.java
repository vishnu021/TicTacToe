package com.vish.game.tictaktoe.metrics;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import static com.vish.game.tictaktoe.metrics.MetricConstants.CONNECTED_USERS_COUNT;
import static com.vish.game.tictaktoe.metrics.MetricConstants.REGISTERED_USERS_COUNT;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class MetricsAspect {

    private final Metrics metrics;

    @Around("BroadcastServiceMethods() || GameServiceMethods()")
    public Object serviceLatencyAdvice(ProceedingJoinPoint joinPoint) {
        Object returnValue = null;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            returnValue = joinPoint.proceed();
            stopWatch.stop();
            String methodName = joinPoint.getSignature().getName();
            metrics.recordDistribution("ttt." + methodName, stopWatch.getLastTaskTimeMillis());
        } catch (Throwable e) {
            log.error("Error while updating service metrics", e);
        }
        return returnValue;
    }

    @AfterReturning("execution(* com.vish.game.tictaktoe.controller.GameWebSockerController.register(..))")
    public void registerCountAdvice() {
        metrics.increment(REGISTERED_USERS_COUNT);
    }

    @AfterReturning("execution(* com.vish.game.tictaktoe.handler.UserConnectionEventListener.handleWebSocketConnectListener(..))")
    public void connectedUserCountAdvice() {
        metrics.increment(CONNECTED_USERS_COUNT);
    }

    @Pointcut("execution(* com.vish.game.tictaktoe.service.BroadCastService.*(..))")
    public void BroadcastServiceMethods() {
    }

    @Pointcut("execution(* com.vish.game.tictaktoe.service.GameService.*(..))")
    public void GameServiceMethods() {
    }
}
