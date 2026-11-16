package org.drone.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.drone.model.Provider;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
public class JsonFileProviderLoader implements ProviderLoader {

    private final ObjectMapper objectMapper;
    private final Resource resource;

    @Override
    public List<Provider> load() {
        try (InputStream is = resource.getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load providers from " + resource.getDescription(), e);
        }
    }
}
