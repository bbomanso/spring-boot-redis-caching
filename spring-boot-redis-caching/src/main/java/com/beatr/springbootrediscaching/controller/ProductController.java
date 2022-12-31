package com.beatr.springbootrediscaching.controller;

import com.beatr.springbootrediscaching.entity.Product;
import com.beatr.springbootrediscaching.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @PostMapping("/")
    public Product saveInvoice(@RequestBody Product prod) {
        return service.saveProduct(prod);
    }

    @GetMapping("/")
    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return service.getProductById(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@RequestBody Product prod, @PathVariable Long id) {
        return service.updateProduct(prod, id);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return "Employee with id: "+id+ " Deleted!";
    }
}
