package com.example.rhrn.RightHereRightNow.firebase_entry;

import com.firebase.client.ServerValue;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by NatSand on 5/23/17.
 */

public class Shares {

    public int type;
    public Object timeStamp;

    Shares(int Type){ type = Type; timeStamp = ServerValue.TIMESTAMP;}

    public static void Share(int type, String id, String user){
        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference("Shares").child(user).child(id);
        rootref.setValue(new Shares(type));
    }
}

