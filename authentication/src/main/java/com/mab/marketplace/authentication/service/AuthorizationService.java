package com.mab.marketplace.authentication.service;

import com.mab.marketplace.authentication.dto.RqCreateUser;
import com.mab.marketplace.authentication.dto.RsInfoUserPro;
import com.mab.marketplace.authentication.entity.UserEntity;
import com.mab.marketplace.authentication.repository.UserRepository;
import com.mab.marketplace.authentication.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис регистрации и авторизации
 */
@Service
@Data
public class AuthorizationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private boolean check = false;
    private ResponseEntity<?> errorMessage;
    private PasswordEncoder passwordEncoder;


    /**
     * Регистрация пользователя
     * @param rq  запрос на создание пользователя
     * @return  результат и новый токен
     */
    public ResponseEntity<?> registration(RqCreateUser rq){
        if (!check){
            return errorMessage;
        }

        UserEntity newUser = new UserEntity()
                .setName(rq.getName())
                .setMiddleName(rq.getMiddleName())
                .setLastName(rq.getLastName())
                .setEmail(rq.getEmail())
                .setPhone(rq.getPhone())
                .setPassword(
                        passwordEncoder.encode(rq.getPassword()) // encode -> зашифровать
                );

        userRepository.save(newUser);

        Claims claims = Jwts.claims();
        claims.put("id", newUser.getId());
        claims.put("name", newUser.getName());
        claims.put("middleName", newUser.getMiddleName());
        claims.put("lastName", newUser.getLastName());
        claims.put("email", newUser.getEmail());
        claims.put("phone", newUser.getPhone());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Вы успешно зарегистрировались! Ваш токен для подтверждения регистрации: " + jwtUtil.generateToken(claims));
    }

    /**
     * Авторизауия пользователя по номеру телефона и паролю
     * @param phone номер телефона
     * @param pass пароль
     * @return Результат входа и, в случае успеха, новый токен
     */
    public ResponseEntity<?> login(String phone, String pass){
        Optional<UserEntity> tmpUser = userRepository.findByPhone(phone);
        if (!tmpUser.isPresent()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пользователя с таким номером телефона не существует.");
        }

        if(!passwordEncoder.matches(pass, tmpUser.get().getPassword())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Неверный пароль или номер телефона.");
        }

        Claims claims = Jwts.claims();
        claims.put("id", tmpUser.get().getId());
        claims.put("name", tmpUser.get().getName());
        claims.put("middleName", tmpUser.get().getMiddleName());
        claims.put("lastName", tmpUser.get().getLastName());
        claims.put("email", tmpUser.get().getEmail());
        claims.put("phone", tmpUser.get().getPhone());

        String response = String.format("%s %s %s, добро пожаловать!\n" +
                "Ваша эл. почта: %s\n" +
                "Ваш номер теелефона: %s\n" +
                "Ваш новый токен: ", tmpUser.get().getName(), tmpUser.get().getMiddleName(), tmpUser.get().getLastName(),
                tmpUser.get().getEmail(), phone) + jwtUtil.generateToken(claims);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Метод для полчения данных о всех пользователях,
     * ПРЕДНАЗНАЧЕН ДЛЯ МОДЕРОВ
     * @return информация о пользователях
     */
    public ResponseEntity<?> getAllUsers(){
        List<UserEntity> users = userRepository.findAll();
        if (users.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ни одного пользователя в приложении еще нет :/");
        }

        List<RsInfoUserPro> infoUsers = new ArrayList<>();
        for (UserEntity user : users){
            RsInfoUserPro infoUser = new RsInfoUserPro()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setMiddleName(user.getMiddleName())
                    .setLastName(user.getLastName())
                    .setEmail(user.getEmail())
                    .setPhone(user.getPhone());
            infoUsers.add(infoUser);
        }

        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(infoUsers);
    }

    /**
     * Получение информации о пользователе по его id
     * @param id id пользователя
     * @return данные о нем
     */
    public ResponseEntity<?> getUserById(Long id){
        Optional<UserEntity> user = userRepository.findById(id);
        if (!user.isPresent()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пользователя с таким id не существует.");
        }

        RsInfoUserPro infoUser = new RsInfoUserPro()
                .setId(user.get().getId())
                .setName(user.get().getName())
                .setMiddleName(user.get().getMiddleName())
                .setEmail(user.get().getEmail())
                .setPhone(user.get().getPhone());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(infoUser);
    }
}
