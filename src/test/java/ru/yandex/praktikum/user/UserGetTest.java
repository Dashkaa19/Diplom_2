package ru.yandex.praktikum.user;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import ru.yandex.praktikum.entity.User;
import ru.yandex.praktikum.api.UserClient;
import ru.yandex.praktikum.utils.GenerateUser;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Get user")
public class UserGetTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = GenerateUser.getRandomUser();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));
    }

    @Test
    @DisplayName("Get user by valid credentials")
    public void userGetByValidCredentials() {
        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.getUser(accessToken);
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");
        String email = response.extract().path("user.email");
        String name = response.extract().path("user.name");

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("User is get incorrect", isGet, equalTo(true));
        assertThat("Email not equal", email, equalTo(user.getEmail()));
        assertThat("Name not equal", name, equalTo(user.getName()));
    }
}
