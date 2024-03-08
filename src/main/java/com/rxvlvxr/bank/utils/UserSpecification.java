package com.rxvlvxr.bank.utils;

import com.rxvlvxr.bank.models.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
public class UserSpecification implements Specification<User> {
    private User filter;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        final Predicate[] p = {criteriaBuilder.conjunction()};

        if (filter.getBirthDate() != null)
            p[0] = criteriaBuilder.and(p[0], criteriaBuilder.greaterThan(root.get("birthDate"), filter.getBirthDate()));
        if (filter.getPhones() != null && !filter.getPhones().isEmpty())
            filter.getPhones().stream()
                    .findFirst()
                    .ifPresent(phone -> p[0] = criteriaBuilder.and(p[0], criteriaBuilder.equal(root.join("phones").get("number"), phone.getNumber())));
        if (filter.getFullName() != null && !filter.getFullName().isEmpty())
            p[0] = criteriaBuilder.and(p[0], criteriaBuilder.like(root.get("fullName"), filter.getFullName() + "%"));
        if (filter.getEmails() != null && !filter.getEmails().isEmpty()) {
            filter.getEmails().stream()
                    .findFirst()
                    .ifPresent(email -> p[0] = criteriaBuilder.and(p[0], criteriaBuilder.equal(root.join("emails").get("address"), email.getAddress())));
        }

        return p[0];
    }
}