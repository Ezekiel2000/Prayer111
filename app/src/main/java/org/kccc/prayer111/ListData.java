package org.kccc.prayer111;

/**
 * Created by ezekiel on 2017. 2. 3..
 */

public class ListData {
    String number;
    String profileImage;
    String email;
    String name;
    String date;
    String content;
    String imageInput;
    int warn;
    int prayerNumber;
    int commentNumber;
    int chkHeart;

    public String getNumber() {
        return this.number;
    }

    public String getProfileImage() {
        return this.profileImage;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getContent() {
        return this.content;
    }

    public String getImageInput() {
        return this.imageInput;
    }

    public String getDate() {
        return this.date;
    }

    public int getWarn() {
        return this.warn;
    }

    public int getChkHeart() {
        return this.chkHeart;
    }

    public int getPrayerNumber() {
        return this.prayerNumber;
    }

    public int getCommentNumber() {
        return this.commentNumber;
    }

    ListData(String number, String profileImage, String email, String name, String date, String content, String imageInput, int warn, int prayerNumber, int commentNumber, int chkHeart) {
        this.number = number;
        this.profileImage = profileImage;
        this.email = email;
        this.name = name;
        this.date = date;
        this.content = content;
        this.imageInput = imageInput;
        this.warn = warn;
        this.prayerNumber = prayerNumber;
        this.commentNumber = commentNumber;
        this.chkHeart = chkHeart;
    }

}
