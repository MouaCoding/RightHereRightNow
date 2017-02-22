package com.example.rhrn.RightHereRightNow;

/**
 * Created by Matthew Moua on 2/17/2017.
 */
public class User {

    //User's information is public to be stored in the database
    public String   FirstName,
                    LastName,
                    Email,
                    Phone,
                    Address,
                    City,
                    State;

    public String   fullName;

    //Private password to not allow storage
    private String Password;

    //Copy constructor to assign email to a user's full name
    public User(String aEmail, String fullname) {
        Email       = aEmail;
        fullName    = fullname;
    }

    public User(String aFirstName, String aLastName, String aEmail, String aPassword,
                String aPhone,String aAddress,String aCity,String aState) {
        FirstName   = aFirstName;
        LastName    = aLastName;
        Email       = aEmail;
        Password    = aPassword;
        Phone       = aPhone;
        Address     = aAddress;
        City        = aCity;
        State       = aState;
    }

    //Since phone is optional, need a constructor for one without phone
    public User(String aFirstName, String aLastName, String aEmail, String aPassword,
                String aAddress, String aCity, String aState) {
        FirstName   = aFirstName;
        LastName    = aLastName;
        fullName    = FirstName + " " + String.valueOf(LastName);
        Email       = aEmail;
        Password    = aPassword;
        Address     = aAddress;
        City        = aCity;
        State       = aState;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getUserEmail() {
        return Email;
    }

    public String getPhone() {
        return Phone;
    }

    public String getAddress() {
        return Address;
    }

    public String getCity() {
        return City;
    }

    public String getState() {
        return State;
    }
}
