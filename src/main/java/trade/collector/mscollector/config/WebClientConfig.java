package trade.collector.mscollector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // базовый URL ms-db берём из application.yml
    @Bean
    public WebClient msDbClient(@Value("${msdb.url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .clientConnector(new ReactorClientHttpConnector())
                // увеличим буфер
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg -> cfg.defaultCodecs()
                                .maxInMemorySize(2 * 1024 * 1024))
                        .build())
                .build();
    }

    @Bean
    public WebClient binanceWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.binance.com")
                .clientConnector(new ReactorClientHttpConnector())
                .build();
    }
}
