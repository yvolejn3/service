package yvolejn3.service.main.cache

import com.binance.api.client.domain.market.TickerStatistics
import com.google.inject.Singleton
import groovy.util.logging.Log4j2
import yvolejn3.service.main.cache.loader.TickerLoader
import yvolejn3.service.main.statistic.Listener

@Log4j2
@Singleton
class TickerCache extends AbstractCache<TickerLoader> {

    TickerStatistics get(String symbol) {
        return getCache(symbol).ticker
    }

}
