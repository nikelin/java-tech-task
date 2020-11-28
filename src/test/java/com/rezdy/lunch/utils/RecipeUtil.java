package com.rezdy.lunch.utils;

import com.rezdy.lunch.service.Ingredient;
import com.rezdy.lunch.service.Recipe;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class RecipeUtil {
    public static final boolean anyExpired(List<Recipe> recipes, LocalDate now) {
        return recipes.stream().anyMatch(r ->
            r.getIngredients().stream().anyMatch(i ->
                i.getUseBy().isBefore(now)
            )
        );
    }

    public static final Comparator<Recipe> BEST_BEFORE_WISE_COMPARATOR = (o1, o2) -> {
        var ingredientComparator = Comparator.comparing(Ingredient::getBestBefore);
        var leastFreshO1 = o1.getIngredients().stream().min(ingredientComparator);
        var leastFreshO2 = o2.getIngredients().stream().min(ingredientComparator);
        if (leastFreshO1.isPresent() && leastFreshO2.isPresent()) {
            return -1 * leastFreshO1.get().getBestBefore().compareTo(leastFreshO2.get().getBestBefore());
        } else if (leastFreshO2.isPresent()) {
            return -1;
        } else if (leastFreshO1.isPresent()) {
            return 1;
        } else {
            return 0;
        }
    };
}
