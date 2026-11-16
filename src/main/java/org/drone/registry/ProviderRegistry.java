package org.drone.registry;

import org.drone.model.Provider;

import java.util.List;

public interface ProviderRegistry {

    Provider getProvider(String id);

    void replaceProviders(List<Provider> providers);

    List<Provider> getProviders();
}
