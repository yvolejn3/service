package yvolejn3.service.main.cache

import com.google.inject.Singleton
import groovy.util.logging.Log4j2
import yvolejn3.service.main.cache.loader.DepthLoader

@Log4j2
@Singleton
class DepthCache extends AbstractCache<DepthLoader> {

    NavigableMap<BigDecimal, BigDecimal> getAsks(String symbol) {
        return getCache(symbol).asks
    }

    NavigableMap<BigDecimal, BigDecimal> getBids(String symbol) {
        return getCache(symbol).bids
    }

}
