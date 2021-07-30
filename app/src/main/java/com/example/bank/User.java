package com.example.bank;

public class User {
    String userId, username, email, password, phone, pKey, eKey, sKey, isVerified, activeCode;

    public User() {
    }

    public User(String userId, String username, String email, String phone, String password, String pKey, String eKey, String sKey, String isVerified, String activeCode) {

        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isVerified = isVerified;
        this.pKey = pKey;
        this.eKey = eKey;
        this.sKey = sKey;
        this.activeCode = activeCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getPKey() {
        return pKey;
    }

    public void setPKey(String pKey) {
        this.pKey = pKey;
    }

    public String getEKey() {
        return eKey;
    }

    public void setEKey(String eKey) {
        this.eKey = eKey;
    }

    public String getSKey() {
        return sKey;
    }

    public void setSKey(String sKey) {
        this.sKey = sKey;
    }

    public String getActiveCode() {
        return activeCode;
    }

    public void setActiveCode(String activeCode) {
        this.activeCode = activeCode;
    }
}
