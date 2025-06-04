package trade.collector.mscollector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import trade.collector.mscollector.dto.BinanceTickerDto;
import trade.collector.mscollector.dto.MarketDataRequest;
import trade.collector.mscollector.service.BinanceClient;
import trade.collector.mscollector.service.CollectorScheduler;
import trade.collector.mscollector.service.MarketDataSender;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectorSchedulerTest {

    @Mock
    private BinanceClient binanceClient;

    @Mock
    private MarketDataSender sender;

    private CollectorScheduler.CollectorProps props;
    private CollectorScheduler scheduler;

    @BeforeEach
    void setUp() {
        // Инициализируем props перед каждым тестом
        props = new CollectorScheduler.CollectorProps();
        props.setSymbols(List.of("BTCUSDT", "ETHUSDT"));

        // Создаем экземпляр CollectorScheduler с инициализированными зависимостями
        scheduler = new CollectorScheduler(props, binanceClient, sender);
    }

    @Test
    void collect_SuccessfulProcessing() {
        // Arrange
        BinanceTickerDto btcTicker = createTicker("BTCUSDT", 50000.0, 1000.0, 1.5);
        BinanceTickerDto ethTicker = createTicker("ETHUSDT", 3500.0, 500.0, 2.0);

        when(binanceClient.fetch24hTicker("BTCUSDT")).thenReturn(Mono.just(btcTicker));
        when(binanceClient.fetch24hTicker("ETHUSDT")).thenReturn(Mono.just(ethTicker));
        when(sender.send(any(MarketDataRequest.class))).thenReturn(Mono.empty());

        // Act
        scheduler.collect();

        // Assert
        verify(sender, times(2)).send(any());
        verify(sender).send(argThat(req ->
                req.getSymbol().equals("BTCUSDT") &&
                        req.getPrice().equals(new BigDecimal("50000"))
        ));
        verify(sender).send(argThat(req ->
                req.getSymbol().equals("ETHUSDT") &&
                        req.getPrice().equals(new BigDecimal("3500"))
        ));
    }

    @Test
    void collect_WithBinanceError() {
        // Arrange
        BinanceTickerDto btcTicker = createTicker("BTCUSDT", 50000.0, 1000.0, 1.5);

        when(binanceClient.fetch24hTicker("BTCUSDT")).thenReturn(Mono.just(btcTicker));
        when(binanceClient.fetch24hTicker("ETHUSDT")).thenReturn(Mono.error(new RuntimeException("API down")));
        when(sender.send(any(MarketDataRequest.class))).thenReturn(Mono.empty());

        // Act
        scheduler.collect();

        // Assert
        verify(sender, times(1)).send(any());
        verify(sender).send(argThat(req -> req.getSymbol().equals("BTCUSDT")));
    }

    @Test
    void collect_WithSenderError() {
        // Arrange
        // Обновляем символы только для BTC
        props.setSymbols(List.of("BTCUSDT"));

        BinanceTickerDto btcTicker = createTicker("BTCUSDT", 50000.0, 1000.0, 1.5);

        when(binanceClient.fetch24hTicker("BTCUSDT")).thenReturn(Mono.just(btcTicker));
        when(sender.send(any(MarketDataRequest.class))).thenReturn(Mono.error(new RuntimeException("DB unreachable")));

        // Act
        scheduler.collect();

        // Assert
        verify(sender, times(1)).send(any());
    }

    private BinanceTickerDto createTicker(String symbol, double price, double volume, double change) {
        BinanceTickerDto dto = new BinanceTickerDto();
        dto.setSymbol(symbol);
        dto.setLastPrice(new BigDecimal(price));
        dto.setVolume(new BigDecimal(volume));
        dto.setPriceChangePercent(new BigDecimal(change));
        return dto;
    }
}
