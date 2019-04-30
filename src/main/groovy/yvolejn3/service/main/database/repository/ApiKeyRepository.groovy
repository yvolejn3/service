package yvolejn3.service.main.database.repository

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import yvolejn3.service.main.database.entity.ApiKey

interface ApiKeyRepository {

    @SqlUpdate("insert into api_keys (login, name, api_key, api_secret) values (:login, :name, :apiKey, :apiSecret) ON DUPLICATE KEY UPDATE api_key = :apiKey, api_secret = :apiSecret")
    void save(@Bind("login") String login,
                @BindBean ApiKey apiKey);


    @SqlUpdate("delete from api_keys where login = :login and name = :name")
    void delete(@Bind("login") String login,
                @Bind("name") String name);

    @RegisterBeanMapper(ApiKey.class)
    @SqlQuery("select * from api_keys where login = :login and name = :name")
    Optional<ApiKey> get(@Bind("login") String login,
                         @Bind("name") String name)

    @RegisterBeanMapper(ApiKey.class)
    @SqlQuery("select name from api_keys where login = :login")
    List<String> getAllNamesByLogin(@Bind("login") String login)

}