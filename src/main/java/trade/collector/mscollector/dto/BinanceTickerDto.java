package trade.collector.mscollector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BinanceTickerDto {
    private String symbol;

    @JsonProperty("lastPrice")
    private BigDecimal lastPrice;

    private BigDecimal volume;

    @JsonProperty("priceChangePercent")
    private BigDecimal priceChangePercent;
}