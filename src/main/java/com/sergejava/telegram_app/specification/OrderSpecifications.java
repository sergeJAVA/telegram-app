package com.sergejava.telegram_app.specification;

import com.sergejava.telegram_app.entity.Order;
import com.sergejava.telegram_app.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@UtilityClass
public class OrderSpecifications {

    public static Specification<Order> findOrdersByUserAndStatus(Long userTelegramId, String status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(status)) {
                predicates.add(cb.like(root.get("status"), status.toUpperCase(Locale.ROOT)));
            }
            if (userTelegramId != null) {
                Join<Order, User> userJoin = root.join("user", JoinType.INNER);
                predicates.add(cb.equal(userJoin.get("userId"), userTelegramId));
            }
          return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
