package ru.yandex.praktikum.api;

import io.qameta.allure.Step;
import ru.yandex.praktikum.entity.User;
import ru.yandex.praktikum.config.Config;
import ru.yandex.praktikum.utils.Urls;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends Config {

    @Step("Send GET request to /api/auth/user to retrieve user information")
    public ValidatableResponse getUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .log().all()
                .get(Urls.USER_PATH + "user")
                .then()
                .log().all();
    }

    @Step("Send POST request to /api/auth/register to create a new user")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .post(Urls.USER_PATH + "register")
                .then()
                .log().all();
    }

    @Step("Send POST request to /api/auth/login to log in a user")
    public ValidatableResponse loginUser(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .body(user)
                .log().all()
                .post(Urls.USER_PATH + "login")
                .then()
                .log().all();
    }

    @Step("Send DELETE request to /api/auth/user to delete the current user")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .log().all()
                .delete(Urls.USER_PATH + "user")
                .then()
                .log().all();
    }

    @Step("Send PATCH request to /api/auth/user to update user info with authorization")
    public ValidatableResponse updateUserWithAuthorization(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(user)
                .log().all()
                .patch(Urls.USER_PATH + "user")
                .then()
                .log().all();
    }

    @Step("Send PATCH request to /api/auth/user to update user info without authorization")
    public ValidatableResponse updateUserWithoutAuthorization(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .patch(Urls.USER_PATH + "user")
                .then()
                .log().all();
    }
}
