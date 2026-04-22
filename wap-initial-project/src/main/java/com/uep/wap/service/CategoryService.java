package com.uep.wap.service;

import com.uep.wap.dto.CategoryDTO;
import com.uep.wap.dto.CreateCategoryDTO;
import com.uep.wap.dto.UpdateCategoryDTO;
import com.uep.wap.model.Category;
import com.uep.wap.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAscNameAsc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategory(Long id) {
        return mapToDto(findCategory(id));
    }

    public CategoryDTO createCategory(CreateCategoryDTO dto) {
        validate(dto.getName());
        Category category = new Category();
        applyChanges(category, dto.getName(), dto.getDescription(), dto.getColor(), dto.getDisplayOrder(), dto.getParentId());
        return mapToDto(categoryRepository.save(category));
    }

    public CategoryDTO updateCategory(Long id, UpdateCategoryDTO dto) {
        Category category = findCategory(id);
        applyChanges(category, dto.getName(), dto.getDescription(), dto.getColor(), dto.getDisplayOrder(), dto.getParentId());
        return mapToDto(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        categoryRepository.delete(findCategory(id));
    }

    private CategoryDTO mapToDto(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setColor(category.getColor());
        dto.setDisplayOrder(category.getDisplayOrder());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }
        return dto;
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    private void applyChanges(Category category, String name, String description, String color,
                              Integer displayOrder, Long parentId) {
        if (name != null) {
            validate(name);
            category.setName(name.trim());
        }
        if (description != null) {
            category.setDescription(description);
        }
        if (color != null) {
            category.setColor(color);
        }
        if (displayOrder != null) {
            category.setDisplayOrder(displayOrder);
        }
        if (parentId != null) {
            if (category.getId() != null && category.getId().equals(parentId)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
            category.setParent(findCategory(parentId));
        }
    }

    private void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
    }
}
