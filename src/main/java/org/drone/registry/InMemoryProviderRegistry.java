package org.drone.registry;

import lombok.NoArgsConstructor;
import org.drone.model.Provider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class InMemoryProviderRegistry implements ProviderRegistry {

    private final Map<String, Provider> providers = new ConcurrentHashMap<>();

    public InMemoryProviderRegistry(List<Provider> initialProviders) {
        replaceProviders(initialProviders);
    }

    @Override
    public Provider getProvider(String id) {
        Provider provider = providers.get(id);
        if (provider == null) {
            throw new IllegalArgumentException("Provider not found: " + id);
        }
        return provider;
    }

    @Override
    public void replaceProviders(List<Provider> providers) {
        this.providers.clear();
        providers.forEach(p -> this.providers.put(p.id(), p));
    }

    @Override
    public List<Provider> getProviders() {
        return List.copyOf(providers.values());
    }
}
