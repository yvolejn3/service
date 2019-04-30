package yvolejn3.service.main.cache.loader

import com.binance.api.client.domain.event.AllMarketTickersEvent
import com.binance.api.client.domain.market.TickerStatistics
import groovy.util.logging.Log4j2
import yvolejn3.service.main.cache.AbstractCache
import yvolejn3.service.main.statistic.Listener

@Log4j2
class TickerLoader extends StreamLoader {
    private long eventTime

    TickerStatistics ticker

    TickerLoader(String symbol, AbstractCache cache) {
        super(symbol, cache)
    }

    void initialize() {
        ticker = restClient.get24HrPriceStatistics(symbol.toUpperCase())
    }

    Closeable startEventStreaming() {
        return webSocketClient.onAllMarketTickersEvent({
            it.stream()
                    .filter({ it.symbol == symbol && it.eventTime > eventTime })
                    .forEach({ ticker = it })
            // оповестить слушателей
            cache.notifyListeners(symbol, new BigDecimal(ticker.lastPrice))
        })
    }

    private void setTicker(AllMarketTickersEvent it) {
        eventTime = it.eventTime
        ticker.symbol = it.symbol
        ticker.priceChange = it.priceChange
        ticker.priceChangePercent = it.priceChangePercent
        ticker.weightedAvgPrice = it.weightedAveragePrice
        ticker.prevClosePrice = it.previousDaysClosePrice
        ticker.lastPrice = it.currentDaysClosePrice
        ticker.bidPrice = it.bestBidPrice
        ticker.askPrice = it.bestAskPrice
        ticker.openPrice = it.openPrice
        ticker.highPrice = it.highPrice
        ticker.lowPrice = it.lowPrice
        ticker.volume = it.totalTradedBaseAssetVolume
        ticker.openTime = it.statisticesOpenTime
        ticker.closeTime = it.statisticesCloseTime
        ticker.firstId = it.firstTradeId
        ticker.lastId = it.lastTradeId
        ticker.count = it.totalNumberOfTrades
    }
}
