package com.twdev.gosesh.Inbox;

import java.io.Serializable;

/**
 * Created by AQEEL on 10/15/2018.
 */

public class Match_Get_Set implements Serializable{
    String u_id,username,picture;

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
