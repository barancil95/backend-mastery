package com.baranproject.backendmastery.controller;

import com.baranproject.backendmastery.dto.UserRegisterDTO;
import com.baranproject.backendmastery.entity.Role;
import com.baranproject.backendmastery.entity.User;
import com.baranproject.backendmastery.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserRegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDto") UserRegisterDTO userDto,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Girdilerde hata var.");
            return "register";
        }

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Bu e-posta adresi zaten kayıtlı.");
            return "register";
        }

        User user = User.builder()
                .fullName(userDto.getFullName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
        return "redirect:/login?registered";
    }
}
