package com.cafe.cafeapp.cache;

import com.cafe.cafeapp.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class QueryCacheService {

    private final Map<CacheKey, Page<OrderResponseDto>> cache = new HashMap<>();

    public Page<OrderResponseDto> get(CacheKey key) {
        return cache.get(key);
    }

    public void put(CacheKey key, Page<OrderResponseDto> value) {
        cache.put(key, value);
    }

    public void invalidateByProductId(String productId) {
        cache.keySet().removeIf(key -> key.getProductName().equals(productId));
    }
}
