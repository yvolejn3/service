package yvolejn3.service.main.cache.loader

import com.binance.api.client.domain.market.OrderBookEntry
import groovy.util.logging.Log4j2
import yvolejn3.service.main.cache.AbstractCache
import yvolejn3.service.main.statistic.Listener

@Log4j2
class DepthLoader extends StreamLoader {
    private long lastUpdateId

    NavigableMap<BigDecimal, BigDecimal> bids
    NavigableMap<BigDecimal, BigDecimal> asks

    DepthLoader(String symbol, AbstractCache cache) {
        super(symbol, cache)
    }

    /**
     * Initializes the depth cache by using the REST API.
     */
    void initialize() {
        def orderBook = restClient.getOrderBook(symbol, 100)
        this.lastUpdateId = orderBook.getLastUpdateId()

        asks = new TreeMap<>()
        orderBook.getAsks()
                .forEach({ asks.put(new BigDecimal(it.price), new BigDecimal(it.qty)) })

        bids = new TreeMap<>(Comparator.reverseOrder() as Comparator<? super BigDecimal>)
        orderBook.getBids()
                .forEach({ bids.put(new BigDecimal(it.price), new BigDecimal(it.qty)) })

    }

    /**
     * Begins streaming of depth events.
     */
    Closeable startEventStreaming() {
        return webSocketClient.onDepthEvent(symbol, { response ->
            if (response.getFinalUpdateId() > lastUpdateId) {
                lastUpdateId = response.getFinalUpdateId()
                updateOrderBook(asks, response.getAsks())
                updateOrderBook(bids, response.getBids())
                // оповестить слушателей
                cache.notifyListeners(symbol, bids.firstKey())
            }
        })
    }

    /**
     * Updates an order book (bids or asks) with a delta received from the server.
     *
     * Whenever the qty specified is ZERO, it means the price should was removed from the order book.
     */
    private static void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries, List<OrderBookEntry> orderBookDeltas) {
        for (OrderBookEntry orderBookDelta : orderBookDeltas) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice())
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty())
            if (qty == BigDecimal.ZERO) {
                lastOrderBookEntries.remove(price)
            } else {
                lastOrderBookEntries.put(price, qty)
            }
        }
    }
}
