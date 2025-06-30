package com.infinite.elms.service.AuthService;

import org.springframework.stereotype.Component;

@Component
public interface AuthService {
    String login(String email, String password);
}