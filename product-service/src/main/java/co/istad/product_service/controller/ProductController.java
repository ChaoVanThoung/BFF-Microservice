package co.istad.product_service.controller;

import co.istad.product_service.dto.ProductRequest;
import co.istad.product_service.dto.ProductResponse;
import co.istad.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    // find
    @GetMapping
    List<ProductResponse> findAll() {
        return productService.findAll();
    }

    // create
    @PostMapping
    ProductResponse create(@RequestBody ProductRequest productRequest) {
        return productService.create(productRequest);
    }

    // find by id
    @GetMapping("/{id}")
    ProductResponse findById(@PathVariable String id) {
        return productService.findById(id);
    }

}
