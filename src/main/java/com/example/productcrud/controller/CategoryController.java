package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CategoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserRepository userRepository;

    // Menambahkan UserRepository ke dalam constructor
    public CategoryController(CategoryService categoryService, UserRepository userRepository) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    // Method bantuan untuk mengambil data User yang sedang login dari database
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
    }

    // Tampilkan Daftar Kategori HANYA milik user yang login
    @GetMapping
    public String listCategories(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        model.addAttribute("categories", categoryService.findByUser(user));
        return "category/list";
    }

    // Tampilkan Form Tambah Kategori
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/form";
    }

    // Simpan Kategori Baru dan kaitkan dengan user yang login
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(userDetails);
        categoryService.save(category, user);
        redirectAttributes.addFlashAttribute("successMessage", "Kategori berhasil disimpan!");
        return "redirect:/categories";
    }

    // Tampilkan Form Edit Kategori HANYA jika itu milik user yang login
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        categoryService.findByIdAndUser(id, user).ifPresent(cat -> model.addAttribute("category", cat));
        return "category/form";
    }

    // Hapus Kategori
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Kategori berhasil dihapus!");
        return "redirect:/categories";
    }
}