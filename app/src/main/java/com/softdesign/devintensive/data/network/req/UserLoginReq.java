package com.softdesign.devintensive.data.network.req;

/**
 * Created by roman on 11.07.16.
 */
public class UserLoginReq {

    private String email;
    private String password;

    public UserLoginReq(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
