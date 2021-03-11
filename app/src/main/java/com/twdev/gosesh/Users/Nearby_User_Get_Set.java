package com.twdev.gosesh.Users;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AQEEL on 10/24/2018.
 */


public class Nearby_User_Get_Set implements Serializable {

   public String fb_id,first_name,last_name,name,job_title,company,school,birthday,gender,about,location,super_like;
    ArrayList<String> imagesurl;

    String swipe;

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getSwipe() {
        return swipe;
    }

    public void setSwipe(String swipe) {
        this.swipe = swipe;
    }

    public String getSuper_like() {
        return super_like;
    }

    public void setSuper_like(String super_like) {
        this.super_like = super_like;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<String> getImagesurl() {
        return imagesurl;
    }

    public void setImagesurl(ArrayList<String> imagesurl) {
        this.imagesurl = imagesurl;
    }
}

