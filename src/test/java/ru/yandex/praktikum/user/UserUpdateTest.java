package ru.yandex.praktikum.user;

import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.UserClient;
import ru.yandex.praktikum.entity.User;
import ru.yandex.praktikum.utils.GenerateUser;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Update user")
public class UserUpdateTest {
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";
    private ValidatableResponse response;
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = GenerateUser.getRandomUser();
    }

    @After
    public void clearState() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));
        }
    }

    @Test
    @DisplayName("Should update user with authorization")
    public void shouldUpdateUserWithAuthorization() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        User updatedUser = GenerateUser.getRandomUser();
        response = userClient.updateUserWithAuthorization(updatedUser, accessToken);

        int statusCode = response.extract().statusCode();
        boolean isSuccess = response.extract().path("success");

        assertThat("Expected status code 200", statusCode, equalTo(SC_OK));
        assertThat("Expected successful user update", isSuccess, equalTo(true));
    }

    @Test
    @DisplayName("Should NOT update user without authorization")
    public void shouldNotUpdateUserWithoutAuthorization() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        User updatedUser = GenerateUser.getRandomUser();
        response = userClient.updateUserWithoutAuthorization(updatedUser);

        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isSuccess = response.extract().path("success");

        assertThat("Expected status code 401", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Expected unauthorized message", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Expected update failure", isSuccess, equalTo(false));
    }
}
