package com.uep.wap.service;

import com.uep.wap.dto.CreateUserDTO;
import com.uep.wap.dto.UpdateUserDTO;
import com.uep.wap.dto.UserDTO;
import com.uep.wap.model.User;
import com.uep.wap.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public UserDTO createUser(CreateUserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPasswordHash());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setRoles(dto.getRoles());
        user.setUpdatedAt(LocalDateTime.now());
        return mapToDto(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }
        if (!dto.getRoles().isEmpty()) {
            user.setRoles(dto.getRoles());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return mapToDto(userRepository.save(user));
    }

    private UserDTO mapToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setActive(user.isActive());
        dto.setRoles(user.getRoles());
        return dto;
    }
}
