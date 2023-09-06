package com.mab.marketplace.authentication.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Класс ответа после создания пользователя
 */
@Data
@Accessors(chain = true)
public class RsCreateUser {

    private Long id;
    private String name;
    private String middleName;
    private String lastName;
    private String phone;
    private String email;
    private String token;
}
