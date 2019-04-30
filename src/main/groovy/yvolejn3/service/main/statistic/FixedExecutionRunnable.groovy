package yvolejn3.service.main.statistic

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class FixedExecutionRunnable implements Runnable {
    private final AtomicInteger runCount = new AtomicInteger()
    private final Runnable delegate
    private volatile ScheduledFuture<?> self
    private final int maxRunCount

    FixedExecutionRunnable(Runnable delegate, int maxRunCount) {
        this.delegate = delegate
        this.maxRunCount = maxRunCount
    }

    @Override
    void run() {
        delegate.run()
        if (runCount.incrementAndGet() == maxRunCount) {
            boolean interrupted = false
            try {
                while (self == null) {
                    try {
                        Thread.sleep(1)
                    } catch (InterruptedException ignored) {
                        interrupted = true
                    }
                }
                self.cancel(false)
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt()
                }
            }
        }
    }

    void runNTimes(ScheduledExecutorService executor, long period, TimeUnit unit) {
        self = executor.scheduleAtFixedRate(this, 0, period, unit)
    }
}
