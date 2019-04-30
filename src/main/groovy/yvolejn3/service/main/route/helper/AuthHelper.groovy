package yvolejn3.service.main.route.helper

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import groovy.util.logging.Log4j2
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.camel.Exchange
import org.apache.http.client.HttpResponseException
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import yvolejn3.service.main.database.repository.UserRepository

import javax.sql.DataSource
import java.nio.charset.Charset
import java.time.Instant

@Log4j2
class AuthHelper {

    private ObjectMapper mapper
    private Jdbi jdbi

    @Inject
    AuthHelper(DataSource dataSource, ObjectMapper mapper) {
        this.mapper = mapper
        this.jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(new SqlObjectPlugin())
    }

    void doSomething(Exchange exchange) {

        //проверяем Basic токен
        def authorization = exchange.message.getHeader("Authorization", String)
        if (!authorization?.startsWith("Basic"))
            throw new HttpResponseException(401, "authorization is empty or does not start with Basic")
        def base64token = authorization.substring("Basic".length()).trim()

        //достаем данные из base64token
        Map<String, String> credentials = getCredentials(base64token)
        exchange.message.removeHeader("Authorization")

        def username = credentials.username
        def password = credentials.password

        def isAuthorized = FakeLdap.check(username, password)

        if (!isAuthorized) throw new HttpResponseException(401, "incorrect login or password (Доступ только для YnInK Inc.)")

        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(getExpired())
                .setIssuer("YnInK inc.")
                .addClaims(["sn": "Сатоши Накомото"])
                .signWith(SignatureAlgorithm.HS512, [1, 2, 3] as byte[]).compact()

        log.info("${username}: /auth -> Bearer ${token}")
        exchange.message.setHeader("Token", "Bearer " + token)
        exchange.message.body = "Bearer " + token
    }

    private static Date getExpired() {
        int lifetime = 20
        lifetime = lifetime * 60
        return new Date((Instant.now().getEpochSecond() + lifetime) * 1000)
    }

    // декодируем base64 токен
    private static Map<String, String> getCredentials(String base64token) throws HttpResponseException {
        Map<String, String> credentials = new HashMap<>()
        try {
            String c = new String(Base64.decoder.decode(base64token), Charset.forName("UTF-8"))
            // с = username:password
            final String[] values = c.split(":", 2)
            credentials.put("username", values[0])
            credentials.put("password", values[1])
        } catch (Exception e) {
            log.error(e)
            throw new HttpResponseException(401, "wrong format basic token")
        }
        return credentials
    }


    static class FakeLdap {
        private static final def users = ["yoleinik": "123456!", "irfatku2": "123456", "kterkun": "123456"]

        static boolean check(String username, String password) {
            return password == users.get(username)
        }

    }
}
