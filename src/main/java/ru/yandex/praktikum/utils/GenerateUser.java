package ru.yandex.praktikum.utils;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex.praktikum.entity.User;

public class GenerateUser {

    private static final int FIELD_LENGTH = 8;
    private static final String EMAIL_DOMAIN = "@yandex.ru";

    public static User getRandomUser() {
        String name = RandomStringUtils.randomAlphabetic(FIELD_LENGTH);
        String email = name.toLowerCase() + EMAIL_DOMAIN;
        String password = RandomStringUtils.randomAlphanumeric(FIELD_LENGTH);
        return new User(email, password, name);
    }
}
