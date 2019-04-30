package yvolejn3.service.main.cache.loader

import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.BinanceApiWebSocketClient
import com.binance.api.client.domain.market.AggTrade
import groovy.util.logging.Log4j2
import yvolejn3.service.main.cache.AbstractCache
import yvolejn3.service.main.statistic.Listener

@Log4j2
class AggTradesLoader extends StreamLoader {

    /**
     * Key is the aggregate trade id, and the value contains the aggregated trade data, which is
     * automatically updated whenever a new agg data stream event arrives.
     */
    Map<Long, AggTrade> aggTradesCache

    AggTradesLoader(String symbol, AbstractCache cache) {
        super(symbol, cache)
    }

    /**
     * Initializes the aggTrades cache by using the REST API.
     */

    void initialize() {
        List<AggTrade> aggTrades = restClient.getAggTrades(symbol.toUpperCase())
        this.aggTradesCache = new HashMap<>()
        for (AggTrade aggTrade : aggTrades) {
            aggTradesCache.put(aggTrade.getAggregatedTradeId(), aggTrade)
        }
    }


    /**
     * Begins streaming of agg trades events.
     */
    Closeable startEventStreaming() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance()
        BinanceApiWebSocketClient client = factory.newWebSocketClient()

        return client.onAggTradeEvent(symbol.toLowerCase(), { response ->
            Long aggregatedTradeId = response.getAggregatedTradeId()
            AggTrade updateAggTrade = aggTradesCache.get(aggregatedTradeId)
            if (updateAggTrade == null) {
                // new agg trade
                updateAggTrade = new AggTrade()
            }
            updateAggTrade.aggregatedTradeId = aggregatedTradeId
            updateAggTrade.price = response.price
            updateAggTrade.quantity = response.quantity
            updateAggTrade.firstBreakdownTradeId = response.firstBreakdownTradeId
            updateAggTrade.lastBreakdownTradeId = response.lastBreakdownTradeId
            updateAggTrade.buyerMaker = response.isBuyerMaker()

            // Store the updated agg trade in the cache
            aggTradesCache.put(aggregatedTradeId, updateAggTrade)
            log.info("${this.class.simpleName}: Enevt ${symbol} ${updateAggTrade}")
        })
    }

}