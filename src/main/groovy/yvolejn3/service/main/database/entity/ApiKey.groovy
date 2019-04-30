package yvolejn3.service.main.database.entity

import groovy.transform.*
import org.jdbi.v3.core.mapper.reflect.ColumnName

@Canonical
@TupleConstructor
//@EqualsAndHashCode(includes = ["name"])
@ToString(includePackage = false, includeNames=true, ignoreNulls = true)
class ApiKey {

    @ColumnName("name")
    String name
    @ColumnName("api_key")
    String apiKey
    @ColumnName("api_secret")
    String apiSecret

}


