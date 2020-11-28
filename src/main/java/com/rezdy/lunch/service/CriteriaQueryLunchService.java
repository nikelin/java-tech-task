package com.rezdy.lunch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("criteria-query")
public class CriteriaQueryLunchService implements LunchService {

    private final EntityManager em;

    @Autowired
    public CriteriaQueryLunchService(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Recipe> getNonExpiredRecipesOnDate(LocalDate date) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<Recipe> recipeRoot = query.from(Recipe.class);

        Expression<String> recipeRootTitle = recipeRoot.get("title");
        Expression<Number> ingredientBestBefore = recipeRoot.joinSet("ingredients").get("bestBefore");

        Subquery<String> expiredRecipesSubquery = query.subquery(String.class);
        Root<Recipe> expiredRecipesRoot = expiredRecipesSubquery.from(Recipe.class);

        Expression<Number> minBestBefore = cb.min(ingredientBestBefore);

        TypedQuery<Tuple> q = em.createQuery(
                query.multiselect(recipeRoot, minBestBefore)
                    .where(
                        cb.not(
                            recipeRootTitle.in(
                                expiredRecipesSubquery.select(expiredRecipesRoot.get("title"))
                                    .where(
                                        cb.lessThan(
                                                expiredRecipesRoot.join("ingredients").get("useBy"),
                                                cb.literal(date)
                                        )
                                    )
                            )
                        )
                    )
                    .groupBy(recipeRootTitle)
                    .orderBy(cb.desc(minBestBefore))
        );

        return q.getResultStream().map(result ->
            result.get(0, Recipe.class)
        ).collect(Collectors.toList());
    }

}
