package com.adarshverma.fyn.service;

import com.adarshverma.fyn.dto.CategoryDTO;
import com.adarshverma.fyn.entity.CategoryEntity;
import com.adarshverma.fyn.entity.ProfileEntity;
import com.adarshverma.fyn.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;


    //    main service methods
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();

        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new RuntimeException("Category with this name already exists");
        }
        CategoryEntity newCategory = toEntity(categoryDTO, profile);
        categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    public List<CategoryDTO> getCategoriesforCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> entities = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId()).orElseThrow(() -> new RuntimeException("Category not found or accessible"));
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setIcon(categoryDTO.getIcon());
        categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }


    //    helper methods
    //    converting DTO to Entity
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
        return CategoryEntity.builder().id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .type(categoryDTO.getType())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .build();
    }

    //    converting Entity to DTO
    private CategoryDTO toDTO(CategoryEntity entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .icon(entity.getIcon())
                .profileId(entity.getProfile() != null ? entity.getProfile().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
