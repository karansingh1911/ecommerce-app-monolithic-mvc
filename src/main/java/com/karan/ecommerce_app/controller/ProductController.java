package com.karan.ecommerce_app.controller;

import com.karan.ecommerce_app.config.PaginationProperties;
import com.karan.ecommerce_app.dto.product.*;
import com.karan.ecommerce_app.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequestMapping("/products")
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    PaginationProperties paginationProperties;

    //-> removed getAllProducts -> after paginated search filter impl
    // Unified endpoint: fetching all products is just a special case of search with no filters applied.
    //@GetMapping("/all")
    //public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
    //    return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    //}

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@Positive @PathVariable Long productId) {

        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }

    @GetMapping("/{productId}/image")
    public ResponseEntity<byte[]> getImageByProductId(@Positive @PathVariable long productId) {

        ProductImageResponseDTO imageDTO = productService.getImageById(productId);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM; // to create a default fallback - either browser download the
        // image - browser makes it Content-Type: application/octet-stream :
        // or doesn't display it inline
        try {
            if (imageDTO.getProductImageType() != null) {
                mediaType = MediaType.parseMediaType(imageDTO.getProductImageType());
            }
        } catch (Exception ignored) {
            // fallback stays application/octet-stream
        }


        return ResponseEntity.ok().contentType(mediaType).body(imageDTO.getProductImage());
    }


    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProductById(@Positive @PathVariable long productId) {
        productService.deleteProductById(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping
    public ResponseEntity<PostProductResponseDTO> createProduct(@Valid @RequestBody PostProductRequestDTO postProductRequestDTO) {
        return new ResponseEntity<>(productService.createProduct(postProductRequestDTO), HttpStatus.CREATED);

    }


    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProductById(@Positive @PathVariable Long productId,
                                                                @Valid @RequestBody ProductUpdateRequestDTO productUpdateRequestDTO) {
        return new ResponseEntity<>(productService.updateProduct(productId, productUpdateRequestDTO), HttpStatus.OK);
    }


    @PutMapping("/{productId}/image")
    public ResponseEntity<Void> updateImageByProductId(@Positive @PathVariable Long productId, @RequestParam("image") MultipartFile image) {
        productService.updateImageByProductId(productId, image);
        return ResponseEntity.noContent().build();
    }

    //GET /products/search?query=iphone&pageNumber=0&pageSize=5
    // GET /products/search?query=iphone&pageNumber=0&pageSize=5&sortBy=price&sortDir=desc
    // GET /products/search?pageNumber=0&pageSize=10
    @GetMapping("/search")
    public ResponseEntity<PaginatedResponseDTO> searchProducts(@RequestParam(required = false) String query,
                                                               @PositiveOrZero @RequestParam(required = false) Integer pageNumber,
                                                               @Positive @RequestParam(required = false) Integer pageSize,
                                                               @RequestParam(defaultValue = "productId") String sortBy,
                                                               @RequestParam(defaultValue = "asc") String sortDir) {

        // making sure null isn't passed into to repo so sanitizing it first
        String safeQuery = (query == null || query.isBlank()) ? "" : query;

        int pageToBeReturned = (pageNumber == null) ? paginationProperties.getDefaultPage() : pageNumber;
        int pageSizeToBeReturned = (pageSize == null) ? paginationProperties.getDefaultSize() : pageSize;

        if (pageSizeToBeReturned > paginationProperties.getMaxPageSize()) {
            throw new IllegalArgumentException("Page size exceeds limit");
        }
        if (pageToBeReturned < 0 || pageSizeToBeReturned <= 0) {
            throw new IllegalArgumentException("Invalid pagination values");
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageToBeReturned, pageSizeToBeReturned, sort);
        PaginatedResponseDTO responseDTO = productService.search(safeQuery, pageable);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);

    }

}
//NOTE :  spring NonNull now exists in Jspecify and not spring (learn about new API later)


