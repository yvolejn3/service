package yvolejn3.service.main.cache

import yvolejn3.service.main.statistic.Listener

import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class FakePriceCache {
    long init = Instant.now().epochSecond
    def rnd = ["ETHBTC"]

    def listeners = new HashMap<String, ArrayList<Listener>>()

    FakePriceCache() {
        /*ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor()
        service.scheduleAtFixedRate({
            send(rnd.get(new Random().ints(0, rnd.size()).limit(1).findFirst().getAsInt()))
        }, 0, 1, TimeUnit.SECONDS)*/
    }

    void send(String symbol) {
        //listeners.get(symbol)?.forEach({ it.doEvent(Instant.now().epochSecond - init) })
    }

    void add(String symbol, Listener listener) {
        def listenerList = listeners.computeIfAbsent(symbol, {new ArrayList<Listener>()})
        listenerList.add(listener)
    }


}
