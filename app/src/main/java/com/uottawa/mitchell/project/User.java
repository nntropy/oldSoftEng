package com.uottawa.mitchell.project;

public class User {
    String firstName, lastName, userName, password, role;

    public User (String firstName, String lastName,
                 String userName, String password,String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.role=role;
    }

    String getFirstName() {return firstName;}
    String getLastName() {return lastName;}
    String getUserName() {return userName;}
    String getPassword() {return password;}
    String getRole (){return role;}
    public String toString (){return firstName+" "+lastName;}

}