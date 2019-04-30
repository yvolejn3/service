package yvolejn3.service.main.route.helper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.util.logging.Log4j2
import org.apache.camel.Header
import org.apache.http.client.HttpResponseException
import yvolejn3.service.main.cache.DepthCache
import yvolejn3.service.main.cache.SymbolsCache
import yvolejn3.service.main.cache.TickerCache

@Log4j2
@Singleton
class ApiHelper {

    private ObjectMapper mapper

    @Inject
    private TickerCache tickerCache
    @Inject
    private DepthCache depthCache
    @Inject
    private SymbolsCache symbolsCache

    @Inject
    ApiHelper(ObjectMapper mapper) {
        this.mapper = mapper
    }

    JsonNode getDepth(@Header("symbol") String symbol) {
        checkSymbol(symbol)
        return mapper.valueToTree(
                ["ASKS": depthCache.getAsks(symbol),
                 "BIDS": depthCache.getBids(symbol)]
        )
    }

    JsonNode getTicker(@Header("symbol") String symbol) {
        checkSymbol(symbol)
        return mapper.valueToTree(tickerCache.get(symbol))
    }

    JsonNode getSymbols() {
        return mapper.valueToTree(symbolsCache.getSymbols())
    }

    private void checkSymbol(String symbol) {
        if (!symbolsCache.getSymbols().contains(symbol.toUpperCase()))
            throw new HttpResponseException(400, "Invalid symbol.")

    }
}