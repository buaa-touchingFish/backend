package com.touchfish.Tool;

import com.touchfish.Po.User;

public class UserContext {

    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user) {
        userThreadLocal.set(user);
    }

    public static User getUser() {
        return userThreadLocal.get();
    }

    public static void clearUser() {
        userThreadLocal.remove();
    }


}
