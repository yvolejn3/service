package yvolejn3.service.main.bots.impl


import com.binance.api.client.BinanceApiRestClient
import com.binance.api.client.domain.account.Account
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import groovy.util.logging.Log4j2
import org.apache.camel.ExchangeProperty
import yvolejn3.service.main.bots.BinanceClientFactory
import yvolejn3.service.main.bots.Bot

import java.util.stream.Collectors

@Log4j2
class DefaultBot implements Bot {

    private BinanceClientFactory restClientFactory

    @Inject
    DefaultBot(BinanceClientFactory restClientFactory) {
        this.restClientFactory = restClientFactory
    }

    @Override
    JsonNode start(@ExchangeProperty("principal") String login) {

        //DepthCache d = new DepthCache(restClientFactory.get(login), "xmrbtc")

        BinanceApiRestClient restClient = restClientFactory.get(login).newRestClient()


        long time = System.currentTimeMillis()
        Account account = restClient.account

        def list = account.balances
                .stream()
                .filter({ (new BigDecimal(it.free) != BigDecimal.ZERO) })
                .collect(Collectors.toList())

        log.info("${login}: start -> ${list}")
        log.info("Бот успешно отработал (${System.currentTimeMillis() - time}ms)")
        log.info("--------------------------------------")

        return new ObjectMapper().valueToTree(list)
    }


}
