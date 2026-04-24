package com.example.productcrud.controller;

import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CategoryService;
import com.example.productcrud.service.ProductService;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, UserRepository userRepository, CategoryService categoryService) {
        this.productService = productService;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/products";
    }

    // --- FITUR SEARCH & FILTER DI SINI ---
    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryId", required = false) Long categoryId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        User currentUser = getCurrentUser(userDetails);

        // Memanggil method search yang sudah kita buat di Service tadi
        List<Product> products = productService.searchProductsByOwner(keyword, categoryId, currentUser);

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findByUser(currentUser));

        // Mengirimkan kembali keyword dan categoryId agar form di HTML tetap terisi (sticky form)
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);

        return "product/list";
    }

    @GetMapping("/products/{id}")
    public String detailProduct(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        return productService.findByIdAndOwner(id, currentUser)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Produk tidak ditemukan.");
                    return "redirect:/products";
                });
    }

    @GetMapping("/products/new")
    public String showCreateForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = getCurrentUser(userDetails);
        Product product = new Product();
        product.setCreatedAt(LocalDate.now());

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findByUser(currentUser));
        return "product/form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);

        if (product.getId() != null) {
            boolean isOwner = productService.findByIdAndOwner(product.getId(), currentUser).isPresent();
            if (!isOwner) {
                redirectAttributes.addFlashAttribute("errorMessage", "Produk tidak ditemukan.");
                return "redirect:/products";
            }
        }

        product.setOwner(currentUser);
        productService.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil disimpan!");
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        return productService.findByIdAndOwner(id, currentUser)
                .map(product -> {
                    model.addAttribute("product", product);
                    model.addAttribute("categories", categoryService.findByUser(currentUser));
                    return "product/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Produk tidak ditemukan.");
                    return "redirect:/products";
                });
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        boolean isOwner = productService.findByIdAndOwner(id, currentUser).isPresent();

        if (isOwner) {
            productService.deleteByIdAndOwner(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil dihapus!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Produk tidak ditemukan.");
        }

        return "redirect:/products";
    }
}