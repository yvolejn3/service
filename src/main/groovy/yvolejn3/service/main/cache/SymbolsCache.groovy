package yvolejn3.service.main.cache

import yvolejn3.service.main.cache.loader.SymbolsLoader

class SymbolsCache extends AbstractCache<SymbolsLoader> {

    List<String> getSymbols() {
        return getCache().symbols
    }

}
