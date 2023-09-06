package com.mab.marketplace.authentication.annotation;

import com.mab.marketplace.authentication.dto.RqCreateUser;
import com.mab.marketplace.authentication.repository.UserRepository;
import com.mab.marketplace.authentication.service.AuthorizationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для логики аннотации проверки пользователя
 */
@Aspect
@Component
@Slf4j
@Data
public class CheckUserAspect {

    private UserRepository userRepository;
    private AuthorizationService authorizationService;

    /**
     * Основной метод класса (связующее звено)
     * @param proceedingJoinPoint
     * @param checkUser
     * @return результат регистрации
     */
    @Around(value = "@annotation(checkUser)")
    public ResponseEntity<?> checkUser(ProceedingJoinPoint proceedingJoinPoint, CheckUser checkUser){
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Object[] args = proceedingJoinPoint.getArgs();
        String[] parameterNames = methodSignature.getParameterNames();
        int nameIndex = Arrays.asList(parameterNames).indexOf(checkUser.name());

        RqCreateUser rq = (RqCreateUser) args[nameIndex];

        if (!mainCheck(rq).getStatusCode().is2xxSuccessful()){
            authorizationService.setCheck(false);
            authorizationService.setErrorMessage(mainCheck(rq));
            return authorizationService.registration(rq);
        }

        authorizationService.setCheck(true);
        return authorizationService.registration(rq);
    }

    /**
     * Большой метод для всех основных проверок
     * @param user проверяемый пользователь
     * @return результат проверки
     */
    private ResponseEntity<?> mainCheck(RqCreateUser user) {
        // Проверка на уникальность пользователя по почте и номеру
        if (userRepository.findByPhone(user.getPhone()).isPresent() || userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Человек с такими данными уже существует. Пожалуйста, проверьте свою контактную информацию!");
        }
        // Проверка на пустоту полей
        if (user.getName().isEmpty() || user.getMiddleName().isEmpty() ||
                user.getLastName().isEmpty() || user.getEmail().isEmpty() ||
                user.getPhone().isEmpty() || user.getPassword().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ни одно из полей не должно быть пустым!");
        }
        // Проверка на пробелы вы полях
        if (user.getName().contains(" ") || user.getMiddleName().contains(" ") ||
                user.getLastName().contains(" ") || user.getEmail().contains(" ") ||
                user.getPhone().contains(" ") || user.getPassword().contains(" ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ни одно из полей не должно содержать пробелы!");
        }
        // Корректность неомера телефона
        if (!numberCheck(user.getPhone())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Некорректный номер телефона!");
        }
        // Проверка на символы и цифры в пароле
        String symbols = "§±!#$%&()*+,-./0123456789:;<=>?@[]^_`{|}~\"'\\";
        if (!lettersCheck(symbols, user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Введите пароль с символами и цифрами!");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Успешно.");

    }

    /**
     * Проверка на содержание символов (в пароле)
     * @param current - строка для сравнения
     * @param check - проверяемая строка
     * @return true/false
     */
    public static boolean lettersCheck(String current, String check){
        boolean result = false;
        for (int i = 0; i < check.length(); i++){
            for (int j = 0; j < current.length(); j++){
                String a = String.valueOf(current.charAt(j));
                if (check.contains(a)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Проверка номера телефона
     *
     * @param number проверяемый номер телефона
     * @return результат проверки true/false
     */
    public static boolean numberCheck(String number){
        Pattern pattern = Pattern.compile("(\\+(0/300)\\(?)?(\\d{3}\\)?[\\- ]?)?(\\d{3}[\\- ]?)?(\\d{2}[\\- ]?)?(\\d{2})?");
        Matcher match = pattern.matcher(number);

        return match.find();
    }
}
