package yvolejn3.service.main.route.helper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import org.apache.camel.Body
import org.apache.camel.ExchangeProperty
import org.apache.camel.Header
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import yvolejn3.service.main.database.entity.ApiKey
import yvolejn3.service.main.database.entity.User
import yvolejn3.service.main.database.repository.ApiKeyRepository
import yvolejn3.service.main.database.repository.UserRepository
import groovy.util.logging.Log4j2

import javax.sql.DataSource

@Log4j2
class ProfileHelper {

    private ObjectMapper mapper
    private Jdbi jdbi

    @Inject
    ProfileHelper(DataSource dataSource, ObjectMapper mapper) {
        this.mapper = mapper
        this.jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(new SqlObjectPlugin())
    }

    JsonNode getProfile(@ExchangeProperty("principal") String login) {
        def user = jdbi.withExtension(UserRepository, { it.get(login) }).orElse(new User(login))
        log.info("${login}: getProfile -> ${user}")
        return mapper.valueToTree(user)
    }

    JsonNode saveProfile(@ExchangeProperty("principal") String login, @Body Map<String, Object> body) {
        def user = jdbi.withExtension(UserRepository, { it.get(login) }).orElse(new User(login))
        user.name = body.name
        jdbi.useExtension(UserRepository, { it.save(user) })
        log.info("${login}: save -> ${user}")
        return mapper.valueToTree(user)
    }

    JsonNode getKeys(@ExchangeProperty("principal") String login) {
        def list = jdbi.withExtension(ApiKeyRepository, { it.getAllNamesByLogin(login) })
        log.info("${login}: getKeys -> ${list}")
        return mapper.valueToTree(list)
    }

    JsonNode saveKey(@ExchangeProperty("principal") String login, @Body Map<String, String> body) {
        def key = jdbi.withExtension(ApiKeyRepository, { it.get(login, body.name) }).orElse(new ApiKey())
        body.forEach(key.&setProperty)
        jdbi.useExtension(ApiKeyRepository, { it.save(login, key) })
        log.info("${login}: save -> ${key}")
        return mapper.valueToTree(key)
    }

    void deleteKey(@ExchangeProperty("principal") String login, @Header("name") String name) {
        jdbi.useExtension(ApiKeyRepository, { it.delete(login, name) })
        log.info("${login}: delete key -> (${name})")
    }

}
