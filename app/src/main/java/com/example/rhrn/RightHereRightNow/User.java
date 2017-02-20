package com.example.rhrn.RightHereRightNow;

/**
 * Created by Matthew Moua on 2/17/2017.
 */
public class User
{
    public String FirstName,LastName,Email,Phone,Address,City,State;
    public String fullName = FirstName + " " + String.valueOf(LastName);

    private String Password;

    public User(String aEmail, String fullname)
    {
        Email = aEmail;
        fullName = fullname;
    }

    public User(String aFirstName, String aLastName, String aEmail, String aPassword, String aPhone,String aAddress,String aCity,String aState)
    {
        FirstName=aFirstName;
        LastName=aLastName;
        Email=aEmail;
        Password = aPassword;
        Phone=aPhone;
        Address=aAddress;
        City=aCity;
        State=aState;
    }

    //Since phone is optional, need a constructor for one without phone
    public User(String aFirstName, String aLastName, String aEmail, String aPassword,String aAddress,String aCity,String aState)
    {
        FirstName=aFirstName;
        LastName=aLastName;
        Email=aEmail;
        Password = aPassword;
        Address=aAddress;
        City=aCity;
        State=aState;
    }
}
