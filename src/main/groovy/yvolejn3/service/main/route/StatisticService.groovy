package yvolejn3.service.main.route

import com.google.inject.Inject
import com.google.inject.Provider
import yvolejn3.service.main.route.builder.AbstractRestRouteBuilder
import yvolejn3.service.main.route.helper.StatisticHelper
import groovy.util.logging.Log4j2


@Log4j2
class StatisticService extends AbstractRestRouteBuilder {

    @Inject
    Provider<StatisticHelper> helper

    @Override
    void configure() {
        super.configure()

        rest("/queue")
                /*.get("/{symbol}")
                .route()
                .bean(helper.get())
                .endRest()*/

                .get()
                .route()
                .bean(helper.get(),"get")
                .endRest()

                .post()
                .route()
                .bean(helper.get(),"post")
                .endRest()

    }
}
