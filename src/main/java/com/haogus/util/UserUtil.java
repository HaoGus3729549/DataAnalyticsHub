package com.haogus.util;

import com.haogus.entity.User;

public class UserUtil {

    public static User currentUser = null;

    public static User getCurrentUser() {
        return currentUser;
    }

}
