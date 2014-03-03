package com.sohu.smc.hystrix.service;

import com.netflix.hystrix.*;
import com.sohu.smc.hystrix.annotation.HystrixHandler;

/**
 * Created with IntelliJ IDEA.
 * User: xiaocaijing
 * Date: 13-9-12
 * Time: 下午5:20
 */
public abstract class HystrixMethodCommand<T> extends HystrixCommand<T>  {
        static{
            HystrixMetricSchedule.start();
        }
        public HystrixMethodCommand(HystrixHandler handle) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(handle.commandGroup()))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(handle.commandKey()))
                    .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(handle.threadPool()))
                    .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                            .withCoreSize(handle.threadPool_coreSize())
                            .withMaxQueueSize(handle.threadPool_maxQueueSize())
                            .withKeepAliveTimeMinutes(handle.threadPool_keepAliveTimeMinutes())
                            .withQueueSizeRejectionThreshold(handle.threadPool_queueSizeRejectionThreshold())
                            .withMetricsRollingStatisticalWindowBuckets(handle.threadPool_rollingNumberStatisticalWindowBuckets())
                            .withMetricsRollingStatisticalWindowInMilliseconds(handle.threadPool_rollingNumberStatisticalWindow()))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                            .withCircuitBreakerErrorThresholdPercentage(handle.errorThresholdPercentage())
                            .withCircuitBreakerEnabled(handle.circuitBreakerEnabled())
                            .withCircuitBreakerForceClosed(handle.circuitBreakerForceClosed())
                            .withCircuitBreakerForceOpen(handle.circuitBreakerForceOpen())
                            .withCircuitBreakerRequestVolumeThreshold(handle.circuitBreakerRequestVolumeThreshold())
                            .withCircuitBreakerSleepWindowInMilliseconds(handle.circuitBreakerSleepWindowInMilliseconds())
                            .withExecutionIsolationThreadTimeoutInMilliseconds(handle.executionIsolationThreadTimeoutInMilliseconds())
                            .withExecutionIsolationThreadInterruptOnTimeout(handle.executionIsolationThreadInterruptOnTimeout())
                            .withExecutionIsolationSemaphoreMaxConcurrentRequests(handle.executionIsolationSemaphoreMaxConcurrentRequests())
                            .withExecutionIsolationStrategy(handle.executionIsolationStrategy())
                            .withFallbackEnabled(handle.fallbackEnabled())
                            .withFallbackIsolationSemaphoreMaxConcurrentRequests(handle.fallbackIsolationSemaphoreMaxConcurrentRequests())
                            .withMetricsHealthSnapshotIntervalInMilliseconds(handle.metricsHealthSnapshotIntervalInMilliseconds())
                            .withMetricsRollingPercentileBucketSize(handle.metricsRollingPercentileBucketSize())
                            .withMetricsRollingPercentileEnabled(handle.metricsRollingPercentileEnabled())
                            .withMetricsRollingPercentileWindowBuckets(handle.metricsRollingPercentileWindowBuckets())
                            .withMetricsRollingPercentileWindowInMilliseconds(handle.metricsRollingPercentileWindow())
                            .withMetricsRollingStatisticalWindowBuckets(handle.metricsRollingStatisticalWindowBuckets())
                            .withMetricsRollingStatisticalWindowInMilliseconds(handle.metricsRollingStatisticalWindow())
                            .withRequestCacheEnabled(handle.requestCacheEnabled())
                            .withRequestLogEnabled(handle.requestLogEnabled())
                    ));
        }

}
