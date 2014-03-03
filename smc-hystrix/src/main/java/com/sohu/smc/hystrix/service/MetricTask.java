package com.sohu.smc.hystrix.service;

import com.netflix.hystrix.*;
import com.netflix.hystrix.util.HystrixRollingNumberEvent;
import com.sohu.smc.hystrix.util.IpUtil;
import com.sohu.smc.hystrix.util.Key;
import com.sohu.smc.hystrix.util.UDPClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import java.io.StringWriter;

import static com.sohu.smc.hystrix.util.Key.*;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-8-28
 * Time: 下午3:57
 * To change this template use File | Settings | File Templates.
 */
public class MetricTask implements Runnable {
    private final static JsonFactory jsonFactory = new JsonFactory();
    private final String socketIp;
    private final int socketPort;
    private final String app_ip = IpUtil.getIp();

    public MetricTask(String ip, int port) {
        this.socketIp = ip;
        this.socketPort = port;
    }

    @Override
    public void run() {

        try {
            // command metrics
            for (HystrixCommandMetrics commandMetrics : HystrixCommandMetrics.getInstances()) {
                HystrixCommandKey key = commandMetrics.getCommandKey();
                HystrixCircuitBreaker circuitBreaker = HystrixCircuitBreaker.Factory.getInstance(key);
                StringWriter jsonString = new StringWriter();
                JsonGenerator json = jsonFactory.createJsonGenerator(jsonString);

                // Informational and Status
                json.writeStartObject();
                json.writeStringField(type.value, HystrixCommand.value);
                json.writeStringField(name.value, key.name());
                json.writeStringField(group.value, commandMetrics.getCommandGroup().name());
                json.writeNumberField(currentTime.value, (int) (System.currentTimeMillis() / 1000));

                // circuit breaker
                json.writeBooleanField(isCircuitBreakerOpen.value, circuitBreaker.isOpen());
                HystrixCommandMetrics.HealthCounts healthCounts = commandMetrics.getHealthCounts();
                json.writeNumberField(errorPercentage.value, healthCounts.getErrorPercentage());
                json.writeNumberField(errorCount.value, healthCounts.getErrorCount());
                json.writeNumberField(requestCount.value, healthCounts.getTotalRequests());

                // rolling counters  Gauge
                json.writeNumberField(rollingCountCollapsedRequests.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.COLLAPSED));
                json.writeNumberField(rollingCountExceptionsThrown.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.EXCEPTION_THROWN));
                json.writeNumberField(rollingCountFailure.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.FAILURE));
                json.writeNumberField(rollingCountFallbackFailure.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.FALLBACK_FAILURE));
                json.writeNumberField(rollingCountFallbackRejection.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.FALLBACK_REJECTION));
                json.writeNumberField(rollingCountFallbackSuccess.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.FALLBACK_SUCCESS));
                json.writeNumberField(rollingCountResponsesFromCache.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.RESPONSE_FROM_CACHE));
                json.writeNumberField(rollingCountSemaphoreRejected.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.SEMAPHORE_REJECTED));
                json.writeNumberField(rollingCountShortCircuited.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.SHORT_CIRCUITED));
                json.writeNumberField(rollingCountSuccess.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.SUCCESS));
                json.writeNumberField(rollingCountThreadPoolRejected.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.THREAD_POOL_REJECTED));
                json.writeNumberField(rollingCountTimeout.value, commandMetrics.getRollingCount(HystrixRollingNumberEvent.TIMEOUT));

                json.writeNumberField(currentConcurrentExecutionCount.value, commandMetrics.getCurrentConcurrentExecutionCount());

                // latency percentiles
                json.writeNumberField(latencyExecute_mean.value, commandMetrics.getExecutionTimeMean());
                json.writeObjectFieldStart(latencyExecute.value);
                json.writeNumberField("0", commandMetrics.getExecutionTimePercentile(0));
                json.writeNumberField("25", commandMetrics.getExecutionTimePercentile(25));
                json.writeNumberField("50", commandMetrics.getExecutionTimePercentile(50));
                json.writeNumberField("75", commandMetrics.getExecutionTimePercentile(75));
                json.writeNumberField("90", commandMetrics.getExecutionTimePercentile(90));
                json.writeNumberField("95", commandMetrics.getExecutionTimePercentile(95));
                json.writeNumberField("99", commandMetrics.getExecutionTimePercentile(99));
                json.writeNumberField("99.5", commandMetrics.getExecutionTimePercentile(99.5));
                json.writeNumberField("100", commandMetrics.getExecutionTimePercentile(100));
                json.writeEndObject();
                //
                json.writeNumberField(latencyTotal_mean.value, commandMetrics.getTotalTimeMean());
                json.writeObjectFieldStart(latencyTotal.value);
                json.writeNumberField("0", commandMetrics.getTotalTimePercentile(0));
                json.writeNumberField("25", commandMetrics.getTotalTimePercentile(25));
                json.writeNumberField("50", commandMetrics.getTotalTimePercentile(50));
                json.writeNumberField("75", commandMetrics.getTotalTimePercentile(75));
                json.writeNumberField("90", commandMetrics.getTotalTimePercentile(90));
                json.writeNumberField("95", commandMetrics.getTotalTimePercentile(95));
                json.writeNumberField("99", commandMetrics.getTotalTimePercentile(99));
                json.writeNumberField("99.5", commandMetrics.getTotalTimePercentile(99.5));
                json.writeNumberField("100", commandMetrics.getTotalTimePercentile(100));
                json.writeEndObject();

                // property values for reporting what is actually seen by the command rather than what was set somewhere
                HystrixCommandProperties commandProperties = commandMetrics.getProperties();

                json.writeNumberField(propertyValue_circuitBreakerRequestVolumeThreshold.value, commandProperties.circuitBreakerRequestVolumeThreshold().get());
                json.writeNumberField(propertyValue_circuitBreakerSleepWindowInMilliseconds.value, commandProperties.circuitBreakerSleepWindowInMilliseconds().get());
                json.writeNumberField(propertyValue_circuitBreakerErrorThresholdPercentage.value, commandProperties.circuitBreakerErrorThresholdPercentage().get());
                json.writeBooleanField(propertyValue_circuitBreakerForceOpen.value, commandProperties.circuitBreakerForceOpen().get());
                json.writeBooleanField(propertyValue_circuitBreakerForceClosed.value, commandProperties.circuitBreakerForceClosed().get());
                json.writeBooleanField(propertyValue_circuitBreakerEnabled.value, commandProperties.circuitBreakerEnabled().get());

                json.writeStringField(propertyValue_executionIsolationStrategy.value, commandProperties.executionIsolationStrategy().get().name());
                json.writeNumberField(propertyValue_executionIsolationThreadTimeoutInMilliseconds.value, commandProperties.executionIsolationThreadTimeoutInMilliseconds().get());
                json.writeBooleanField(propertyValue_executionIsolationThreadInterruptOnTimeout.value, commandProperties.executionIsolationThreadInterruptOnTimeout().get());
                json.writeStringField(propertyValue_executionIsolationThreadPoolKeyOverride.value, commandProperties.executionIsolationThreadPoolKeyOverride().get());
                json.writeNumberField(propertyValue_executionIsolationSemaphoreMaxConcurrentRequests.value, commandProperties.executionIsolationSemaphoreMaxConcurrentRequests().get());
                json.writeNumberField(propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests.value, commandProperties.fallbackIsolationSemaphoreMaxConcurrentRequests().get());

                /*
                * The following are commented out as these rarely change and are verbose for streaming for something people don't change.
                * We could perhaps allow a property or request argument to include these.
                */

                //                    json.put("propertyValue_metricsRollingPercentileEnabled", commandProperties.metricsRollingPercentileEnabled().get());
                //                    json.put("propertyValue_metricsRollingPercentileBucketSize", commandProperties.metricsRollingPercentileBucketSize().get());
                //                    json.put("propertyValue_metricsRollingPercentileWindow", commandProperties.metricsRollingPercentileWindowInMilliseconds().get());
                //                    json.put("propertyValue_metricsRollingPercentileWindowBuckets", commandProperties.metricsRollingPercentileWindowBuckets().get());
                //                    json.put("propertyValue_metricsRollingStatisticalWindowBuckets", commandProperties.metricsRollingStatisticalWindowBuckets().get());
                json.writeNumberField(propertyValue_metricsRollingStatisticalWindowInMilliseconds.value, commandProperties.metricsRollingStatisticalWindowInMilliseconds().get());
                json.writeBooleanField(propertyValue_requestCacheEnabled.value, commandProperties.requestCacheEnabled().get());
                json.writeBooleanField(propertyValue_requestLogEnabled.value, commandProperties.requestLogEnabled().get());
                json.writeNumberField(reportingHosts.value, 1);

                json.writeStringField(Key.ip.value, app_ip);

                json.writeEndObject();
                json.close();
