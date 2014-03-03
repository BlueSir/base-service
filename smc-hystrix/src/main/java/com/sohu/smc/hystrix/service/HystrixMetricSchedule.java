package com.sohu.smc.hystrix.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class HystrixMetricSchedule {
    private final static Logger logger;
    private final ScheduledExecutorService executor;
    private final int delay;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final MetricTask task;
    private ScheduledFuture<?> scheduledTask = null;
    private static HystrixMetricSchedule schedule;
    private static boolean isDebug = false;


    static {
        logger = LoggerFactory.getLogger(HystrixMetricSchedule.class);
//        if (!isDebug) {
//            schedule = new HystrixMetricSchedule();
//            schedule.start();
//        }
    }

    /**
     * 发送socket时间间隔
     */
    public HystrixMetricSchedule() {
        this(5, "10.13.80.154", 6803);
    }

    public HystrixMetricSchedule(int delay, String socketServerIp, int socketServerPort) {
        this.delay = delay;
        this.executor = new ScheduledThreadPoolExecutor(2, new MetricsPollerThreadFactory());

        this.task = new MetricTask(socketServerIp, socketServerPort);
    }


    /**
     * Start polling.
     */
    public static synchronized void start() {
        schedule = new HystrixMetricSchedule();
        // use compareAndSet to make sure it starts only once and when not running
        if (schedule.running.compareAndSet(false, true)) {
            schedule.scheduledTask = schedule.executor.scheduleWithFixedDelay(schedule.task, 0, schedule.delay, TimeUnit.SECONDS);
            System.out.println("Started Hystrix HystrixMetricSchedule..");
        }
    }

    /**
     * Pause (stop) polling. Polling can be started again with <code>start</code> as long as <code>shutdown</code> is not called.
     */
    public synchronized void pause() {
        // use compareAndSet to make sure it stops only once and when running
        if (running.compareAndSet(true, false)) {
            logger.info("Stopping the Servo Metrics Poller");
            scheduledTask.cancel(true);
        }
    }

    /**
     * Stops polling and shuts down the ExecutorService.
     * <p/>
     * This instance can no longer be used after calling shutdown.
     */
    public synchronized void shutdown() {
        pause();
        executor.shutdown();
    }

    public boolean isRunning() {
        return running.get();
    }

    /**
     * Used to protect against leaking ExecutorServices and threads if this class is abandoned for GC without shutting down.
     */
    @SuppressWarnings("unused")
    private final Object finalizerGuardian = new Object() {
        protected void finalize() throws Throwable {
            if (!executor.isShutdown()) {
                logger.info("{} was not shutdown. Caught in Finalize Guardian and shutting down.", HystrixMetricSchedule.class.getSimpleName());
                try {
                    shutdown();
                } catch (Exception e) {
                    System.err.println("Failed to shutdown " + HystrixMetricSchedule.class.getSimpleName());
                    e.printStackTrace();
                }
            }
        }
    };


    private class MetricsPollerThreadFactory implements ThreadFactory {
        private static final String MetricsThreadName = "HystrixMetricScheduler";

        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName(MetricsThreadName);
            return thread;
        }
    }
}
