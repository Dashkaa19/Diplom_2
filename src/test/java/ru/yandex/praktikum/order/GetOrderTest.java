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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Get Order and Ingredients")
public class GetOrderTest {
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";

    private ValidatableResponse response;
    private User user;
    private Order order;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = GenerateUser.getRandomUser();
        order = new Order();
        userClient = new UserClient();
        orderClient = new OrderClient();
        populateIngredients();
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));
            accessToken = null;
        }
    }

    @Test
    @DisplayName("Get all ingredients")
    public void shouldGetAllIngredients() {
        response = orderClient.getAllIngredients();

        int statusCode = response.extract().statusCode();
        boolean isSuccess = response.extract().path("success");

        assertThat("Unexpected status code", statusCode, equalTo(SC_OK));
        assertThat("Failed to get ingredients", isSuccess, equalTo(true));
    }

    @Test
    @DisplayName("Get all orders without authorization")
    public void shouldGetAllOrdersWithoutAuthorization() {
        orderClient.createOrderWithoutAuthorization(order);
        response = orderClient.getAllOrders();

        int statusCode = response.extract().statusCode();
        boolean isSuccess = response.extract().path("success");

        assertThat("Unexpected status code", statusCode, equalTo(SC_OK));
        assertThat("Failed to get orders", isSuccess, equalTo(true));
    }

    @Test
    @DisplayName("Get orders as authorized user")
    public void shouldGetOrdersByAuthorizedUser() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");

        userClient.loginUser(user, accessToken);
        orderClient.createOrderWithAuthorization(order, accessToken);
        response = orderClient.getOrdersWithAuthorization(accessToken);

        int statusCode = response.extract().statusCode();
        boolean isSuccess = response.extract().path("success");

        assertThat("Unexpected status code", statusCode, equalTo(SC_OK));
        assertThat("Failed to get orders as authorized user", isSuccess, equalTo(true));
    }

    @Test
    @DisplayName("Get orders without authorization")
    public void shouldFailToGetOrdersWithoutAuthorization() {
        orderClient.createOrderWithoutAuthorization(order);
        response = orderClient.getOrdersWithoutAuthorization();

        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isSuccess = response.extract().path("success");

        assertThat("Unexpected status code", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Unexpected error message", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Request should not succeed", isSuccess, equalTo(false));
    }

    private void populateIngredients() {
        response = orderClient.getAllIngredients();
        List<String> ingredientIds = response.extract().path("data._id");

        List<String> ingredients = order.getIngredients();
        ingredients.add(ingredientIds.get(0));
        ingredients.add(ingredientIds.get(5));
        ingredients.add(ingredientIds.get(0));
    }
}
