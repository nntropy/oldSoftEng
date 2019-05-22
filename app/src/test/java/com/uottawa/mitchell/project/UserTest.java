package com.uottawa.mitchell.project;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class UserTest {

    User user = new User("John","Doe","JDoe","Password","user");

    @Test
    public void testPassword(){
        String in="Password1";
        String out= user.getPassword();
        String expected = "Password";
        assertNotEquals(in,out);
    }

    @Test
    public void testUsername(){
        String in="JDoe1";
        String out= user.getUserName();
        String expected = "JDoe";
        assertNotEquals(in,out);
    }

    @Test
    public void testRole(){
        String in="admin";
        String out= user.getRole();
        String expected = "user";
        assertNotEquals(in,out);
    }
}
