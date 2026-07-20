package com.baranproject.backendmastery.controller;

import com.baranproject.backendmastery.dto.OrderDTO;
import com.baranproject.backendmastery.dto.OrderItemDTO;
import com.baranproject.backendmastery.service.OrderService;
import com.baranproject.backendmastery.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;

    // Sipariş listesi
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders/list";
    }

    // Sipariş detayı
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        return "orders/detail";
    }

    // Sipariş formu — ürün listesini de gönderiyoruz (hangi ürünü sipariş edecek?)
    @GetMapping("/new")
    public String showOrderForm(Model model) {
        OrderDTO orderDTO = new OrderDTO();
        // Başlangıçta 1 boş kalem ile başlat
        orderDTO.setItems(new ArrayList<>());
        orderDTO.getItems().add(new OrderItemDTO());

        model.addAttribute("order", orderDTO);
        model.addAttribute("products", productService.getAllProducts());
        return "orders/form";
    }

    // Sipariş oluşturma
    @PostMapping
    public String createOrder(@Valid @ModelAttribute("order") OrderDTO dto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllProducts());
            return "orders/form";
        }

        try {
            orderService.createOrder(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Sipariş başarıyla oluşturuldu!");
            return "redirect:/orders";
        } catch (RuntimeException e) {
            log.error("Sipariş oluşturma hatası: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("products", productService.getAllProducts());
            return "orders/form";
        }
    }
}
