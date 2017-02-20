package org.kccc.prayer111;

/**
 * Created by ezekiel on 2017. 2. 3..
 */

public class ListData {
    int profileImage;
    String name;
    String date;
    String content;
    int prayerNumber;
    int commentNumber;

    public int getProfileImage() {
        return this.profileImage;
    }

    public String getName() {
        return this.name;
    }

    public String getContent() {
        return this.content;
    }

    public String getDate() {
        return this.date;
    }

    public int getPrayerNumber() {
        return this.prayerNumber;
    }

    public int getCommentNumber() {
        return this.commentNumber;
    }

    ListData(int profileImage, String name, String date, String content, int prayerNumber, int commentNumber) {
        this.profileImage = profileImage;
        this.name = name;
        this.date = date;
        this.content = content;
        this.prayerNumber = prayerNumber;
        this.commentNumber = commentNumber;
    }

}
