package com.infinite.elms.service;

import org.springframework.stereotype.Component;

@Component
public interface AuthService {
    String login(String email, String password);
}