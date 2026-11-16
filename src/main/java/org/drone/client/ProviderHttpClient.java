package org.drone.client;

import lombok.RequiredArgsConstructor;
import org.drone.model.Provider;
import org.drone.model.SubscriberResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ProviderHttpClient {

    private final RestClient restClient;

    public boolean checkSubscriber(Provider provider, String phone) {
        try {
            SubscriberResponse response = restClient.get()
                    .uri(provider.baseUrl() + "/subscribers/{phone}", phone)
                    .header("Authorization", "Bearer " + provider.apiKey())
                    .retrieve()
                    .body(SubscriberResponse.class);
            return response != null && "ok".equals(response.status());
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }
}
