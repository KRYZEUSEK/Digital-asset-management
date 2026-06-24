package backend.service;

import backend.dto.CreateUserDTO;
import backend.dto.UpdateUserDTO;
import backend.dto.UserDTO;
import backend.model.Role;
import backend.model.StorageQuota;
import backend.model.User;
import backend.repository.StorageQuotaRepository;
import backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final long DEFAULT_QUOTA_BYTES = 1_073_741_824L;

    private final UserRepository userRepository;
    private final StorageQuotaRepository storageQuotaRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       StorageQuotaRepository storageQuotaRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.storageQuotaRepository = storageQuotaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public UserDTO getUser(Long id) {
        return mapToDto(findUser(id));
    }

    public UserDTO createUser(CreateUserDTO dto) {
        validateCreate(dto);
        userRepository.findByEmail(dto.getEmail())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email is already in use");
                });

        User user = new User();
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash()));
        user.setFirstName(dto.getFirstName().trim());
        user.setLastName(dto.getLastName().trim());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setRoles(dto.getRoles().isEmpty() ? new HashSet<>(List.of(Role.VIEWER)) : dto.getRoles());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        StorageQuota quota = new StorageQuota();
        quota.setUser(savedUser);
        quota.setMaxStorageBytes(DEFAULT_QUOTA_BYTES);
        quota.setUsedStorageBytes(0L);
        storageQuotaRepository.save(quota);

        return mapToDto(savedUser);
    }

    public UserDTO updateUser(Long id, UpdateUserDTO dto) {
        User user = findUser(id);
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName().trim());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName().trim());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            user.setRoles(dto.getRoles());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return mapToDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = findUser(id);
        userRepository.delete(user);
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
        dto.setLastLoginAt(user.getLastLoginAt());
        storageQuotaRepository.findByUserId(user.getId()).ifPresent(quota -> {
            dto.setQuotaUsedBytes(quota.getUsedStorageBytes());
            dto.setQuotaMaxBytes(quota.getMaxStorageBytes());
        });
        return dto;
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void validateCreate(CreateUserDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (dto.getPasswordHash() == null || dto.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }
}
