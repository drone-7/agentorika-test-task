package org.drone.client;

import org.drone.model.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ProviderHttpClientTest {

    private static final String BASE_URL = "https://api.example.com";
    private static final String API_KEY = "test-key";
    private static final String PHONE = "79991234567";
    private static final Provider PROVIDER = new Provider("p1", BASE_URL, API_KEY);

    private ProviderHttpClient client;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ProviderHttpClient(builder.build());
    }

    @Test
    void checkSubscriber_okResponse_returnsTrue() {
        server.expect(requestTo(BASE_URL + "/subscribers/" + PHONE))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + API_KEY))
                .andRespond(withSuccess("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON));

        boolean result = client.checkSubscriber(PROVIDER, PHONE);

        assertThat(result).isTrue();
        server.verify();
    }

    @Test
    void checkSubscriber_notFound_returnsFalse() {
        server.expect(requestTo(BASE_URL + "/subscribers/" + PHONE))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        boolean result = client.checkSubscriber(PROVIDER, PHONE);

        assertThat(result).isFalse();
        server.verify();
    }

    @Test
    void checkSubscriber_serverError_throwsException() {
        server.expect(requestTo(BASE_URL + "/subscribers/" + PHONE))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> client.checkSubscriber(PROVIDER, PHONE))
                .isInstanceOf(HttpServerErrorException.class);

        server.verify();
    }

    @Test
    void checkSubscriber_nonOkStatus_returnsFalse() {
        server.expect(requestTo(BASE_URL + "/subscribers/" + PHONE))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"status\":\"error\"}", MediaType.APPLICATION_JSON));

        boolean result = client.checkSubscriber(PROVIDER, PHONE);

        assertThat(result).isFalse();
        server.verify();
    }

    @Test
    void checkSubscriber_badRequest_throwsException() {
        server.expect(requestTo(BASE_URL + "/subscribers/" + PHONE))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.checkSubscriber(PROVIDER, PHONE))
                .isInstanceOf(HttpClientErrorException.class);

        server.verify();
    }
}
