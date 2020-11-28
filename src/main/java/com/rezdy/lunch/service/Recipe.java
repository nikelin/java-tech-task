package com.rezdy.lunch.service;

import javax.persistence.*;
import java.util.Set;

@Entity
@NamedNativeQuery(
    name = "nonExpiredRecipes",
    resultClass = Recipe.class,
    query = "select r3.title, min(i2.best_before) as bb " +
            "               from recipe r3 " +
            "                       left join recipe_ingredient ri on ri.recipe = r3.title " +
            "                       left join ingredient i2 on ri.ingredient = i2.title " +
            "               where r3.title not in (" +
            "                   select recipe" +
            "                   from recipe_ingredient r" +
            "                            left join ingredient i on i.title = r.ingredient" +
            "                   where i.use_by < :date)" +
            "               group by r3.title" +
            "               order by bb desc"
)
public class Recipe {

    @Id
    private String title;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "recipe_ingredient",
            joinColumns = @JoinColumn(name = "recipe", referencedColumnName = "title"),
            inverseJoinColumns = @JoinColumn(name = "ingredient", referencedColumnName = "title"))
    private Set<Ingredient> ingredients;

    public String getTitle() {
        return title;
    }

    public Recipe setTitle(String title) {
        this.title = title;
        return this;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public Recipe setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    @Override
    public String toString() {
        return this.title + ": " + this.ingredients.size();
    }
}
