package co.istad.product_service.service;

import co.istad.product_service.domain.Product;
import co.istad.product_service.dto.ProductRequest;
import co.istad.product_service.dto.ProductResponse;
import co.istad.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> findAll() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(
                p -> ProductResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .price(p.getPrice())
                        .build()
        ).toList();
    }

    @Override
    public ProductResponse create(ProductRequest productRequest) {

        Product product = new Product();
        product.setTitle(productRequest.title());
        product.setPrice(productRequest.price());
        Product saveProduct = productRepository.save(product);

        return ProductResponse.builder()
                .id(saveProduct.getId())
                .title(saveProduct.getTitle())
                .price(saveProduct.getPrice())
                .build();
    }

    @Override
    public ProductResponse findById(String id) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(id)
                .title(product.getTitle())
                .price(product.getPrice())
                .build();
    }
}
