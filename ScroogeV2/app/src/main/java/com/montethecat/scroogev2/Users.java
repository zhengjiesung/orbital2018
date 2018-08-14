package com.montethecat.scroogev2;

import com.google.firebase.database.IgnoreExtraProperties;

//Barnabas Move this
@IgnoreExtraProperties
public class Users {

    public String name, image,userID, email,layoutName1,layoutName2,layoutName3,layoutName4,layoutName5;

    public Users(){
    }

    public Users(String name, String image, String userID, String email, String layoutName1, String layoutName2, String layoutName3, String layoutName4, String layoutName5) {
        this.name = name;
        this.image = image;
        this.userID = userID;
        this.email = email;
        this.layoutName1 = layoutName1;
        this.layoutName2 = layoutName2;
        this.layoutName3 = layoutName3;
        this.layoutName4 = layoutName4;
        this.layoutName5 = layoutName5;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getLayoutName1() {
        return layoutName1;
    }

    public String getLayoutName2() {
        return layoutName2;
    }

    public String getLayoutName3() {
        return layoutName3;
    }

    public String getLayoutName4() {
        return layoutName4;
    }

    public String getLayoutName5() {
        return layoutName5;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLayoutName1(String layoutName1) {
        this.layoutName1 = layoutName1;
    }

    public void setLayoutName2(String layoutName2) {
        this.layoutName2 = layoutName2;
    }

    public void setLayoutName3(String layoutName3) {
        this.layoutName3 = layoutName3;
    }

    public void setLayoutName4(String layoutName4) {
        this.layoutName4 = layoutName4;
    }



    public void setLayoutName5(String layoutName5) {
        this.layoutName5 = layoutName5;
    }
    @Override
    public String toString() {
        return "Users{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", layoutName1='" + layoutName1 + '\'' +
                ", layoutName2='" + layoutName2 + '\'' +
                ", layoutName3='" + layoutName3 + '\'' +
                ", layoutName4='" + layoutName4 + '\'' +
                ", layoutName5='" + layoutName5 + '\'' +
                '}';
    }
}
