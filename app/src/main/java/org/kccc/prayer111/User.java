package org.kccc.prayer111;

import java.io.Serializable;

/**
 * Created by ezekiel on 2017. 2. 13..
 */

public class User implements Serializable {

    // email 가입시 필요한 내용
    private String user_profile;
    private String user_id;
    private String name;
    private String password;

    public User(String user_profile, String user_id, String name, String password) {
        this.user_profile = user_profile;
        this.user_id = user_id;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public String getUserId() {
        return this.user_id;
    }

    public String getUser_profile() {
        return this.user_profile;
    }

    public String getPassword() {
        return this.password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
