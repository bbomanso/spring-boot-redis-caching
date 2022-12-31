package com.beatr.springbootrediscaching.service;

import com.beatr.springbootrediscaching.entity.Product;
import com.beatr.springbootrediscaching.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Product saveProduct(Product prod) {
        return repository.save(prod);
    }
    public Product updateProduct(Product prod, Long prodId) {
        Product product = repository.findById(prodId).get();
        product.setProductName(prod.getProductName());
        product.setProductBrand(prod.getProductBrand());
        product.setProductPrice(prod.getProductPrice());

        return repository.save(product);
    }

    public void deleteProduct(Long prodId) {
        Product product = repository.findById(prodId).get();
        repository.delete(product);
    }

//    @Cacheable(value="products", key="#prodId")
    public Product getProductById(Long prodId) {
        Product product = repository.findById(prodId).get();
        return product;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }
}