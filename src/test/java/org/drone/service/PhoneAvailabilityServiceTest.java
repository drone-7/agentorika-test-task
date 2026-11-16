package org.drone.service;

import org.drone.client.ProviderHttpClient;
import org.drone.model.Provider;
import org.drone.registry.ProviderRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhoneAvailabilityServiceTest {

    @Mock
    private ProviderRegistry registry;

    @Mock
    private ProviderHttpClient httpClient;

    @InjectMocks
    private PhoneAvailabilityService service;

    @Test
    void isAvailable_providerFound_delegatesToHttpClient() {
        Provider provider = new Provider("p1", "https://api.example.com", "key");
        when(registry.getProvider("p1")).thenReturn(provider);
        when(httpClient.checkSubscriber(provider, "79991234567")).thenReturn(true);

        boolean result = service.isAvailable("p1", "79991234567");

        assertThat(result).isTrue();
    }

    @Test
    void isAvailable_providerNotFound_throwsException() {
        when(registry.getProvider("unknown")).thenThrow(new IllegalArgumentException("Provider not found: unknown"));

        assertThatThrownBy(() -> service.isAvailable("unknown", "79991234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Provider not found: unknown");
    }

    @Test
    void isAvailable_httpReturnsFalse_returnsFalse() {
        Provider provider = new Provider("p1", "https://api.example.com", "key");
        when(registry.getProvider("p1")).thenReturn(provider);
        when(httpClient.checkSubscriber(provider, "79991234567")).thenReturn(false);

        boolean result = service.isAvailable("p1", "79991234567");

        assertThat(result).isFalse();
    }
}
