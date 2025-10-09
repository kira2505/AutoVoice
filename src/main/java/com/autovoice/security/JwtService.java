package com.telran.store.service.security;

import com.telran.store.entity.ShopUser;

public interface JwtService {

    String generateToken(ShopUser user);

    String extractUserName(String token);

    boolean isTokenValid(String token);
}
