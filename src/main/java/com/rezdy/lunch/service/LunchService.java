package com.rezdy.lunch.service;

import java.time.LocalDate;
import java.util.List;

public interface LunchService {

    List<Recipe> getNonExpiredRecipesOnDate(LocalDate date);

}
