package com.example.sadulla.tasty.Common;

import com.example.sadulla.tasty.Model.User;

public class Common {

    public static User currentUser;

    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On Way";
        else
            return "Shipped";

    }
}
