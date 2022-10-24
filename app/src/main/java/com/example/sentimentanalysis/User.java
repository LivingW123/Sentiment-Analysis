package com.example.sentimentanalysis;

public class User {

    private String username;
    private String password;



    private String name;
    private String age;
    private String phonenumber;
    private String gender;
    private int height;
    private int weight;



    public User (String email, String password, String name, String age, String phonenumber, String gender, int heightFeet, int heightInches, int weight){
        this.password=password;
        this.username=email;
        this.name=name;
        this.age=age;
        this.phonenumber=phonenumber;
        this.gender=gender;
        this.height=heightFeet*12+heightInches;
        this.weight=weight;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setHeight(int heightFeet, int heightInches) {
        this.height = heightFeet*12+heightInches;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }










}
