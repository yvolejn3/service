package yvolejn3.service.main.bots

import com.binance.api.client.BinanceApiClientFactory
import com.google.common.cache.Cache
import com.google.inject.Inject
import org.apache.http.client.HttpResponseException
import groovy.util.logging.Log4j2
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import yvolejn3.service.main.database.entity.ApiKey
import yvolejn3.service.main.database.repository.ApiKeyRepository
import yvolejn3.service.main.database.repository.UserRepository

import javax.sql.DataSource

@Log4j2
class BinanceClientFactory {

    private Cache<String, BinanceApiClientFactory> cache
    private Jdbi jdbi

    @Inject
    BinanceClientFactory(DataSource dataSource, Cache<String, BinanceApiClientFactory> cache) {
        this.cache = cache
        this.jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(new SqlObjectPlugin())
    }

    BinanceApiClientFactory get(String login) {
        return cache.get(login, {
            def key = jdbi.withExtension(UserRepository, { it.getKey(login) }).orElse(new ApiKey())
            createClientFactory(key)
        })
    }

    private static BinanceApiClientFactory createClientFactory(ApiKey key) {
        return BinanceApiClientFactory.newInstance(key.apiKey, key.apiSecret)
    }
}
