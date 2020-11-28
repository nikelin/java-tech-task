package com.rezdy.lunch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@Service
@Profile("native-query")
public class NativeQueryLunchService implements LunchService {

    private final EntityManager em;

    @Autowired
    public NativeQueryLunchService(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Recipe> getNonExpiredRecipesOnDate(LocalDate date) {
        TypedQuery<Recipe> query = em.createNamedQuery("nonExpiredRecipes", Recipe.class);
        query.setParameter("date", date);
        List<Recipe> results = query.getResultList();
        return results;
    }
}
