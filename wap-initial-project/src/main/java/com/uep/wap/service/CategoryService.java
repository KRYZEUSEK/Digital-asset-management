package com.uep.wap.service;

import com.uep.wap.dto.CategoryDTO;
import com.uep.wap.model.Category;
import com.uep.wap.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
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
}
