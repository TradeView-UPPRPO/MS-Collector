package trade.collector.mscollector.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import trade.collector.mscollector.dto.MarketDataRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectorScheduler {

    private final CollectorProps props;
    private final BinanceClient binanceClient;
    private final MarketDataSender sender;

    // раз в fixedDelay опрашиваем Binance для каждого символа
    @Scheduled(initialDelayString = "${collector.initialDelay:5000}",
            fixedDelayString   = "${collector.fixedDelay:60000}")
    public void collect() {
        Flux.fromIterable(props.getSymbols())
                .flatMap(sym -> binanceClient.fetch24hTicker(sym)
                        .flatMap(ticker -> sender.send(
                                MarketDataRequest.builder()
                                        .symbol(ticker.getSymbol())
                                        .price(ticker.getLastPrice())
                                        .volume(ticker.getVolume())
                                        .changePct(ticker.getPriceChangePercent())
                                        .fetchedAt(Instant.now())
                                        .data(Map.of(
                                                "lastPrice", ticker.getLastPrice(),
                                                "volume",    ticker.getVolume(),
                                                "changePct", ticker.getPriceChangePercent()))
                                        .build())))
                .onErrorContinue((e, obj) ->
                        log.warn("pipeline error for {}", obj, e))
                .blockLast();
    }

    @Configuration
    @ConfigurationProperties(prefix = "collector")
    @Getter
    @Setter
    public static class CollectorProps {
        private List<String> symbols = List.of("BTCUSDT", "ETHUSDT");
        private long fixedDelay = 60_000;
        private long initialDelay = 5_000;
    }
}
