package yvolejn3.service.main.route.processors

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.http.client.HttpResponseException
import groovy.util.logging.Log4j2

@Log4j2
class SecurityProcessor implements Processor {

    @Override
    void process(Exchange exchange) throws Exception {
        // парсим jwt токен
        String jwt = getJwt(exchange)
        Claims claims = getClaims(jwt)
        String principal = claims.getSubject()
        exchange.setProperty("principal", principal)
    }

    //Декодирует тело JWT не проверяя сигнатуру
    private static Claims getClaims(String token) throws HttpResponseException {
        try {
            int i = token.lastIndexOf('.')
            String withoutSignature = token.substring(0, i + 1)
            return Jwts.parser().parseClaimsJwt(withoutSignature).getBody()
        } catch (ExpiredJwtException e) {
            throw new HttpResponseException(401, e.message)
        }
    }

    //Проверяет валидность токена
    private static String getJwt(Exchange exchange) throws HttpResponseException {
        String auth = exchange.message.getHeader("Authorization", String)
        if (!auth) throw new HttpResponseException(401, "'Authorization' is missing or empty")
        if (!auth.contains("Bearer ")) throw new HttpResponseException(401, "JWT token must begin  with 'Bearer'")
        return auth.substring("Bearer".length()).trim()
    }
}
