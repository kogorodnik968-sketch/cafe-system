package com.cafe.cafeapp.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class CacheKey {
    private final Long productId;
    private final BigDecimal minTotal;
    private final int  pageNumber;
    private final int pageSize;
    private final String sort;

    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        CacheKey cacheKey = (CacheKey) obj;

        return pageNumber == cacheKey.pageNumber &&
                pageSize == cacheKey.pageSize &&
                Objects.equals(productId, cacheKey.productId) &&
                Objects.equals(minTotal, cacheKey.minTotal) &&
                Objects.equals(sort, cacheKey.sort);
    }

    @Override
    public int hashCode () {
        return Objects.hash(productId, minTotal, pageNumber, pageSize, sort);
    }

}
