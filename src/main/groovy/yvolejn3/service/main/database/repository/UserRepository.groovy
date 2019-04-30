package yvolejn3.service.main.database.repository

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import yvolejn3.service.main.database.entity.ApiKey
import yvolejn3.service.main.database.entity.User

interface UserRepository {

    @SqlUpdate("insert into users (login, name) values (:login, :name) ON DUPLICATE KEY UPDATE name = :name")
    void save(@BindBean User user);

    @RegisterBeanMapper(User.class)
    @SqlQuery("select * from users where login = :login")
    Optional<User> get(@Bind("login") String login)

    @RegisterBeanMapper(ApiKey.class)
    @SqlQuery("select * from users join api_keys using (name, login) where login = :login")
    Optional<ApiKey> getKey(@Bind("login") String login)

}