package com.api.pos_backend.shared.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {
    public static Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    public static Pageable createPageable(int page, int size, String sortBy) {
        return PageRequest.of(page, size, Sort.by(sortBy));
    }

    public static Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }
}
