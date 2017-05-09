package com.example.rhrn.RightHereRightNow.firebase_entry;

/**
 * Created by Matt on 5/9/2017.
 */

public class City {
    public String   CityName,
                    Country,
                    State,
                    Picture,
                    NumPosts,
                    NumEvents;

    public City()
    {
         CityName = Picture = NumPosts = NumEvents = Country = State =  null;
    }

    public City(String aCityName, String aState, String aCountry, String aPicture, String aNumPosts, String aNumEvents)
    {
        CityName = aCityName;
        State = aState;
        Country = aCountry;
        Picture = aPicture;
        NumPosts = aNumPosts;
        NumEvents = aNumEvents;
    }
}
