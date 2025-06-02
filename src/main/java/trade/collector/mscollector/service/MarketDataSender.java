package trade.collector.mscollector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import trade.collector.mscollector.dto.MarketDataRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataSender {

    @Qualifier("msDbClient")
    private final WebClient msDbClient;

    public Mono<Void> send(MarketDataRequest req) {
        return msDbClient.post()
                .uri("/api/market-data")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.debug("Sent marketData for {}", req.getSymbol()))
                .doOnError(e -> log.error("Failed to send marketData for {}", req.getSymbol(), e));
    }
}