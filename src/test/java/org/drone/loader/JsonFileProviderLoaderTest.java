package org.drone.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.drone.model.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonFileProviderLoaderTest {

    @TempDir
    Path tempDir;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void load_validJsonFile() throws IOException {
        Path jsonFile = tempDir.resolve("providers.json");
        Files.writeString(jsonFile, """
                [
                  {"id": "p1", "baseUrl": "https://api.first.com", "apiKey": "key1"},
                  {"id": "p2", "baseUrl": "https://api.second.com", "apiKey": "key2"}
                ]
                """);

        Resource resource = new FileSystemResource(jsonFile);
        JsonFileProviderLoader loader = new JsonFileProviderLoader(objectMapper, resource);

        List<Provider> providers = loader.load();

        assertThat(providers).hasSize(2);
        assertThat(providers.get(0).id()).isEqualTo("p1");
        assertThat(providers.get(0).baseUrl()).isEqualTo("https://api.first.com");
        assertThat(providers.get(0).apiKey()).isEqualTo("key1");
        assertThat(providers.get(1).id()).isEqualTo("p2");
    }

    @Test
    void load_emptyArray_returnsEmptyList() throws IOException {
        Path jsonFile = tempDir.resolve("empty.json");
        Files.writeString(jsonFile, "[]");

        Resource resource = new FileSystemResource(jsonFile);
        JsonFileProviderLoader loader = new JsonFileProviderLoader(objectMapper, resource);

        List<Provider> providers = loader.load();

        assertThat(providers).isEmpty();
    }

    @Test
    void load_fileNotFound_throwsException() {
        Resource resource = new FileSystemResource(tempDir.resolve("nonexistent.json"));
        JsonFileProviderLoader loader = new JsonFileProviderLoader(objectMapper, resource);

        assertThatThrownBy(loader::load)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to load providers");
    }

    @Test
    void load_invalidJson_throwsException() throws IOException {
        Path jsonFile = tempDir.resolve("invalid.json");
        Files.writeString(jsonFile, "{not valid json}");

        Resource resource = new FileSystemResource(jsonFile);
        JsonFileProviderLoader loader = new JsonFileProviderLoader(objectMapper, resource);

        assertThatThrownBy(loader::load)
                .isInstanceOf(IllegalStateException.class);
    }
}
