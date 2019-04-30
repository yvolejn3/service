package yvolejn3.service.main.route

import com.google.inject.Inject
import com.google.inject.Provider
import groovy.util.logging.Log4j2
import yvolejn3.service.main.bots.Bot
import yvolejn3.service.main.route.builder.AbstractRestRouteBuilder
import yvolejn3.service.main.route.helper.ApiHelper
import yvolejn3.service.main.route.processors.SecurityProcessor

@Log4j2
class ApiService extends AbstractRestRouteBuilder {
    @Inject
    private Bot bot

    @Inject
    SecurityProcessor securityProcessor

    @Inject
    Provider<ApiHelper> apiHelper

    @Override
    void configure() {
        super.configure()

        interceptFrom().process(securityProcessor)

        rest("/bot")
                .get("/start")
                .route()
                .bean(bot, "start")
                .endRest()

        rest("/api")
                .get("/depth/{symbol}").route()
                .bean(apiHelper.get(), "getDepth")
                .endRest()

                .get("/ticker/{symbol}").route()
                .bean(apiHelper.get(), "getTicker")
                .endRest()

                .get("/symbols").route()
                .bean(apiHelper.get(), "getSymbols")
                .endRest()

    }

}