//                System.out.println(ip + ":" + port + "||" + jsonString.getBuffer().toString());
                UDPClient.send(socketIp, socketPort, jsonString.getBuffer().toString().getBytes(), new byte[]{});
            }

            // thread pool metrics
            for (HystrixThreadPoolMetrics threadPoolMetrics : HystrixThreadPoolMetrics.getInstances()) {
                HystrixThreadPoolKey key = threadPoolMetrics.getThreadPoolKey();

                StringWriter jsonString = new StringWriter();
                JsonGenerator json = jsonFactory.createJsonGenerator(jsonString);
                json.writeStartObject();

                json.writeStringField(type.value, HystrixThreadPool.value);
                json.writeStringField(name.value, key.name());
                json.writeNumberField(currentTime.value, System.currentTimeMillis());

                //101.3 80 154

                json.writeNumberField(currentActiveCount.value, threadPoolMetrics.getCurrentActiveCount().intValue());
                json.writeNumberField(currentCompletedTaskCount.value, threadPoolMetrics.getCurrentCompletedTaskCount().longValue());
                json.writeNumberField(currentCorePoolSize.value, threadPoolMetrics.getCurrentCorePoolSize().intValue());
                json.writeNumberField(currentLargestPoolSize.value, threadPoolMetrics.getCurrentLargestPoolSize().intValue());
                json.writeNumberField(currentMaximumPoolSize.value, threadPoolMetrics.getCurrentMaximumPoolSize().intValue());
                json.writeNumberField(currentPoolSize.value, threadPoolMetrics.getCurrentPoolSize().intValue());
                json.writeNumberField(currentQueueSize.value, threadPoolMetrics.getCurrentQueueSize().intValue());
                json.writeNumberField(currentTaskCount.value, threadPoolMetrics.getCurrentTaskCount().longValue());
                json.writeNumberField(rollingCountThreadsExecuted.value, threadPoolMetrics.getRollingCountThreadsExecuted());
                json.writeNumberField(rollingMaxActiveThreads.value, threadPoolMetrics.getRollingMaxActiveThreads());

                json.writeNumberField(propertyValue_queueSizeRejectionThreshold.value, threadPoolMetrics.getProperties().queueSizeRejectionThreshold().get());
                json.writeNumberField(propertyValue_metricsRollingStatisticalWindowInMilliseconds.value, threadPoolMetrics.getProperties().metricsRollingStatisticalWindowInMilliseconds().get());

                json.writeNumberField(reportingHosts.value, 1); // this will get summed across all instances in a cluster
                json.writeStringField(Key.ip.value, app_ip);
                json.writeEndObject();
                json.close();

                String str = jsonString.getBuffer().toString();
                byte[] ret = str.getBytes();
                UDPClient.send(socketIp, socketPort, ret, new byte[]{});
            }
        } catch (Exception e) {
            System.err.println("Failed to output metrics as JSON");
            e.printStackTrace();
        }
    }
}
