package com.sohu.smc.hystrix.model;

import com.netflix.hystrix.HystrixCommandProperties;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/10/13
 * Time: 10:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class HystrixConstants {
    public static final String default_commandGroup = "DefaultCommandGroup";
    public static final String default_commandKey = "DefaultCommandKey";
    public static final String default_threadPool = "DefaultThreadPool";
    public static final int default_threadPool_coreSize = 10;
    public static final int default_threadPool_keepAliveTimeMinutes = 1;
    public static final int default_threadPool_maxQueueSize = 100;
    public static final int default_threadPool_queueSizeRejectionThreshold = 50;
    public static final int default_threadPool_rollingNumberStatisticalWindow = 10000;
    public static final int default_errorThresholdPercentage = 85;

    public static final int default_threadPool_rollingNumberStatisticalWindowBuckets = 10;
    public static final int default_metricsRollingStatisticalWindow = 10000;
    public static final int default_metricsRollingStatisticalWindowBuckets = 10;
    public static final int default_circuitBreakerRequestVolumeThreshold = 20;
    public static final int default_circuitBreakerSleepWindowInMilliseconds = 5000;
    public static final boolean default_circuitBreakerForceOpen = false;
    public static final boolean default_circuitBreakerForceClosed = false;
    public static final int default_executionIsolationThreadTimeoutInMilliseconds = 2000;
    public static final boolean default_executionIsolationThreadInterruptOnTimeout = true;
    public static final boolean default_metricsRollingPercentileEnabled = true;
    public static final boolean default_requestCacheEnabled = true;
    public static final int default_fallbackIsolationSemaphoreMaxConcurrentRequests = 10;
    public static final boolean default_fallbackEnabled = true;
    public static final int default_executionIsolationSemaphoreMaxConcurrentRequests = 10;
    public static final boolean default_requestLogEnabled = true;
    public static final boolean default_circuitBreakerEnabled = true;
    public static final int default_metricsRollingPercentileWindow = 60000;
    public static final int default_metricsRollingPercentileWindowBuckets = 6;
    public static final int default_metricsRollingPercentileBucketSize = 100;
    public static final int default_metricsHealthSnapshotIntervalInMilliseconds = 500;

}
