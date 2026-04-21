package com.karan.ecommerce_app.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class PaginatedResponseDTO {
    private List<ProductResponseDTO> products;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
    private boolean hasNext;
}
