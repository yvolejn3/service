package yvolejn3.service.main.route.processors

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import groovy.util.logging.Log4j2
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.http.client.HttpResponseException

@Log4j2
class FaultProcessor implements Processor {

    @Override
    void process(Exchange exchange) throws Exception {
        log.debug(String.format("fault process : { %s}", exchange.getIn().getHeaders()))
        HttpResponseException e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, HttpResponseException.class)
        log.error(e)
        //e.printStackTrace();

        setErrorMessage(exchange, e)

        if (exchange.getPattern().isOutCapable()) {
            exchange.out.body = exchange.message.body
            exchange.message.body = null
            exchange.out.headers.putAll(exchange.message.headers)
        }
    }

    private static void setErrorMessage(Exchange exchange, HttpResponseException e) throws Exception {
        ObjectMapper mapper = new ObjectMapper()
        ObjectNode errorBody = mapper.createObjectNode()
        errorBody.put("error_code", e.getStatusCode())
        errorBody.put("error_message", e.getReasonPhrase())

        exchange.message.removeHeaders("*")
        exchange.message.setHeader(Exchange.CONTENT_TYPE, "application/json")
        exchange.message.setHeader(Exchange.HTTP_RESPONSE_CODE, e.getStatusCode())

        String fault = mapper.writeValueAsString(errorBody)
        log.debug("fault message: $fault")
        exchange.message.body = fault
    }
}
