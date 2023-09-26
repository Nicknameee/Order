package io.management.ua.products.service;

import io.management.ua.annotations.DefaultValue;
import io.management.ua.category.entity.Category;
import io.management.ua.products.dto.OrderedProductDTO;
import io.management.ua.products.dto.ProductDTO;
import io.management.ua.products.dto.ProductFilter;
import io.management.ua.products.entity.ProductModel;
import io.management.ua.products.mapper.ProductMapper;
import io.management.ua.products.repository.ProductRepository;
import io.management.ua.utility.ResourceLoaderUtil;
import io.management.ua.utility.Scripts;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    public List<ProductModel> getProducts(@NotNull ProductFilter productFilter,
                                          @DefaultValue("1") Integer page,
                                          @DefaultValue("1") Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductModel> query = criteriaBuilder.createQuery(ProductModel.class);
        Root<ProductModel> root = query.from(ProductModel.class);

        List<Predicate> predicates = new ArrayList<>();

        if (productFilter.getCategoryId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(ProductModel.Fields.category).get(Category.Fields.id), productFilter.getCategoryId()));
        }

        if (!StringUtil.isNullOrEmpty(productFilter.getBrand())) {
            predicates.add(criteriaBuilder.equal(root.get(ProductModel.Fields.brand), productFilter.getBrand()));
        }

        if (!StringUtil.isNullOrEmpty(productFilter.getProductName())) {
            predicates.add(criteriaBuilder.like(root.get(ProductModel.Fields.productName), "%" + productFilter.getProductName() + "%"));
        }

        if (productFilter.getPriceFrom() > 0) {
            predicates.add(criteriaBuilder.ge(root.get(ProductModel.Fields.cost), productFilter.getPriceFrom()));
        }

        if (productFilter.getPriceTo() < Integer.MAX_VALUE) {
            predicates.add(criteriaBuilder.le(root.get(ProductModel.Fields.cost), productFilter.getPriceTo()));
        }

        if (productFilter.isPresent()) {
            predicates.add(criteriaBuilder.greaterThan(root.get(ProductModel.Fields.itemsLeft), 0));
        } else {
            predicates.add(criteriaBuilder.le(root.get(ProductModel.Fields.itemsLeft), 0));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
    }

    public ProductModel saveProduct(ProductDTO productDTO) {
        ProductModel productModel = productMapper.dtoToEntity(productDTO);

        return productRepository.save(productModel);
    }

    public void orderProducts(List<OrderedProductDTO> orderedProducts, Long orderId) {
        jdbcTemplate.batchUpdate(ResourceLoaderUtil.getResourceContent(Scripts.addOrderedProduct),
                orderedProducts.stream()
                        .map(orderedProductDTO -> new Object[]{orderId, orderedProductDTO.getId(), orderedProductDTO.getAmount()})
                        .toList());
    }

    public BigDecimal calculateProductsTotalCost(List<OrderedProductDTO> orderedProducts) {
        List<ProductModel> products = productRepository.findAllById(orderedProducts
                .stream()
                .map(OrderedProductDTO::getId)
                .toList());

        return products.stream()
                .map(ProductModel::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clearOrderedProducts(Long orderId) {
        jdbcTemplate.update(ResourceLoaderUtil.getResourceContent(Scripts.clearOrderedProductsForOrder), orderId);
    }

    public void removeProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}
