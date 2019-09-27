package yvolejn3.service.main.route

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Provider
import groovy.util.logging.Log4j2
import yvolejn3.service.main.route.builder.AbstractRestRouteBuilder
import yvolejn3.service.main.route.helper.AuthHelper

import java.util.stream.Collectors
import java.util.stream.IntStream

@Log4j2
class AuthService extends AbstractRestRouteBuilder {

    @Inject
    Provider<AuthHelper> authHelper
    ObjectMapper mapper = new ObjectMapper()

    @Override
    void configure() {
        super.configure()
        rest()
                .get()
                .route()
                .process({
                    def millis = it.getIn().getHeader("sleep", Integer)
                    def size = it.getIn().getHeader("size", 0, Integer)
                    if (millis) sleep(millis)

                    def content = IntStream.range(0, size)
                            .boxed()
                            .collect(Collectors.toMap({ it }, { UUID.randomUUID() }))
                    Map<String, Object> body = [
                            "size"   : size,
                            "content": content
                    ]
                    it.getIn().setBody(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body))
                })
        .convertBodyTo(String.class)
                .endRest()

        rest("/auth")
                .get()
                .route()
                .bean(authHelper.get())
                .endRest()

    }

}
