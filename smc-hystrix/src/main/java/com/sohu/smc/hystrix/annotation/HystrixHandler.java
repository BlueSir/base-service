package com.sohu.smc.hystrix.annotation;

import com.netflix.hystrix.HystrixCommandProperties;
import com.sohu.smc.hystrix.model.HystrixConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HystrixHandler {
    //监控的key，用来在Hystrix的监控大盘中唯一标识一个监控功能
    String commandKey() default HystrixConstants.default_commandKey;

    //监控群组，它用来组织多个commandKey
    String commandGroup() default HystrixConstants.default_commandGroup;

    //监控的线程池的名字，每定义一个线程池名字都将为其分配一个新的线程池。
    String threadPool() default HystrixConstants.default_threadPool;

    // 线程池的大小
    int threadPool_coreSize() default HystrixConstants.default_threadPool_coreSize;

    // 每个线程存活的时长，单位为分钟。
    int threadPool_keepAliveTimeMinutes() default HystrixConstants.default_threadPool_keepAliveTimeMinutes;

    // 线程队列的最大值。(这个值不能动态变化，所以我们通过改变queueSizeRejectionThreshold 的值来人工的限制和放弃请求。
    int threadPool_maxQueueSize() default HystrixConstants.default_threadPool_maxQueueSize;

    // 线程队列中最大可留存的记录的数量
    int threadPool_queueSizeRejectionThreshold() default HystrixConstants.default_threadPool_queueSizeRejectionThreshold;

    // 统计窗口滚动的时长，单位是毫秒。默认是10000毫秒，即每10秒为一个统计时长。
    int threadPool_rollingNumberStatisticalWindow() default HystrixConstants.default_threadPool_rollingNumberStatisticalWindow;

    // number of buckets in rolling number (10 1-second buckets)
    int threadPool_rollingNumberStatisticalWindowBuckets() default HystrixConstants.default_threadPool_rollingNumberStatisticalWindowBuckets;

    // default => statisticalWindow: 10000 = 10 seconds (and default of 10 buckets so each bucket is 1 second)
    int metricsRollingStatisticalWindow() default HystrixConstants.default_metricsRollingStatisticalWindow;

    // default => statisticalWindowBuckets: 10 = 10 buckets in a 10 second window so each bucket is 1 second
    int metricsRollingStatisticalWindowBuckets() default HystrixConstants.default_metricsRollingStatisticalWindowBuckets;

    // default => statisticalWindowVolumeThreshold: 20 requests in 10 seconds must occur before statistics matter
    int circuitBreakerRequestVolumeThreshold() default HystrixConstants.default_circuitBreakerRequestVolumeThreshold;

    // default => sleepWindow: 5000 = 5 seconds that we will sleep before trying again after tripping the circuit
    int circuitBreakerSleepWindowInMilliseconds() default HystrixConstants.default_circuitBreakerSleepWindowInMilliseconds;

    // 降级开启阈值百分比，即错误率达到百分之多少的时候将降级。默认是％85
    int errorThresholdPercentage() default HystrixConstants.default_errorThresholdPercentage;

    // default => forceCircuitOpen = false (we want to allow traffic)
    boolean circuitBreakerForceOpen() default HystrixConstants.default_circuitBreakerForceOpen;

    // default => ignoreErrors = false
    boolean circuitBreakerForceClosed() default HystrixConstants.default_circuitBreakerForceClosed;

    // default => executionTimeoutInMilliseconds: 2000 = 2 second
    int executionIsolationThreadTimeoutInMilliseconds() default HystrixConstants.default_executionIsolationThreadTimeoutInMilliseconds;

    HystrixCommandProperties.ExecutionIsolationStrategy executionIsolationStrategy() default HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;;

    boolean executionIsolationThreadInterruptOnTimeout() default HystrixConstants.default_executionIsolationThreadInterruptOnTimeout;

    boolean metricsRollingPercentileEnabled() default HystrixConstants.default_metricsRollingPercentileEnabled;

    boolean requestCacheEnabled() default HystrixConstants.default_requestCacheEnabled;

    int fallbackIsolationSemaphoreMaxConcurrentRequests() default HystrixConstants.default_fallbackIsolationSemaphoreMaxConcurrentRequests;

    boolean fallbackEnabled() default HystrixConstants.default_fallbackEnabled;

    int executionIsolationSemaphoreMaxConcurrentRequests() default HystrixConstants.default_executionIsolationSemaphoreMaxConcurrentRequests;

    boolean requestLogEnabled() default HystrixConstants.default_requestLogEnabled;

    boolean circuitBreakerEnabled() default HystrixConstants.default_circuitBreakerEnabled;

    // default to 1 minute for RollingPercentile
    int metricsRollingPercentileWindow() default HystrixConstants.default_metricsRollingPercentileWindow;

    // default to 6 buckets (10 seconds each in 60 second window)
    int metricsRollingPercentileWindowBuckets() default HystrixConstants.default_metricsRollingPercentileWindowBuckets;

    // default to 100 values max per bucket
    int metricsRollingPercentileBucketSize() default HystrixConstants.default_metricsRollingPercentileBucketSize;

    // default to 500ms as max frequency between allowing snapshots of health (error percentage etc)
    int metricsHealthSnapshotIntervalInMilliseconds() default HystrixConstants.default_metricsHealthSnapshotIntervalInMilliseconds;

}
