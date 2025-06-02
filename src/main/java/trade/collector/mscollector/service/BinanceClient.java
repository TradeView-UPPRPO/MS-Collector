package trade.collector.mscollector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import trade.collector.mscollector.dto.BinanceTickerDto;

@Slf4j
@Component
public class BinanceClient {

    private final WebClient webClient;

    public BinanceClient(@Qualifier("binanceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<BinanceTickerDto> fetch24hTicker(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/ticker/24hr")
                        .queryParam("symbol", symbol)
                        .build())
                .retrieve()
                .bodyToMono(BinanceTickerDto.class)
                .doOnError(e -> log.error("Binance 24h ticker error for {}", symbol, e));
    }
}