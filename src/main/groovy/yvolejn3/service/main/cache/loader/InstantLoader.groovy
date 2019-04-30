package yvolejn3.service.main.cache.loader

import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.BinanceApiRestClient
import groovy.transform.Canonical
import groovy.util.logging.Log4j2
import yvolejn3.service.main.cache.AbstractCache
import yvolejn3.service.main.statistic.Listener

@Log4j2
abstract class InstantLoader {
    String symbol
    AbstractCache cache

    protected static BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance()
    protected static BinanceApiRestClient restClient = factory.newRestClient()

    InstantLoader(String symbol, AbstractCache cache) {
        this.symbol = symbol.toUpperCase()
        this.cache = cache
        init()
    }

    protected void init() {
        initialize()
        log.info("${this.class.simpleName}: initialized ${symbol}")
    }

    abstract void initialize()
}
