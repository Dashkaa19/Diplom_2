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

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Create User")
public class CreateUserTest {

    private static final String MESSAGE_USER_EXISTS = "User already exists";
    private static final String MESSAGE_REQUIRED_FIELDS = "Email, password and name are required fields";

    private ValidatableResponse response;
    private UserClient userClient;
    private User user;
    private String accessToken = null;

    @Before
    public void setUp() {
        user = GenerateUser.getRandomUser();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));
            accessToken = null;
        }
    }

    @Test
    @DisplayName("Successfully create user with valid credentials")
    public void shouldCreateUserWithValidCredentials() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");

        int statusCode = response.extract().statusCode();
        boolean isCreated = response.extract().path("success");
        String accessToken = response.extract().path("accessToken");

        userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));

        assertThat("Expected status code 200", statusCode, equalTo(SC_OK));
        assertThat("User creation failed", isCreated, equalTo(true));
    }

    @Test
    @DisplayName("Fail to create user with empty email")
    public void shouldFailToCreateUserWithEmptyEmail() {
        user.setEmail(null);

        response = userClient.createUser(user);

        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreated = response.extract().path("success");

        assertThat("Expected status code 403", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Expected validation error message", message, equalTo(MESSAGE_REQUIRED_FIELDS));
        assertThat("User should not be created", isCreated, equalTo(false));
    }

    @Test
    @DisplayName("Fail to create user with empty password")
    public void shouldFailToCreateUserWithEmptyPassword() {
        user.setPassword(null);

        response = userClient.createUser(user);

        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreated = response.extract().path("success");

        assertThat("Expected status code 403", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Expected validation error message", message, equalTo(MESSAGE_REQUIRED_FIELDS));
        assertThat("User should not be created", isCreated, equalTo(false));
    }

    @Test
    @DisplayName("Fail to create user with empty name")
    public void shouldFailToCreateUserWithEmptyName() {
        user.setName(null);

        response = userClient.createUser(user);

        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreated = response.extract().path("success");

        assertThat("Expected status code 403", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Expected validation error message", message, equalTo(MESSAGE_REQUIRED_FIELDS));
        assertThat("User should not be created", isCreated, equalTo(false));
    }

    @Test
    @DisplayName("Fail to create user with duplicate credentials")
    public void shouldFailToCreateDuplicateUser() {
        userClient.createUser(user);
        response = userClient.createUser(user);

        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreated = response.extract().path("success");

        assertThat("Expected status code 403", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Expected duplication error message", message, equalTo(MESSAGE_USER_EXISTS));
        assertThat("User should not be created again", isCreated, equalTo(false));
    }
}
