package yvolejn3.service.main.statistic

interface Listener {
    void doEvent(BigDecimal currentPrice);
}