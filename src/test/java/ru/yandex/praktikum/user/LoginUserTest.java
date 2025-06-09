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
import static org.hamcrest.Matchers.*;

@Epic("Login user")
public class LoginUserTest {
    private static final String MESSAGE_UNAUTHORIZED = "email or password are incorrect";
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
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));
        }
    }

    @Test
    @DisplayName("Should login user with valid credentials")
    public void shouldLoginUserWithValidCredentials() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);

        int statusCode = response.extract().statusCode();
        boolean isSuccess = response.extract().path("success");

        assertThat("Access token should not be null", accessToken, notNullValue());
        assertThat("Expected status code 200", statusCode, equalTo(SC_OK));
        assertThat("Login should be successful", isSuccess, equalTo(true));
    }

    @Test
    @DisplayName("Should not login user with empty email")
    public void shouldNotLoginUserWithEmptyEmail() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setEmail(null);
        response = userClient.loginUser(user, accessToken);

        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isSuccess = response.extract().path("success");

        assertThat("Access token should not be null", accessToken, notNullValue());
        assertThat("Expected status code 401", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Expected unauthorized message", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Login should fail", isSuccess, equalTo(false));
    }

    @Test
    @DisplayName("Should not login user with empty password")
    public void shouldNotLoginUserWithEmptyPassword() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setPassword(null);
        response = userClient.loginUser(user, accessToken);

        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isSuccess = response.extract().path("success");

        assertThat("Access token should not be null", accessToken, notNullValue());
        assertThat("Expected status code 401", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Expected unauthorized message", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Login should fail", isSuccess, equalTo(false));
    }
}
