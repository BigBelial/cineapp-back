package com.example.cineapp.usuario.service;

import com.example.cineapp.usuario.dto.RegisterDTO;
import com.example.cineapp.usuario.model.User;
import com.example.cineapp.usuario.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterDTO dto, boolean forceSuperUser) {
        User user = new User();
        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setIsSuperUser(forceSuperUser);
        user.setActive(true);
        return userRepository.save(user);
    }

    public User registerUser(RegisterDTO dto) {
        return registerUser(dto, false);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public User updateUser(Long id, RegisterDTO dto) {
        User user = getUser(id);
        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public void logicalDelete(Long id) {
        User user = getUser(id);
        user.setActive(false);
        userRepository.save(user);
    }
}

