package yvolejn3.service.main.statistic

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class Utils {


    static void emulateRequest(int count, long period, TimeUnit unit, Closure closure) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor()
        new FixedExecutionRunnable(closure, count).runNTimes(service, period, unit)
    }


    static int getRandomNumberInRange(int min, int max) {
        return new Random().ints(min, (max + 1)).limit(1).findFirst().getAsInt()

    }

    static String millsToString(long mills) {
        return Duration.ofMillis(mills).toString()
                .substring(2)
                .replaceAll('(\\d[HMS])(?!$)', '$1 ')
                .toLowerCase()
    }
}
