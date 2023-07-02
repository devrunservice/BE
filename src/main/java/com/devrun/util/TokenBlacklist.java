package com.devrun.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TokenBlacklist {

    private static Set<String> blacklistedTokens = Collections.synchronizedSet(new HashSet<>());
    
    public static void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public static boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}