# Implementing Redis Cache (Step-by-Step)

## Step 1: Create a Spring Boot Project in Spring Initializr

- If you are using IntelliJ IDE create a new project.
  
![image](https://user-images.githubusercontent.com/119079911/210106704-34fdf25c-d1ba-4522-87de-4c0fda7825a1.png)

- On the left menu select *Spring Initializr*.
- After that, a screen to configure the initializr website and JDK to use in this project will be displayed on the left side.
  - **Name**: give a name to your Spring Boot Application.
  - **Localtion**: Select the folder where you want your project to be created.
  - **Language**: select *Java*.
  - **Type**: select *Maven*.
  - **Group**: provide a unique base name of the company or group that created the project.
    - In my case I used it com.beatr (Beatriz)
  - **Artifact**: provide a unique name of the project.
    - You can use the same name as your project name.
  - **Package name**: You can either leave the default name or you can change as per your need.
  - **JDK**: Select your JDK.
  - **Java**: Select your Java version.
  - **Packaging**: Select *Jar*.
- Select *Next* button.

## Step 2: Configure Project Dependencies

- We need to add dependencies as per our project requirements.
  
![image](https://user-images.githubusercontent.com/119079911/210108378-2a332f9a-8fc3-4c7c-a773-4ef04d1970f9.png)

- For this project, select the following dependencies:
  1. Spring Web
  2. Spring Data JPA
  3. H2 Database
  4. Spring Data Redis (Access + Driver)
- Select *Create* button.

After that, the project structure is created by the IDE, and the dependencies are downloaded and stored in the *External Libraries* folder as *Jar* files.

![image](https://user-images.githubusercontent.com/119079911/210109293-30325431-f761-4dca-8ea5-c8a64690dd18.png)

*SpringBootRedisCachingApplication* contains de code that will run your SpringBoot application.

- This class contains a main() method that instantiates and runs your application.

## Step 3: Update application.properties

write the following to your application.properties:

```
# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.show-sql=true

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.redis.time-to-live=60000
spring.cache.cache-names=products
spring.cache.type=redis
```

## Step 4: Add @EnableCaching Annotation at Starter Class

```
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class  SpringBootRedisCachingApplication {

   public static void main(String[] args) {
      SpringApplication.run( SpringBootRedisCachingApplication.class, args);
   }
}
```

 ## Step 5: Create Different Packages for Different Layers

1. **Entity Layer**: Contains the object that will be mapped to the database table.
2. **Repository Layer**: Repository interfaces are included in this layer which are used to connect with the database and access the data stored in it.
3. **Service Layer**: All the business logic should be included in this layer
4. **Controller Layer**: Controller layer is used to take the incoming requests and pass the request to the service layer to perform some business logic.

![image](https://user-images.githubusercontent.com/119079911/210125361-952377f7-19c1-4ebd-bf1a-6182ae538113.png)

## Step 6: Create an Entity Class as Product

```
package com.beatr.springbootrediscaching.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "product")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    private String productName;
    private String productBrand;
    private double productPrice;

    public Product() {
    }

    public Product(String productName, String productBrand, int productPrice) {
        this.productName = productName;
        this.productBrand = productBrand;
        this.productPrice = productPrice;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    @Override
    public String toString() {
        return "Product [" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productBrand='" + productBrand + '\'' +
                ", productPrice=" + productPrice +
                ']';
    }
}
```

## Step 7: Create a Repository Interface as ProductRepository

```
package com.beatr.springbootrediscaching.repository;

import com.beatr.springbootrediscaching.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
```

## Step 8: Create a Service Class as ProductService

```
package com.beatr.springbootrediscaching.service;

import com.beatr.springbootrediscaching.entity.Product;
import com.beatr.springbootrediscaching.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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

    @CachePut(value="products", key="#prodId")
    public Product updateProduct(Product prod, Long prodId) {
        Product product = repository.findById(prodId).get();
        product.setProductName(prod.getProductName());
        product.setProductBrand(prod.getProductBrand());
        product.setProductPrice(prod.getProductPrice());

        return repository.save(product);
    }

    @CacheEvict(value="products", key="#prodId")
    public void deleteProduct(Long prodId) {
        Product product = repository.findById(prodId).get();
        repository.delete(product);
    }

    @Cacheable(value="products", key="#prodId")
    public Product getProductById(Long prodId) {
        Product product = repository.findById(prodId).get();
        return product;
    }

    @Cacheable(value="products")
    public List<Product> getAllProducts() {
        return repository.findAll();
    }
}
```

## Step 9: Create a Controller Class as ProductController

```
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
```
## Step 10: Testing the Application After Implementing Redis Cache

1) Start Redis Server
2) Start your Spring Boot Application
3) Make a Rest Call using any REST client on operations .
4) When you call this operation for the first time you will see a DB query in your Spring Boot Application console.
5) If you call the same operation for the second time, you will not see the DB query in the console; this means you have successfully applied the Redis Cache.