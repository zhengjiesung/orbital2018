package com.montethecat.scroogev2;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserInformation {
    //instance variable
    public String name;
    public String email;
    public String layoutName1;
    public String layoutName2;
    public String layoutName3;
    public String layoutName4;
    public String layoutName5;
    public UserInformation(){}

    public UserInformation(String name, String email, String layoutName1, String layoutName2, String layoutName3, String layoutName4, String layoutName5) {
        this.name = name;
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
}
