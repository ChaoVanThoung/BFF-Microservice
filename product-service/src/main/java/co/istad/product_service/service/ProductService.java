package co.istad.product_service.service;

import co.istad.product_service.domain.Product;
import co.istad.product_service.dto.ProductRequest;
import co.istad.product_service.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    // find all
    List<ProductResponse> findAll();

    // create
    ProductResponse create(ProductRequest productRequest);

    // find by id
    ProductResponse findById(String id);

}
