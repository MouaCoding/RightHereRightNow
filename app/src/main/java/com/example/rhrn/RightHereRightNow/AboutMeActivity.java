package com.example.rhrn.RightHereRightNow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Matt on 3/27/2017.
 */

public class AboutMeActivity extends AppCompatActivity{
    public ImageButton back;
    public Button done;
    public EditText content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_me);

        populateContent();

        content = (EditText) findViewById(R.id.about_me_content);
        content.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    content.append("\n");
                    return true;
                }
                return false;
            }
        });
        back = (ImageButton) findViewById(R.id.am_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        done = (Button) findViewById(R.id.done_edit);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit = content.getText().toString(); //Update this string to Firebase
                FirebaseDatabase.getInstance().getReference().child("User")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("AboutMeActivity").setValue(edit);
                finish();
            }
        });

    }
    void populateContent()
    {
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("User");
        users.orderByChild("Email").equalTo(fbuser.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User temp = userSnapshot.getValue(User.class);
                            content.setText(temp.AboutMe);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
    }

}
