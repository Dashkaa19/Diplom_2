package ru.yandex.praktikum.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private List<String> ingredients = new ArrayList<>();
}
