package com.rezdy.lunch.service;

import com.rezdy.lunch.utils.RecipeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql({"/setup-db.sql"})
@ActiveProfiles("native-query")
public class NativeQueryLunchServiceTest {

    @Autowired
    LunchService service;

    @Test
    public void getNonExpiredRecipesOnDateShouldReturnCorrectResults() {
        var now = LocalDate.of(2020, 11, 27);
        var results = service.getNonExpiredRecipesOnDate(now);
        var sorted = new ArrayList<>(results);
        sorted.sort(RecipeUtil.BEST_BEFORE_WISE_COMPARATOR);

        Assertions.assertEquals(3, results.size(), "wrong size");
        Assertions.assertEquals(sorted, results, "wrong order");
        Assertions.assertFalse(RecipeUtil.anyExpired(results, now), "recipe depends on an expired ingredient");
    }

}
