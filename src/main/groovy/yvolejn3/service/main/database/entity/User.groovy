package yvolejn3.service.main.database.entity

import groovy.transform.*
import org.jdbi.v3.core.mapper.reflect.ColumnName

@Canonical
@TupleConstructor
@EqualsAndHashCode(includes = "login")
@ToString(includePackage = false, includeNames=true, ignoreNulls = true)
class User {

    @ColumnName("login")
    String login

    @ColumnName("name")
    String name

}
