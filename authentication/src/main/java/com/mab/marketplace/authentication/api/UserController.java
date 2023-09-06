package com.mab.marketplace.authentication.api;

import com.mab.marketplace.authentication.annotation.CheckUser;
import com.mab.marketplace.authentication.dto.RqCreateUser;
import com.mab.marketplace.authentication.service.AuthorizationService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для любых операций с пользователем :
 * какие-то методы самим пользователям, а иные - модерам или другим высшим ролям
 */
@RestController
@Data
public class UserController {

    private final AuthorizationService authorizationService;

    /**
     * Регистрация (создзание) пользователя
     * ВСЯ ЛОГИКА ОПИСАНА В АННОТАЦИИ, ПОЭТОМУ ВОЗВРАЩАЕТСЯ NULL
     * @param rq данные пользователя
     * @return сообщение об успешной регистрации и токен для входа
     */
    @CheckUser
    @PostMapping("register")
    public ResponseEntity<?> registration(@RequestBody RqCreateUser rq){
        return null; //Вся логика регистрации прописана в Аннтотации CheckUser
    }

    /**
     * Авторизация
     * @param phone номер телефона пользователя, выступает в качестве логина
     * @param pass пароль
     * @return результат регистрации
     */
    @GetMapping("login")
    public ResponseEntity<?> login(@RequestParam String phone, @RequestParam String pass){
            return authorizationService.login(phone, pass);
    }

    /**
     * Получение данных о всех пользователях
     * ПАРОЛИ НЕ ВЫВОДЯТСЯ
     * @return все пользователи приложения и их данные
     */
    @GetMapping("get-all-users")
    public ResponseEntity<?> getAllUsers(){
        return authorizationService.getAllUsers();
    }

    /**
     * Получение информации о пользователе по его id
     * @param id id пользователя
     * @return данные пользователя
     */
    @GetMapping("get-user-by-id")
    public ResponseEntity<?> getUserById(@RequestBody Long id){
        return authorizationService.getUserById(id);
    }
}
