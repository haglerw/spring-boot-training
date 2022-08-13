package com.caehag.autocase.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * LoginAttemptService
 *
 * This service is going to keep track of user login attempts and lock user on reaching maximum
 * login attempts
 *
 * It makes use of Google guava library
 *
 * @author Hagler Wafula
 * @version 1.0
 */
@Service
public class LoginAttemptService {
    public static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    public static final int ATTEMPT_INCREMENT = 1;
    private final LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    /**
     * Removes user from login attempt cache when they log in successfully
     * within the specified 15 minutes period
     *
     * @param username the username to be evicted
     *
     */
    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    /**
     * Adds user to login attempt cache when they fail every login attempt
     *
     * @param username the username to be added
     *
     */
    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCache.put(username, attempts);
    }

    /**
     * Checks to confirm if a user has exceeded the maximum login attempt
     *
     * @param username the username to be evicted
     * @return         boolean of the max login attempt check
     *
     */
    public boolean hasExceededMaxLoginAttempt(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
