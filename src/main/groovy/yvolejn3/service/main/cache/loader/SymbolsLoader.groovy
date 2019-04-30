package yvolejn3.service.main.cache.loader

import yvolejn3.service.main.cache.AbstractCache

import java.util.stream.Collectors

class SymbolsLoader extends InstantLoader {

    List<String> symbols

    SymbolsLoader(String symbol, AbstractCache cache) {
        super(symbol, cache)
    }

    @Override
    void initialize() {
        symbols = restClient.exchangeInfo.symbols
                .stream()
                .map({ it.symbol })
                .collect(Collectors.toList())
    }
}
