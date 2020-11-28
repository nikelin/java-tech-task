package com.rezdy.lunch.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezdy.lunch.LunchApplication;
import com.rezdy.lunch.TestConfig;
import com.rezdy.lunch.service.Recipe;
import com.rezdy.lunch.utils.RecipeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {LunchApplication.class, TestConfig.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql({"/setup-db.sql"})
@ActiveProfiles("native-query")
public class LunchControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getRecipesShouldReturnCorrectItems() throws Exception {
        var now = LocalDate.of(2020, 11, 27);
        var response = mockMvc.perform(get("/lunch?date=" + now.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        var recordsList = mapper.readValue(response, new TypeReference<List<Recipe>>() {});
        var sortedList = new ArrayList<>(recordsList);
        sortedList.sort(RecipeUtil.BEST_BEFORE_WISE_COMPARATOR);

        Assertions.assertEquals(3, recordsList.size(), "wrong size");
        Assertions.assertEquals(sortedList, recordsList, "wrong order");
        Assertions.assertFalse(RecipeUtil.anyExpired(recordsList, now));
    }

}
