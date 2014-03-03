package com.sohu.smc.hystrix.util;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-9-2
 * Time: 上午9:39
 * To change this template use File | Settings | File Templates.
 */
public enum Key {
    type("type"), name("name"), group("commandGroup"), currentTime("currentTime"),
    isCircuitBreakerOpen("isCircuitBreakerOpen"), errorPercentage("errorPercentage"), errorCount("errorCount"),
    requestCount("requestCount"), rollingCountCollapsedRequests("rollingCountCollapsedRequests"),
    rollingCountExceptionsThrown("rollingCountExceptionsThrown"), rollingCountFailure("rollingCountFailure"),
    rollingCountFallbackFailure("rollingCountFallbackFailure"), rollingCountFallbackRejection("rollingCountFallbackRejection"),
    rollingCountFallbackSuccess("rollingCountFallbackSuccess"), rollingCountResponsesFromCache("rollingCountResponsesFromCache"),
    rollingCountSemaphoreRejected("rollingCountSemaphoreRejected"), rollingCountTimeout("rollingCountTimeout"),
    rollingCountShortCircuited("rollingCountShortCircuited"), rollingCountSuccess("rollingCountSuccess"),
    rollingCountThreadPoolRejected("rollingCountThreadPoolRejected"),
    currentConcurrentExecutionCount("currentConcurrentExecutionCount"), latencyExecute_mean("latencyExecute_mean"),
    latencyExecute("latencyExecute"), latencyTotal_mean("latencyTotal_mean"), latencyTotal("latencyTotal"),
    propertyValue_circuitBreakerRequestVolumeThreshold("propertyValue_circuitBreakerRequestVolumeThreshold"),
    propertyValue_circuitBreakerSleepWindowInMilliseconds("propertyValue_circuitBreakerSleepWindowInMilliseconds"),
    propertyValue_circuitBreakerErrorThresholdPercentage("propertyValue_circuitBreakerErrorThresholdPercentage"),
    propertyValue_circuitBreakerForceOpen("propertyValue_circuitBreakerForceOpen"),
    propertyValue_circuitBreakerForceClosed("propertyValue_circuitBreakerForceClosed"),
    propertyValue_circuitBreakerEnabled("propertyValue_circuitBreakerEnabled"),
    propertyValue_executionIsolationStrategy("propertyValue_executionIsolationStrategy"),
    propertyValue_executionIsolationThreadTimeoutInMilliseconds("propertyValue_executionIsolationThreadTimeoutInMilliseconds"),
    propertyValue_executionIsolationThreadInterruptOnTimeout("propertyValue_executionIsolationThreadInterruptOnTimeout"),
    propertyValue_executionIsolationThreadPoolKeyOverride("propertyValue_executionIsolationThreadPoolKeyOverride"),
    propertyValue_executionIsolationSemaphoreMaxConcurrentRequests("propertyValue_executionIsolationSemaphoreMaxConcurrentRequests"),
    propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests("propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests"),
    propertyValue_metricsRollingStatisticalWindowInMilliseconds("propertyValue_metricsRollingStatisticalWindowInMilliseconds"),
    propertyValue_requestCacheEnabled("propertyValue_requestCacheEnabled"), propertyValue_requestLogEnabled("propertyValue_requestLogEnabled"),
    currentMaximumPoolSize("currentMaximumPoolSize"), currentActiveCount("currentActiveCount"),
    currentCompletedTaskCount("currentCompletedTaskCount"), currentCorePoolSize("currentCorePoolSize"),
    currentLargestPoolSize("currentLargestPoolSize"), currentPoolSize("currentPoolSize"), currentQueueSize("currentQueueSize"),
    currentTaskCount("currentTaskCount"), rollingCountThreadsExecuted("rollingCountThreadsExecuted"),
    rollingMaxActiveThreads("rollingMaxActiveThreads"),
    reportingHosts("reportingHosts"), HystrixCommand("HystrixCommand"),
    HystrixThreadPool("HystrixThreadPool"), propertyValue_queueSizeRejectionThreshold("propertyValue_queueSizeRejectionThreshold"),
    ip("ip");

    public String value;

    Key(String type) {
        this.value = type;
    }
}