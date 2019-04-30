package yvolejn3.service.main.cache

import com.binance.api.client.domain.market.AggTrade
import com.google.inject.Singleton
import groovy.util.logging.Log4j2
import yvolejn3.service.main.cache.loader.AggTradesLoader

@Log4j2
@Singleton
class AggTradesCache extends AbstractCache<AggTradesLoader> {

    Map<Long, AggTrade> get(String symbol) {
        return getCache(symbol).aggTradesCache
    }


}