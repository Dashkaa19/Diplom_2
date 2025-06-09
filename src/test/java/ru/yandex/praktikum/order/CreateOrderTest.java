package ru.yandex.praktikum.order;

import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.OrderClient;
import ru.yandex.praktikum.api.UserClient;
import ru.yandex.praktikum.entity.Order;
import ru.yandex.praktikum.entity.User;
import ru.yandex.praktikum.utils.GenerateUser;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Create Order")
public class CreateOrderTest {
    private static final String ERROR_MSG_MISSING_INGREDIENTS = "Ingredient ids must be provided";

    private ValidatableResponse response;
    private User user;
    private Order order;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        user = GenerateUser.getRandomUser();
        order = new Order();
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));
            accessToken = null;
        }
    }


    @Test
    @DisplayName("Create order with authorization")
    public void createOrderWithAuth() {
        addValidIngredientsToOrder();

        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");

        userClient.loginUser(user, accessToken);
        response = orderClient.createOrderWithAuthorization(order, accessToken);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        Integer orderNumber = response.extract().path("order.number");
        String orderId = response.extract().path("order._id");

        assertThat(statusCode, equalTo(SC_OK));
        assertThat(success, is(true));
        assertThat(orderNumber, notNullValue());
        assertThat(orderId, notNullValue());
    }

    @Test
    @DisplayName("Create order without authorization")
    public void createOrderWithoutAuth() {
        addValidIngredientsToOrder();

        response = orderClient.createOrderWithoutAuthorization(order);

        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        Integer orderNumber = response.extract().path("order.number");

        assertThat(statusCode, equalTo(SC_OK));
        assertThat(success, is(true));
        assertThat(orderNumber, notNullValue());
    }

    @Test
    @DisplayName("Create order without ingredients")
    public void createOrderWithoutIngredients() {
        response = orderClient.createOrderWithoutAuthorization(order);

        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean success = response.extract().path("success");

        assertThat(statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(message, equalTo(ERROR_MSG_MISSING_INGREDIENTS));
        assertThat(success, is(false));
    }

    @Test
    @DisplayName("Create order with invalid ingredient hash")
    public void createOrderWithInvalidIngredientHash() {
        response = orderClient.getAllIngredients();
        List<String> allIngredientIds = response.extract().path("data._id");

        List<String> ingredients = order.getIngredients();
        ingredients.add(allIngredientIds.get(0));
        // corrupt one ingredient id intentionally
        ingredients.add(allIngredientIds.get(5).replace("a", "l"));
        ingredients.add(allIngredientIds.get(0));

        response = orderClient.createOrderWithoutAuthorization(order);

        int statusCode = response.extract().statusCode();
        assertThat(statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }

    private void addValidIngredientsToOrder() {
        response = orderClient.getAllIngredients();
        List<String> ingredientIds = response.extract().path("data._id");

        List<String> ingredients = order.getIngredients();
        ingredients.add(ingredientIds.get(0));
        ingredients.add(ingredientIds.get(5));
        ingredients.add(ingredientIds.get(0));
    }
}
