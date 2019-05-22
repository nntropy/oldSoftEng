package com.uottawa.mitchell.project;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class ServiceTest{

    Service service = new Service("ServiceName","ServiceType","100","id" );

    @Test
    public void testServiceName(){
        String in = "ServiceName1";
        String out= service.getName();
        String expected = "ServiceName";
        assertNotEquals(in,out);
    }

    @Test
    public void testServiceType(){
        String in = "ServiceType1";
        String out= service.getType();
        String expected = "ServiceType";
        assertNotEquals(in,out);
    }

    @Test
    public void testRate(){
        String in = "110";
        String out=service.getHourlyrate();
        String expected = "100";
        assertNotEquals(in,out);
    }

    @Test
    public void testID(){
        String in = "id1";
        String out=service.getId();
        String expected = "id";
        assertNotEquals(in,out);
    }


}
