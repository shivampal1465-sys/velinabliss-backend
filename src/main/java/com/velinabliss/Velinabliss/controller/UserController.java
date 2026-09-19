
        package com.velinabliss.Velinabliss.controller;

import com.velinabliss.Velinabliss.entity.User;
import com.velinabliss.Velinabliss.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user.getName() == null ||
                user.getName().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Name is required");
        }

        if (user.getEmail() == null ||
                user.getEmail().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Email is required");
        }

        if (user.getPassword() == null ||
                user.getPassword().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Password is required");
        }

        if (user.getMobile() == null ||
                user.getMobile().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Mobile is required");
        }

        if (user.getAddress() == null ||
                user.getAddress().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Address is required");
        }

        if (userRepository
                .findByEmail(user.getEmail())
                .isPresent()) {

            return ResponseEntity
                    .badRequest()
                    .body("Email already registered");
        }

        User savedUser =
                userRepository.save(user);

        // Password response me nahi bhejna
        savedUser.setPassword(null);

        return ResponseEntity.ok(savedUser);
    }


    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody User loginUser) {

        if (loginUser.getEmail() == null ||
                loginUser.getEmail().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Email is required");
        }

        if (loginUser.getPassword() == null ||
                loginUser.getPassword().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Password is required");
        }

        User user =
                userRepository
                        .findByEmail(loginUser.getEmail())
                        .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid email or password");
        }

        if (!user.getPassword()
                .equals(loginUser.getPassword())) {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid email or password");
        }

        // Password response me nahi bhejna
        user.setPassword(null);

        return ResponseEntity.ok(user);
    }
}

