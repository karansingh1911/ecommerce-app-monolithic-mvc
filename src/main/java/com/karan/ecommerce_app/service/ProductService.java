package com.karan.ecommerce_app.service;

import com.karan.ecommerce_app.dto.product.*;
import com.karan.ecommerce_app.model.Product;
import com.karan.ecommerce_app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    //@Cacheable()
    //@Transactional(readOnly = true)
    //public List<ProductResponseDTO> getAllProducts() {
    //    List<ProductResponseDTO> listProductResponse = productRepository.findAll().stream().map(product -> {
    //        return ProductResponseDTO.builder().productId(product.getProductId()).productName(product.getProductName())
    //        .productCategory(product.getProductCategory()).productDescription(product.getProductDescription()).price(product.getPrice()).isAvailable(product.isAvailable()).build();
    //
    //    }).toList();
    //    return listProductResponse;
    //}
    @Cacheable(value = "productById", key = "#productId")
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Resource not found..."));

        return ProductResponseDTO.builder().productId(product.getProductId()).productName(product.getProductName()).productCategory(product.getProductCategory()).productDescription(product.getProductDescription()).price(product.getPrice()).isAvailable(product.isAvailable()).build();
    }

    @Caching(evict = {
            @CacheEvict(value = "productById",key = "#productId"),
            @CacheEvict(value = "productSearch", allEntries = true)
    })
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProductById(long productId) {
        // if record doesn't exist in DB then throw error!
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(productId);
    }
    @Caching(evict = {
            @CacheEvict(value = "productById",key = "#productId"),
            @CacheEvict(value = "productSearch", allEntries = true)
    })
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO updateProduct(Long productId, ProductUpdateRequestDTO requestDTO) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if (requestDTO.getProductName() != null) {
            product.setProductName(requestDTO.getProductName());
        }
        if (requestDTO.getProductDescription() != null) {
            product.setProductDescription(requestDTO.getProductDescription());
        }
        if (requestDTO.getProductCategory() != null) {
            product.setProductCategory(requestDTO.getProductCategory());
        }
        if (requestDTO.getProductBrand() != null) {
            product.setProductBrand(requestDTO.getProductBrand());
        }
        if (requestDTO.getPrice() != null) {
            product.setPrice(requestDTO.getPrice());
        }
        if (requestDTO.getStockQuantity() != null) {
            product.setStockQuantity(requestDTO.getStockQuantity());
        }

        Product saved = productRepository.save(product);

        return ProductResponseDTO.builder().productId(saved.getProductId()).productName(saved.getProductName()).productCategory(saved.getProductCategory()).productDescription(saved.getProductDescription()).price(saved.getPrice()).isAvailable(saved.isAvailable()).build();

    }
    @CacheEvict(value = "productImageById",key = "#productId")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateImageByProductId(long productId, MultipartFile image) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found."));
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Please upload a valid image.");
        }
        if (image.getSize() > 2 * 1024 * 1024) { // 2MB
            throw new IllegalArgumentException("File size exceeds limit");
        }
        String contentType = image.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
        // prevent even an admin to upload any executables, viruses and lie about the content type simultaneously!
        BufferedImage img;
        try {
            img = ImageIO.read(image.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read image file", e);
        }

        if (img == null) {
            throw new IllegalArgumentException("Invalid image content");
        }
        try {
            product.setProductImage(image.getBytes());
            product.setProductImageType(contentType);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store image", e);
        }
    }

    @Cacheable(value = "productSearch", key = "#safequery + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    @Transactional(readOnly = true)
    public PaginatedResponseDTO search(String safequery, Pageable pageable) { // if users passes
        // null string then lets
        // pass a safer string for the query that would be better
        Page<Product> pageResult = productRepository.findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCaseOrProductBrandContainingIgnoreCase(safequery, safequery, safequery, pageable);
        // get all the list of products so that it can be set inside the paginatedProductResponse
        List<ProductResponseDTO> products =
                pageResult.getContent().stream().map(p -> ProductResponseDTO.builder().productId(p.getProductId()).productName(p.getProductName()).price(p.getPrice()).productDescription(p.getProductDescription()).productCategory(p.getProductCategory()).productBrand(p.getProductBrand()).isAvailable(p.isAvailable()).build()).toList();
        // putting all the products inside the paginatedProductList
        return PaginatedResponseDTO.builder().products(products).page(pageResult.getNumber()).size(pageResult.getSize()).totalItems(pageResult.getTotalElements()).totalPages(pageResult.getTotalPages()).hasNext(pageResult.hasNext()).build();

    }

    @CacheEvict(value = "productSearch", allEntries = true)
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public PostProductResponseDTO createProduct(PostProductRequestDTO postProductRequestDTO) {
        Product product = new Product();
        product.setProductName(postProductRequestDTO.getProductName());
        product.setProductDescription(postProductRequestDTO.getProductDescription());
        product.setProductBrand(postProductRequestDTO.getProductBrand());
        product.setProductCategory(postProductRequestDTO.getProductCategory());
        product.setPrice(postProductRequestDTO.getPrice());
        product.setStockQuantity(postProductRequestDTO.getStockQuantity());

        Product savedProduct = productRepository.save(product);
        return PostProductResponseDTO.builder().productId(savedProduct.getProductId()).productName(savedProduct.getProductName()).productBrand(savedProduct.getProductBrand()).productCategory(savedProduct.getProductCategory()).productDescription(savedProduct.getProductDescription()).price(savedProduct.getPrice()).isAvailable(savedProduct.isAvailable()).stockQuantity(savedProduct.getStockQuantity()).productReleaseDate(savedProduct.getProductReleaseDate()).productUpdateDate(savedProduct.getProductUpdateDate()).build();
    }
    @Cacheable(value = "productImageById", key = "#productId")
    @Transactional(readOnly = true)
    public ProductImageResponseDTO getImageById(long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getProductImage() == null) {
            throw new RuntimeException("Image not found");
        }

        return ProductImageResponseDTO.builder().productImage(product.getProductImage()).productImageType(product.getProductImageType()).build();
    }

}
