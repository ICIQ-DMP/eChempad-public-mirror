package org.ICIQ.eChempad.configurations.Utilities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    /**
     * https://stackoverflow.com/questions/59735951/databufferlimitexception-exceeded-limit-on-max-bytes-to-buffer-webflux-error
     * Increase the data buffer size for the webClient, so it can download bigger files, such as uploadedResource:a73eb824-0e97-4ec7-9ea9-43899e34fb13
     * @return A webClient instance with increased buffer sizes.
     */
    @Bean
    public WebClient webClient()
    {
        final int size = 16 * 1024 * 1024;  // 16 MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }
}
