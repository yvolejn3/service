package yvolejn3.service.main.route

import com.google.inject.Inject
import yvolejn3.service.main.route.builder.AbstractRestRouteBuilder
import yvolejn3.service.main.route.helper.ProfileHelper
import groovy.util.logging.Log4j2
import yvolejn3.service.main.route.processors.SecurityProcessor

@Log4j2
class ProfileService extends AbstractRestRouteBuilder{

    @Inject
    ProfileHelper helper

    @Inject
    SecurityProcessor securityProcessor

    @Override
    void configure() {
        super.configure()

        interceptFrom().process(securityProcessor)

        rest("/profile")
                .get()
                .route()
                .bean(helper, "getProfile")
                .endRest()

                .post()
                .route()
                .bean(helper, "saveProfile")
                .endRest()

                .get("/key")
                .route()
                .bean(helper, "getKeys")
                .endRest()

                .post("/key")
                .route()
                .bean(helper, "saveKey")
                .endRest()

                .delete("/key")
                .route()
                .bean(helper, "deleteKey")
                .endRest()

    }

}
