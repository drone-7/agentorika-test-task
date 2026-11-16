package org.drone.registry;

import org.drone.model.Provider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryProviderRegistryTest {

    @Test
    void replaceProviders_replacesEntireSet() {
        InMemoryProviderRegistry registry = new InMemoryProviderRegistry();
        registry.replaceProviders(List.of(
                new Provider("p1", "url1", "key1"),
                new Provider("p2", "url2", "key2")
        ));

        assertThat(registry.getProviders()).hasSize(2);

        registry.replaceProviders(List.of(new Provider("p3", "url3", "key3")));

        assertThat(registry.getProviders()).hasSize(1);
        assertThat(registry.getProvider("p3").baseUrl()).isEqualTo("url3");
        assertThatThrownBy(() -> registry.getProvider("p1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getProvider_returnsProviderById() {
        InMemoryProviderRegistry registry = new InMemoryProviderRegistry(
                List.of(new Provider("p1", "url1", "key1"))
        );

        Provider provider = registry.getProvider("p1");

        assertThat(provider.id()).isEqualTo("p1");
        assertThat(provider.baseUrl()).isEqualTo("url1");
        assertThat(provider.apiKey()).isEqualTo("key1");
    }

    @Test
    void getProvider_notFound_throwsException() {
        InMemoryProviderRegistry registry = new InMemoryProviderRegistry();

        assertThatThrownBy(() -> registry.getProvider("nonexistent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Provider not found: nonexistent");
    }

    @Test
    void constructor_initialProviders() {
        InMemoryProviderRegistry registry = new InMemoryProviderRegistry(
                List.of(
                        new Provider("p1", "url1", "key1"),
                        new Provider("p2", "url2", "key2")
                )
        );

        assertThat(registry.getProviders()).hasSize(2);
        assertThat(registry.getProvider("p1")).isNotNull();
        assertThat(registry.getProvider("p2")).isNotNull();
    }
}
