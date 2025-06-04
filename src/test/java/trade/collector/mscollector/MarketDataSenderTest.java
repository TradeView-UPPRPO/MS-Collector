package trade.collector.mscollector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import trade.collector.mscollector.dto.MarketDataRequest;
import trade.collector.mscollector.service.MarketDataSender;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketDataSenderTest {

    @Mock
    @Qualifier("msDbClient")
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec uriSpec;

    @Mock
    private WebClient.RequestBodySpec bodySpec;

    @Mock
    private WebClient.RequestHeadersSpec headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private MarketDataSender sender;

    @Test
    void send_Success() {
        // Arrange
        MarketDataRequest request = MarketDataRequest.builder()
                .symbol("BTCUSDT")
                .price(new BigDecimal("50000.0"))
                .volume(new BigDecimal("1000.0"))
                .changePct(new BigDecimal("2.5"))
                .fetchedAt(Instant.now())
                .data(Map.of(
                        "lastPrice", new BigDecimal("50000.0"),
                        "volume",    new BigDecimal("1000.0"),
                        "changePct", new BigDecimal("2.5")))
                .build();

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.contentType(any())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(sender.send(request))
                .verifyComplete();
    }

    @Test
    void send_Failure() {
        // Arrange
        MarketDataRequest request = MarketDataRequest.builder()
                .symbol("BTCUSDT")
                .price(new BigDecimal("50000.0"))
                .volume(new BigDecimal("1000.0"))
                .changePct(new BigDecimal("2.5"))
                .fetchedAt(Instant.now())
                .data(Map.of(
                        "lastPrice", new BigDecimal("50000.0"),
                        "volume",    new BigDecimal("1000.0"),
                        "changePct", new BigDecimal("2.5")))
                .build();
        RuntimeException ex = new RuntimeException("Network error");

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.contentType(any())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(ex));

        // Act & Assert
        StepVerifier.create(sender.send(request))
                .expectError(RuntimeException.class)
                .verify();
    }
}
