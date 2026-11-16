package org.drone.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.drone.loader.JsonFileProviderLoader;
import org.drone.registry.InMemoryProviderRegistry;
import org.drone.registry.ProviderRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestClient;

@Configuration
public class ProviderConfiguration {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public ProviderRegistry providerRegistry(
            @Value("${providers.json.path:}") String path,
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper) {
        if (path == null || path.isBlank()) {
            return new InMemoryProviderRegistry();
        }
        Resource resource = resourceLoader.getResource(path);
        JsonFileProviderLoader loader = new JsonFileProviderLoader(objectMapper, resource);
        return new InMemoryProviderRegistry(loader.load());
    }
}
