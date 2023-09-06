package com.mab.marketplace.authentication.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Класс для вывода информации о пользователе
 * ПАРОЛЬ НЕ ВЫВОДИТСЯ ИЗ-ЗА УГРОЗЫ БЕЗОПАСНОСТИ
 */
@Accessors(chain = true)
@Data
public class RsInfoUserPro {

    private Long id;
    private String name;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
}
