package com.example.productcrud.controller;

import com.example.productcrud.dto.PasswordChangeRequest;
import com.example.productcrud.dto.ProfileUpdateRequest;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showProfile(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setUsername(user.getUsername());

        model.addAttribute("profileUpdateRequest", profileUpdateRequest);
        model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
        model.addAttribute("username", user.getUsername());

        return "profile/index";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute ProfileUpdateRequest profileUpdateRequest,
                                Principal principal,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {

        String newUsername = profileUpdateRequest.getUsername();
        if (newUsername == null || newUsername.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorProfile", "Username tidak boleh kosong!");
            return "redirect:/profile";
        }

        String currentUsername = principal.getName();
        if (newUsername.equals(currentUsername)) {
            redirectAttributes.addFlashAttribute("infoProfile", "Tidak ada perubahan pada username.");
            return "redirect:/profile";
        }

        if (userRepository.findByUsername(newUsername).isPresent()) {
            redirectAttributes.addFlashAttribute("errorProfile", "Username sudah digunakan!");
            return "redirect:/profile";
        }

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setUsername(newUsername.trim());
        userRepository.save(user);

        // Invalidate session to force re-login
        request.getSession().invalidate();

        redirectAttributes.addFlashAttribute("success", "Username berhasil diubah! Silakan login kembali dengan username baru Anda.");
        return "redirect:/login";
    }

    @PostMapping("/password")
    public String changePassword(@ModelAttribute PasswordChangeRequest passwordChangeRequest,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
            redirectAttributes.addFlashAttribute("errorPassword", "Password lama tidak sesuai!");
            return "redirect:/profile";
        }

        if (passwordChangeRequest.getNewPassword() == null || passwordChangeRequest.getNewPassword().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorPassword", "Password baru tidak boleh kosong!");
            return "redirect:/profile";
        }

        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorPassword", "Password baru dan konfirmasi tidak cocok!");
            return "redirect:/profile";
        }

        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successPassword", "Password berhasil diubah!");
        return "redirect:/profile";
    }
}
