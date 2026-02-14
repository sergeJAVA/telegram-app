package com.sergejava.telegram_app.specification;

import com.sergejava.telegram_app.entity.Category;
import com.sergejava.telegram_app.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@UtilityClass
public class ProductSpecifications {

    public static Specification<Product> byCategoryName(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(categoryName)) {
                return criteriaBuilder.conjunction();
            }
            Join<Product, Category> categoryJoin = root.join("category", JoinType.INNER);
            return criteriaBuilder.like(categoryJoin.get("name"), "%" + categoryName + "%");
        };
    }

}
