package com.telran.store.service.security;

import com.telran.store.dto.LoginRequestDto;
import com.telran.store.entity.ShopUser;
import com.telran.store.service.ShopUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ShopUserService shopUserService;

    @Autowired
    private JwtService jwtService;

    @Override
    public String login(LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.login(), loginRequestDto.password()));

        ShopUser shopUser = shopUserService.getByEmail(loginRequestDto.login());
        return jwtService.generateToken(shopUser);
    }
}
