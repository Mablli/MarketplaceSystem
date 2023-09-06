package com.mab.marketplace.authentication.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Класс запроса на создание пользователя
 */
@Data
@Accessors(chain = true)
public class RqCreateUser {

    private String name;
    private String middleName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
}
