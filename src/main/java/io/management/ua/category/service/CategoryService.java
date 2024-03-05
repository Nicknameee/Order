package io.management.ua.category.service;

import io.management.resources.models.Image;
import io.management.resources.service.ImageHostingService;
import io.management.ua.annotations.DefaultNumberValue;
import io.management.ua.annotations.DefaultStringValue;
import io.management.ua.category.dto.CreateCategoryDTO;
import io.management.ua.category.dto.UpdateCategoryDTO;
import io.management.ua.category.entity.Category;
import io.management.ua.category.mapper.CategoryMapper;
import io.management.ua.category.repository.CategoryRepository;
import io.management.ua.exceptions.NotFoundException;
import io.management.ua.products.repository.ProductRepository;
import io.management.ua.utility.api.enums.Folder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
public class CategoryService {
    @PersistenceContext(unitName = "database")
    private final EntityManager entityManager;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ImageHostingService imageHostingService;
    private final ProductRepository productRepository;

    public List<Category> getAllCategories(@DefaultNumberValue Integer page,
                                           @DefaultNumberValue(number = 100) Integer size,
                                           @DefaultStringValue(string = "parentCategoryId") String sortBy,
                                           @DefaultStringValue(string = "ASC") String direction,
                                           Boolean enabled,
                                           Long parentCategoryId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> query = criteriaBuilder.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);
        List<Predicate> predicates = new ArrayList<>();

        if (enabled != null) {
            predicates.add(criteriaBuilder.equal(root.get(Category.Fields.enabled), enabled));
        }

        if (parentCategoryId != null) {
            predicates.add(criteriaBuilder.equal(root.get(Category.Fields.parentCategoryId), parentCategoryId));
        }

        query.where(predicates.toArray(new Predicate[0]));

        switch (Sort.Direction.valueOf(direction)) {
            case DESC -> query.orderBy(criteriaBuilder.desc(root.get(sortBy)));
            case ASC -> query.orderBy(criteriaBuilder.asc(root.get(sortBy)));
        }

        return entityManager.createQuery(query).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
    }

    public Category saveCategory(@Valid CreateCategoryDTO createCategoryDTO) {
         Category category = categoryMapper.dtoToEntity(createCategoryDTO);
         category.setCategoryId(UUID.randomUUID());

         return categoryRepository.save(category);
    }

    public Category setCategoryPicture(UUID categoryId, MultipartFile picture) {
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with ID {} was not found", categoryId));

        if (category.getPictureUrl() != null) {
            imageHostingService.deleteImage(category.getPictureUrl());
            category.setPictureUrl(null);
        }

        if (picture != null) {
            Image image = imageHostingService.uploadImage(picture, Folder.CATEGORIES + category.getCategoryId());
            category.setPictureUrl(image.getSecureUrl());
        }

        return categoryRepository.save(category);
    }

    public Category updateCategory(@Valid UpdateCategoryDTO updateCategoryDTO) {
         Category category = categoryRepository.findByCategoryId(updateCategoryDTO.getCategoryId())
                 .orElseThrow(() -> new NotFoundException("Category with ID {} was not found", updateCategoryDTO.getCategoryId()));

         category.setName(updateCategoryDTO.getName());
         category.setParentCategoryId(updateCategoryDTO.getParentCategoryId());

         return categoryRepository.save(category);
    }

    @Transactional
    public Category switchCategoryState(UUID categoryId, boolean state) {
         Category category = categoryRepository.findByCategoryId(categoryId).orElseThrow(() -> new NotFoundException("Category with ID {} was not found", categoryId));
         category.setEnabled(state);

        List<Category> childCategories = categoryRepository.getCategoriesByParentCategoryId(category.getId());

        if (!childCategories.isEmpty()) {
            childCategories.forEach(childCategory -> childCategory.setEnabled(state));
            categoryRepository.saveAll(childCategories);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findByCategoryId(categoryId).orElseThrow(() -> new NotFoundException("Category with ID {} was not found", categoryId));

        List<Category> childCategories = categoryRepository.getCategoriesByParentCategoryId(category.getId());

        if (!childCategories.isEmpty()) {
            childCategories.forEach(childCategory -> childCategory.setParentCategoryId(null));
            categoryRepository.saveAll(childCategories);
        }

        productRepository.detachProductsFromCategory(category.getId());

        categoryRepository.delete(category);

        if (category.getPictureUrl() != null) {
            imageHostingService.deleteImage(category.getPictureUrl());
        }
    }

    public boolean categoryExistsById(UUID categoryId) {
        return categoryRepository.existsByCategoryId(categoryId);
    }
}