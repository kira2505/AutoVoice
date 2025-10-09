package com.telran.store.service.security;

import com.telran.store.dto.LoginRequestDto;

public interface AuthenticationService {

    String login(LoginRequestDto loginRequestDto);
}
