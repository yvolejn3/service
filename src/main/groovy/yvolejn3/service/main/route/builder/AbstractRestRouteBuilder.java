package yvolejn3.service.main.route.builder;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.http.client.HttpResponseException;
import yvolejn3.service.main.route.processors.FaultProcessor;

@Log4j2
public abstract class AbstractRestRouteBuilder extends RouteBuilder {

    @Inject
    @Named("server.port")
    private Integer port;

    @Inject
    @Named("server.hostname")
    private String hostname;

    @Inject
    FaultProcessor faultProcessor;

    @Override
    public void configure() {

        restConfiguration()
                .component("undertow")
                .endpointProperty("sslContextParameters", "mySslContextParameters")
                .host(hostname)
                .port(port)
                .enableCORS(true) // <-- Important
                .corsAllowCredentials(true) // <-- Important
                .corsHeaderProperty("Access-Control-Allow-Origin", "*")
                .corsHeaderProperty("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization")
                .bindingMode(RestBindingMode.json);




        errorHandler(defaultErrorHandler());

        onException(HttpResponseException.class)
                .handled(true)
                .process(faultProcessor);

    }
}
