package yvolejn3.service.main.cache.loader

import com.binance.api.client.BinanceApiWebSocketClient
import groovy.util.logging.Log4j2
import org.apache.http.client.HttpResponseException
import yvolejn3.service.main.cache.AbstractCache
import yvolejn3.service.main.statistic.Listener
import yvolejn3.service.main.statistic.StreamListener

@Log4j2
abstract class StreamLoader extends InstantLoader {

    private Closeable stream
    protected static BinanceApiWebSocketClient webSocketClient = factory.newWebSocketClient()

    StreamLoader(String symbol, AbstractCache cache) {
        super(symbol, cache)
        this.symbol = symbol.toLowerCase()
        startStream()
    }

    private void startStream() {
        stream = startEventStreaming()
        log.info("${this.class.simpleName}@${hashCode()}: startEventStream ${symbol}")
    }

    void close() {
        stream.close()
        log.info("${this.class.simpleName}@${hashCode()}: stream closed")
    }

    abstract Closeable startEventStreaming()

}