package yvolejn3.service.main.cache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.inject.Singleton
import groovy.util.logging.Log4j2
import yvolejn3.service.main.cache.loader.InstantLoader
import yvolejn3.service.main.cache.loader.StreamLoader
import yvolejn3.service.main.statistic.Listener

import java.lang.reflect.ParameterizedType
import java.util.concurrent.TimeUnit

@Log4j2
@Singleton
abstract class AbstractCache<T extends InstantLoader> {

    def listenerMap = new HashMap<String, List<Listener>>()

    private Cache<String, T> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.HOURS)
            .removalListener({
                if (it.value in StreamLoader) {
                    (it.value as StreamLoader).close()
                }
            })
            .build()

    protected T getCache(String key) {
        key = key?.toUpperCase() ?: ""
        def parameterizedType = getClass().genericSuperclass as ParameterizedType
        def clazz = parameterizedType.actualTypeArguments[0] as Class
        return cache.get(key, { clazz.newInstance(key, this) })
    }

    void addListener(String symbol, Listener listener) {
        listenerMap.computeIfAbsent(symbol, { ArrayList.newInstance() }).add(listener)
        getCache(symbol)
    }

    void notifyListeners(String symbol, BigDecimal price) {
        listenerMap.get(symbol.toUpperCase()).forEach({ it.doEvent(price) })
    }

}
