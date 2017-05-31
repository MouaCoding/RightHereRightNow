package com.example.rhrn.RightHereRightNow.firebase_entry;

import com.firebase.client.ServerValue;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

/**
 * Created by NatSand on 5/23/17.
 */

public class Shares {

    public int type;
    public String id;
    public Object timeStamp;

    Shares(int Type, String ID){ type = Type; id = ID; timeStamp = ServerValue.TIMESTAMP;}

    public static void Share(int type, String id, String user){

        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference("Shares").child(user);
        DatabaseReference ref = rootref.push();
        String key = ref.getKey().toString();
        rootref.child(key).setValue(new Shares(type, id));


    }



}

