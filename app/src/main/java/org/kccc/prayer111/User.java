package org.kccc.prayer111;

/**
 * Created by ezekiel on 2017. 2. 13..
 */

public class User {

    // email 가입시 필요한 내용
    String user_profile;
    String user_id;
    String name;
    String email;
    String password;

    // 카카오톡 로그인시 필요한 내용
//    int user_profile;
//    String id;
//    String name;

    public User() {}

    public String getName() {
        return this.name;
    }

    public String getUserId() {
        return this.user_id;
    }

    public String getUser_profile() {
        return this.user_profile;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    User(String user_profile, String user_id, String name, String email, String password) {
        this.user_profile = user_profile;
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.password = password;
    }


}
