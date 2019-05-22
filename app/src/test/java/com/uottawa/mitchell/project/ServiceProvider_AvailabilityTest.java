package com.uottawa.mitchell.project;
import android.widget.EditText;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServiceProvider_AvailabilityTest {

    ServiceProvider_Availability registeredAcct = new ServiceProvider_Availability();

    private EditText editAddress, editNumber, editDescription, editLicensed;

    //initialization of entries

    String address = editAddress.getText().toString().trim();
    String number = editNumber.getText().toString().trim();
    String licensed = editLicensed.getText().toString().trim().toLowerCase();
    String description = editDescription.getText().toString().trim().toLowerCase();

    //testing validates the entries for Service Provider availability
    @Test
    public void testAddress(){
        address = "123 Address avenue";
        assertNotNull(address);
    }

    @Test
    public void testNumber(){
        number = "(613)-663 9832";
        assertNotNull(number);
    }

    @Test
    public void testLicense(){
        licensed="yes";
        boolean isLicensed=false;
        if (licensed.equals("yes") || licensed.equals("no")){
            isLicensed = true;
        }
        assertTrue(isLicensed);
    }

    @Test
    public void testDescription(){
        description = "I am an expert licensed carpenter, capable of finishing roofs, flooring and cabinets. available by appointment";
        boolean descriptionValidate=true;
        if (description.length() > 20 ){
            descriptionValidate = false;
        }
        assertTrue(description, descriptionValidate);
    }



    }



