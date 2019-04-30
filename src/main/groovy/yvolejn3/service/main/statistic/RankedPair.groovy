package yvolejn3.service.main.statistic

import groovy.transform.Canonical
import groovy.transform.TupleConstructor

@Canonical
@TupleConstructor(excludes = "stat")
class RankedPair implements Listener {

    int rank
    String symbol
    BigDecimal price
    long timestamp = System.currentTimeMillis()
    List<Long> stat = new ArrayList<>()

    @Override
    void doEvent(BigDecimal currentPrice) {
        def change = (currentPrice / price - 1) * 100
        //println("${symbol} изменилась на ${change.setScale(2, BigDecimal.ROUND_HALF_EVEN)}%")
        if (change >= stat.size()) {
            stat.add(System.currentTimeMillis() - timestamp)
        }
    }

}
