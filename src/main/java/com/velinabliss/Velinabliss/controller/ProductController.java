package com.velinabliss.Velinabliss.controller;

import com.velinabliss.Velinabliss.entity.Product;
import com.velinabliss.Velinabliss.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =========================================================
    // ADD PRODUCT
    // =========================================================

    @PostMapping
    public ResponseEntity<Product> addProduct(
            @RequestBody Product product) {

        Product savedProduct = productService.addProduct(product);

        return ResponseEntity.ok(savedProduct);
    }

    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id) {

        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return ResponseEntity.ok(
                productService.updateProduct(id, product)
        );
    }

    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                "Product deleted successfully"
        );
    }

    // =========================================================
    // IMAGE UPLOAD
    // =========================================================

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(
            @RequestParam("image") MultipartFile image) {

        try {

            // Check empty file
            if (image == null || image.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Please select an image");
            }

            // Check image type
            String contentType = image.getContentType();

            if (contentType == null ||
                    !(contentType.equals("image/jpeg")
                            || contentType.equals("image/png")
                            || contentType.equals("image/webp"))) {

                return ResponseEntity.badRequest()
                        .body("Only JPG, PNG and WEBP images are allowed");
            }

            // Create uploads/images folder
            Path uploadPath = Paths.get("uploads/images");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Original file name
            String originalFileName = image.getOriginalFilename();

            if (originalFileName == null ||
                    originalFileName.trim().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body("Invalid image file name");
            }

            // Get extension
            String extension = "";

            int dotIndex = originalFileName.lastIndexOf(".");

            if (dotIndex >= 0) {
                extension = originalFileName.substring(dotIndex);
            }

            // Generate unique file name
            String fileName =
                    UUID.randomUUID().toString()
                            + extension;

            // Final file path
            Path filePath =
                    uploadPath.resolve(fileName);

            // Save image
            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Return image URL
            String imageUrl =
                    "/images/" + fileName;

            return ResponseEntity.ok(imageUrl);

        } catch (IOException e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body("Image upload failed");
        }
    }
}