package ru.yandex.praktikum.api;

import io.qameta.allure.Step;
import ru.yandex.praktikum.entity.Order;
import ru.yandex.praktikum.config.Config;
import ru.yandex.praktikum.utils.Urls;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends Config {

    @Step("Send GET request to /api/ingredients to retrieve all ingredients")
    public ValidatableResponse getAllIngredients() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .get(Urls.INGREDIENTS_PATH)
                .then()
                .log().all();
    }

    @Step("Send GET request to /api/orders with authorization")
    public ValidatableResponse getOrdersWithAuthorization(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .log().all()
                .get(Urls.ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Send GET request to /api/orders without authorization")
    public ValidatableResponse getOrdersWithoutAuthorization() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .get(Urls.ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Send GET request to /api/orders/all")
    public ValidatableResponse getAllOrders() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .get(Urls.ORDER_PATH + "all")
                .then()
                .log().all();
    }

    @Step("Send POST request to /api/orders with authorization")
    public ValidatableResponse createOrderWithAuthorization(Order order, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(order)
                .log().all()
                .post(Urls.ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Send POST request to /api/orders without authorization")
    public ValidatableResponse createOrderWithoutAuthorization(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .log().all()
                .post(Urls.ORDER_PATH)
                .then()
                .log().all();
    }
}
