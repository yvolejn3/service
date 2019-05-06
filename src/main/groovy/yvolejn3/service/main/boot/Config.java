package yvolejn3.service.main.boot;

import com.binance.api.client.BinanceApiClientFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.guice.CamelModuleWithMatchingRoutes;
import org.apache.camel.guice.jndi.JndiBind;
import org.apache.camel.util.jsse.*;
import yvolejn3.service.main.route.ApiService;
import yvolejn3.service.main.route.AuthService;
import yvolejn3.service.main.route.ProfileService;
import yvolejn3.service.main.route.StatisticService;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Log4j2
public class Config extends CamelModuleWithMatchingRoutes {
    @Override
    protected void configure() {
        super.configure();
        bind(ApiService.class);
        bind(ProfileService.class);
        bind(AuthService.class);
        bind(StatisticService.class);
        log.debug("config initialized");
    }

    @Provides
    @Singleton
    Cache<String, BinanceApiClientFactory> getCache() {
        return CacheBuilder.newBuilder()
                .removalListener(n -> log.debug("Removed Exchange for: [{}], Cause: {}", n.getKey(), n.getCause()))
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    DataSource getDataSource(@Named("db.url") String url,
                             @Named("db.username") String username,
                             @Named("db.password") String password,
                             @Named("db.driver") String driver) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);
        return new HikariDataSource(config);
    }

    @Provides
    @Singleton
    @JndiBind("mySslContextParameters")
    SSLContextParameters getMySslContextParameters(@Named("ssl.keystore-path") String path,
                                                   @Named("ssl.keystore-password") String password) {
        SSLContextParameters scp = new SSLContextParameters();

        KeyStoreParameters ksp = new KeyStoreParameters();
        ksp.setResource(path);
        ksp.setPassword(password);

        KeyManagersParameters kmp = new KeyManagersParameters();
        kmp.setKeyStore(ksp);

        kmp.setKeyPassword(password);
        scp.setKeyManagers(kmp);

        SSLContextServerParameters scsp = new SSLContextServerParameters();
        scsp.setClientAuthentication(ClientAuthentication.WANT.name());
        scp.setServerParameters(scsp);

        return scp;
    }

}