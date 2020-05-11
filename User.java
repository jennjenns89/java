package com.example.pictureitgrocerylist;

public class User {
    private int user_id;
    private String user_name;
    private String password;
    private String password_hint;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_hint() {
        return password_hint;
    }

    public void setPassword_hint(String password_hint) {
        this.password_hint = password_hint;
    }

    public User(int user_id, String user_name, String password, String password_hint) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.password = password;
        this.password_hint = password_hint;
    }
}
