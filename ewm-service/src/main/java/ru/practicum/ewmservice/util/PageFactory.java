package ru.practicum.ewmservice.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageFactory {
    public static Pageable createPageable(Integer from, Integer size, Sort.Direction sort, String sortBy) {
        return PageRequest.of(from / size, size, Sort.by(sort, sortBy));
    }
}
