package trade.collector.mscollector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import trade.collector.mscollector.dto.BinanceTickerDto;
import trade.collector.mscollector.service.BinanceClient;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Добавляем LENIENT режим
class BinanceClientTest {

    @Mock
    @Qualifier("binanceWebClient")
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private BinanceClient binanceClient;

    @Test
    void fetch24hTicker_Success() {
        // Arrange
        String symbol = "BTCUSDT";
        BinanceTickerDto mockDto = new BinanceTickerDto();
        mockDto.setSymbol(symbol);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BinanceTickerDto.class)).thenReturn(Mono.just(mockDto));

        // Act & Assert
        StepVerifier.create(binanceClient.fetch24hTicker(symbol))
                .expectNext(mockDto)
                .verifyComplete();
    }

    @Test
    void fetch24hTicker_Error() {
        // Arrange
        String symbol = "INVALID";
        RuntimeException ex = new RuntimeException("API error");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BinanceTickerDto.class)).thenReturn(Mono.error(ex));

        // Act & Assert
        StepVerifier.create(binanceClient.fetch24hTicker(symbol))
                .expectError(RuntimeException.class)
                .verify();
    }
}
