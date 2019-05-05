package yvolejn3.service.main.route

import com.google.inject.Inject
import com.google.inject.Provider
import groovy.util.logging.Log4j2
import yvolejn3.service.main.route.builder.AbstractRestRouteBuilder
import yvolejn3.service.main.route.helper.AuthHelper

@Log4j2
class AuthService extends AbstractRestRouteBuilder {

    @Inject
    Provider<AuthHelper> authHelper

    @Override
    void configure() {
        super.configure()

        rest()
                .get()
                .route()
                .setBody(constant("Hello world"))
                .endRest()

        rest("/auth")
                .get()
                .route()
                .bean(authHelper.get())
                .endRest()

    }

}
