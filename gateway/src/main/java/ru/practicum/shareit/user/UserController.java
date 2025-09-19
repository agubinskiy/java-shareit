package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.validation.CreateValidation;
import ru.practicum.shareit.user.validation.UpdateValidation;

@Controller
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Выполняется запрос пользователя с id={}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated(CreateValidation.class) @RequestBody UserRequestDto requestDto) {
        log.info("Начинается добавление пользователя {}", requestDto);
        return userClient.addUser(requestDto);
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @Validated(UpdateValidation.class) @RequestBody UserRequestDto requestDto) {
        log.info("Начинается обновление пользователя id={}", userId);
        return userClient.updateUser(userId, requestDto);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Начинается удаление пользователя id={}", userId);
        return userClient.deleteUser(userId);
    }
}
