package com.chornyi.spring.boot.thymeleaf.service;

import com.chornyi.spring.boot.thymeleaf.domain.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User findByLogin(String login);

}
