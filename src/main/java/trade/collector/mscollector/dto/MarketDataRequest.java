package trade.collector.mscollector.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

// Модель, которую ждет POST /api/market-data в ms-db
@Data
@Builder
public class MarketDataRequest {
    private String symbol;
    @Builder.Default
    private String source = "BINANCE";
    private Map<String, Object> data;
    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal changePct;
    private Instant   fetchedAt;
}
