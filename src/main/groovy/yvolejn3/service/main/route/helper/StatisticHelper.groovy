package yvolejn3.service.main.route.helper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import groovy.util.logging.Log4j2
import org.apache.camel.Exchange
import org.apache.camel.ExchangeProperty
import org.apache.camel.Header
import org.apache.http.client.HttpResponseException
import yvolejn3.service.main.cache.DepthCache
import yvolejn3.service.main.cache.SymbolsCache
import yvolejn3.service.main.statistic.RankedPair
import yvolejn3.service.main.statistic.Statistic
import yvolejn3.service.main.statistic.Utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.stream.Collectors
import java.util.stream.IntStream

@Log4j2
@Singleton
class StatisticHelper {

    @Inject
    private SymbolsCache symbolsCache
    @Inject
    private DepthCache depthCache

    @Inject
    private Provider<Statistic> statisticProvider

    Map<String, Statistic> statisticMap = new HashMap<>()

    private ObjectMapper mapper

    @Inject
    StatisticHelper(ObjectMapper mapper) {
        this.mapper = mapper
    }

    JsonNode get(@Header("symbol") String symbol, @Header("rank") String rank) {
        if (symbol) {
            symbol = checkSymbol(symbol)
            return mapper.valueToTree(calculate(symbol, rank))
        } else {
            def map = statisticMap.keySet()
                    .stream()
                    .map({ it.toUpperCase() })
                    .collect(Collectors.toMap(
                    { String key -> key },
                    { String key -> calculate(key, rank) }
            ))
            return mapper.valueToTree(map)
        }


        /*def statistic = statisticMap.computeIfAbsent(symbol, { statisticProvider.get() })
        def rankedPairs = statistic.rankedPairList.stream()
                .filter({ !rank || it.rank > Integer.valueOf(rank) })
                .collect(Collectors.toList())

        def max = rankedPairs.stream().mapToInt({ it.stat.size() }).max()
        if (max.isPresent() && max.getAsInt() > 0) {
            def averageList = IntStream.range(0, max.getAsInt())
                    .mapToObj({ i ->
                        def avg = rankedPairs.stream()
                                .filter({ it.stat.size() > i })
                                .mapToLong({ it.stat.get(i) })
                                .average().orElseThrow().longValue()
                        def percent = rankedPairs.stream()
                                .filter({ it.stat.size() <= i })
                                .count() / rankedPairs.size()
                        "прибыль: $i% -> риск: ${percent.movePointRight(2).intValue()}% среднее время продажи: ${Utils.millsToString(avg)}".toString()
                    })
                    .collect(Collectors.toList())
            return mapper.valueToTree(averageList)
        }*/
    }

    List<String> calculate(String symbol, String rank) {
        def statistic = statisticMap.computeIfAbsent(symbol, { statisticProvider.get() })
        def rankedPairs = statistic.rankedPairList.stream()
                .filter({ !rank || it.rank > Integer.valueOf(rank) })
                .collect(Collectors.toList())
        def max = rankedPairs.stream().mapToInt({ it.stat.size() }).max()

        def averageList = new ArrayList()
        if (max.isPresent() && max.getAsInt() > 0) {
            averageList = IntStream.range(0, max.getAsInt())
                    .mapToObj({ i ->
                def avg = rankedPairs.stream()
                        .filter({ it.stat.size() > i })
                        .mapToLong({ it.stat.get(i) })
                        .average().orElseThrow().longValue()
                def percent = rankedPairs.stream()
                        .filter({ it.stat.size() <= i })
                        .count() / rankedPairs.size()
                "прибыль: $i% -> риск: ${percent.movePointRight(2).intValue()}% среднее время продажи: ${Utils.millsToString(avg)}".toString()
            })
                    .collect(Collectors.toList())
        }
        return averageList
    }


    JsonNode post(@Header("symbol") String symbol,
                  @Header("price") String priceStr,
                  @Header("rank") int rank,
                  @ExchangeProperty(Exchange.CREATED_TIMESTAMP) Date date) {
        symbol = checkSymbol(symbol)
        def price = parsePrice(priceStr)
        def pair = new RankedPair(rank, symbol, price)

        //log.info("Костя отправил в очередь ${symbol}:${price} ранг $rank")

        statisticMap.computeIfAbsent(symbol, { statisticProvider.get() }).add(pair)
        depthCache.addListener(symbol, pair)
        return mapper.valueToTree("OK")
    }

    private static BigDecimal parsePrice(String price) {
        try {
            return new BigDecimal(price)
        } catch (Exception ignored) {
            throw new HttpResponseException(400, "Wrong price parameter")
        }
    }

    private static BigDecimal toBigDecimal(String price, Locale locale) {
        def df = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
        df.setParseBigDecimal(true)
        return df.parse(price) as BigDecimal
    }

    private String checkSymbol(String symbol) {
        symbol = symbol.toUpperCase()
        if (!symbolsCache.getSymbols().contains(symbol))
            throw new HttpResponseException(400, "Invalid symbol.")
        return symbol
    }


}
