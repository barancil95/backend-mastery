package com.baranproject.backendmastery.controller;

import com.baranproject.backendmastery.dto.ProductDTO;
import com.baranproject.backendmastery.service.FileStorageService;
import com.baranproject.backendmastery.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Ürün Controller'ı — Thymeleaf sayfalarına veri gönderir.
 *
 * @Controller → REST değil, VIEW dönen controller.
 *   @RestController olsaydı JSON dönerdi.
 *   @Controller ile String döndüğümüzde Thymeleaf şablon adı olarak yorumlanır.
 *
 * @RequestMapping("/products") → Bu controller'daki tüm endpoint'ler /products ile başlar.
 */
@Slf4j
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    /**
     * Ürün listesi sayfası
     * GET /products
     *
     * Model: Controller → Thymeleaf'e veri taşıyan nesne.
     * model.addAttribute("products", ...) → HTML'de th:each="${products}" ile erişilir.
     */
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/list";  // → templates/products/list.html
    }

    /**
     * Ürün ekleme formu sayfası
     * GET /products/new
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        return "products/form";  // → templates/products/form.html
    }

    /**
     * Ürün ekleme işlemi (form submit)
     * POST /products
     */
    @PostMapping
    public String createProduct(@Valid @ModelAttribute("product") ProductDTO dto,
                                BindingResult result,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            log.warn("Ürün ekleme hatası: {}", result.getAllErrors());
            return "products/form";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.uploadFile(imageFile);
            dto.setImageUrl(imageUrl);
        }

        productService.createProduct(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Ürün başarıyla eklendi!");
        return "redirect:/products";  // POST sonrası redirect (PRG pattern)
    }

    /**
     * Ürün düzenleme formu
     * GET /products/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "products/form";
    }

    /**
     * Ürün güncelleme işlemi
     * POST /products/edit/{id}
     */
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("product") ProductDTO dto,
                                BindingResult result,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "products/form";
        }

        ProductDTO existingProduct = productService.getProductById(id);

        if (imageFile != null && !imageFile.isEmpty()) {
            // Eski görseli MinIO/S3'ten sil
            if (existingProduct.getImageUrl() != null) {
                fileStorageService.deleteFile(existingProduct.getImageUrl());
            }
            // Yeni görseli yükle
            String imageUrl = fileStorageService.uploadFile(imageFile);
            dto.setImageUrl(imageUrl);
        } else {
            // Görsel değişmediyse eski görsel URL'ini koru
            dto.setImageUrl(existingProduct.getImageUrl());
        }

        productService.updateProduct(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Ürün güncellendi!");
        return "redirect:/products";
    }

    /**
     * Ürün silme
     * POST /products/delete/{id}
     */
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ProductDTO existingProduct = productService.getProductById(id);
        if (existingProduct.getImageUrl() != null) {
            fileStorageService.deleteFile(existingProduct.getImageUrl());
        }
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Ürün silindi!");
        return "redirect:/products";
    }
}
