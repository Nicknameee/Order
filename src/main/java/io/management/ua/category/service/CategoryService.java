package io.management.ua.category.service;

import io.management.ua.category.dto.CategoryDTO;
import io.management.ua.category.entity.Category;
import io.management.ua.category.mapper.CategoryMapper;
import io.management.ua.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

   public Category saveCategory(CategoryDTO categoryDTO) {
       Category category = categoryMapper.dtoToEntity(categoryDTO);

       return categoryRepository.save(category);
   }

    public void deleteCategory(Long categoryId) {
        List<Category> childCategories = categoryRepository.getCategoriesByParentCategoryId(categoryId);

        if (!childCategories.isEmpty()) {
            childCategories.forEach(childCategory -> childCategory.setParentCategoryId(null));
            categoryRepository.saveAll(childCategories);
        }

        categoryRepository.deleteById(categoryId);
    }
}