package org.kccc.prayer111;

/**
 * Created by ezekiel on 2017. 2. 13..
 */

public class User {

    // email 가입시 필요한 내용
    String user_profile;
    String userId;
    String name;
    String email;
    String password;
    String password_conform;

    // 카카오톡 로그인시 필요한 내용
//    int user_profile;
//    String id;
//    String name;


    public String getName() {
        return this.name;
    }

    public String getUserId() {
        return this.userId;
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

    public String getPassword_conform() {
        return this.password_conform;
    }

    User(String user_profile, String userId, String name, String email, String password, String password_conform) {
        this.user_profile = user_profile;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.password_conform = password_conform;
    }


}
