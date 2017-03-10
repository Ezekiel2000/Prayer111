package org.kccc.prayer111;

/**
 * Created by ezekiel on 2017. 2. 7..
 */

public class ListCommentData {

    String pray_number;
    String comment_profileImage;
    String comment_name;
    String comment_date;
    String comment_content;

    public String getPray_number() {
        return this.pray_number;
    }


    public String getComment_profileImage() {
        return this.comment_profileImage;
    }

    public String getComment_name() {
        return this.comment_name;
    }

    public String getComment_date() {
        return this.comment_date;
    }

    public String getComment_content() {
        return this.comment_content;
    }

    ListCommentData(String pray_number, String comment_profileImage, String comment_name, String comment_date, String comment_content) {
        this.pray_number = pray_number;
        this.comment_profileImage = comment_profileImage;
        this.comment_name = comment_name;
        this.comment_date = comment_date;
        this.comment_content = comment_content;
    }

}
