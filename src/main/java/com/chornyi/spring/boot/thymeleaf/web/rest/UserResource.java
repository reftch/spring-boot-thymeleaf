package com.chornyi.spring.boot.thymeleaf.web.rest;

import com.chornyi.spring.boot.thymeleaf.domain.User;
import com.chornyi.spring.boot.thymeleaf.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        log.debug("REST request to get all users");
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }
}
