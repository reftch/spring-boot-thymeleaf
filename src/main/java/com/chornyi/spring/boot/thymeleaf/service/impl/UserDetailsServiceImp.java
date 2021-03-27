package com.chornyi.spring.boot.thymeleaf.service.impl;

import com.chornyi.spring.boot.thymeleaf.repository.UserRepository;
import com.chornyi.spring.boot.thymeleaf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImp implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        com.chornyi.spring.boot.thymeleaf.domain.User user = findByLogin(username);

        User.UserBuilder builder;
        if (user != null) {
            builder = org.springframework.security.core.userdetails.User.withUsername(username);
            builder.password(passwordEncoder.encode(user.getPassword()));
            builder.roles(user.getRoles());
        } else {
            throw new UsernameNotFoundException("User not found.");
        }

        return builder.build();
    }

    @Override
    public com.chornyi.spring.boot.thymeleaf.domain.User findByLogin(String username) {
        return userRepository.getUserByLogin(username).orElse(null);
    }

    @Override
    public List<com.chornyi.spring.boot.thymeleaf.domain.User> getUsers() {
        return userRepository.findAll();
    }

}
