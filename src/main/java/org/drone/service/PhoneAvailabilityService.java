package org.drone.service;

import lombok.RequiredArgsConstructor;
import org.drone.client.ProviderHttpClient;
import org.drone.model.Provider;
import org.drone.registry.ProviderRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneAvailabilityService {

    private final ProviderRegistry registry;
    private final ProviderHttpClient httpClient;

    public boolean isAvailable(String providerId, String phone) {
        Provider provider = registry.getProvider(providerId);
        return httpClient.checkSubscriber(provider, phone);
    }
}
