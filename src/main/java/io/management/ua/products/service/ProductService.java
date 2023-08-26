package io.management.ua.products.service;

import io.management.ua.annotations.DefaultNumberValue;
import io.management.ua.products.dto.ProductDTO;
import io.management.ua.products.dto.ProductFilter;
import io.management.ua.products.entity.ProductModel;
import io.management.ua.products.mapper.ProductMapper;
import io.management.ua.products.repository.ProductRepository;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final EntityManager entityManager;
    private final ProductMapper productMapper;

    public List<ProductModel> getProducts(@NotNull ProductFilter productFilter,
                                          @DefaultNumberValue(v = 1) Integer page,
                                          @DefaultNumberValue(v = 1) Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductModel> query = criteriaBuilder.createQuery(ProductModel.class);
        Root<ProductModel> root = query.from(ProductModel.class);

        List<Predicate> predicates = new ArrayList<>();

        if (!StringUtil.isNullOrEmpty(productFilter.getCategory())) {
            predicates.add(criteriaBuilder.equal(root.get("category").get("name"), productFilter.getCategory()));
        }

        if (!StringUtil.isNullOrEmpty(productFilter.getBrand())) {
            predicates.add(criteriaBuilder.equal(root.get("brand"), productFilter.getBrand()));
        }

        if (!StringUtil.isNullOrEmpty(productFilter.getName())) {
            predicates.add(criteriaBuilder.like(root.get("name"), "%" + productFilter.getName() + "%"));
        }

        if (productFilter.getPriceFrom() > 0) {
            predicates.add(criteriaBuilder.ge(root.get("cost"), productFilter.getPriceFrom()));
        }

        if (productFilter.getPriceTo() < Integer.MAX_VALUE) {
            predicates.add(criteriaBuilder.le(root.get("cost"), productFilter.getPriceTo()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
    }

    public ProductModel saveProduct(ProductDTO productDTO) {
        ProductModel productModel = productMapper.dtoToEntity(productDTO);

        return productRepository.save(productModel);
    }

    public void removeProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}
