package com.travelmate.destination_service.security;

import com.travelmate.destination_service.exception.ApiException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

public final class Pagination {
    private Pagination() {}

    public static Pageable de(int page, int size, Sort tri) {
        if (page < 0 || size < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Paramètres de pagination invalides.");
        }
        return PageRequest.of(page, Math.min(size, 50), tri);
    }
}
