package com.example.rhrn.RightHereRightNow;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.android.gms.fitness.data.Goal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Bradley Wang on 2/13/2017.
 * Edit by Matt on 3/8/2018 --> Queried displayName and updated the profile page name
 */
public class ProfilePageFragment extends Fragment {
    public TextView userName,
            hash_tag,
            numberFollowers,
            numActivityPoints,
            numLikes,
            about;
    //TODO: Add an about me to the user class, I forgot to do it!
    public ImageView profilePicture, edit;
    public ImageButton changeProfile;
    public User temp;
    ProgressDialog pd;
    private static final int SELECT_PICTURE = 100;
    // creating an instance of Firebase Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //creating a storage reference.
    StorageReference storageRef = storage.getReferenceFromUrl("gs://righthererightnow-72e20.appspot.com");
    Uri filePath;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.profile_page_layout, container, false);
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        // restore any state here if necessary

        //The below code is for handling exception of querying the profile picture:
        //exception is thrown when application attempts to perform a networking operation in the main thread.
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        userName = (TextView) r.findViewById(R.id.profile_name_main);
        hash_tag = (TextView) r.findViewById(R.id.profile_userhandle);
        numberFollowers = (TextView) r.findViewById(R.id.profile_followers_value);
        numActivityPoints = (TextView) r.findViewById(R.id.profile_activitypoints_value);
        numLikes = (TextView) r.findViewById(R.id.profile_karma_value);
        about = (TextView) r.findViewById(R.id.profile_about_text);
        profilePicture = (ImageView) r.findViewById(R.id.profile_picture);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Enlarge the profile picture
            }
        });
        changeProfile = (ImageButton) r.findViewById(R.id.change_profile_picture);
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MM: I decided to use an alert dialog to display a popup which has 3 clickable buttons
                // For changing profile picture.
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());
                dlgAlert.setMessage("Capture New Image or Upload an Image");
                dlgAlert.setTitle("Profile Picture Change");

                //type doesnt matter, but order of buttons do
                //If user wants to upload from gallery
                dlgAlert.setNegativeButton("Upload", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Opens the gallery of the phone if user clicked "Upload"
                        Toast.makeText(getApplicationContext(), "Upload a Picture", Toast.LENGTH_LONG).show();
                        Intent i = new Intent();
                        i.setType("image/*");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
                    }
                });
                //if user decides to take a new picture
                dlgAlert.setNeutralButton("Capture", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Capture", Toast.LENGTH_LONG).show();
                    }
                });
                //if user cancels
                dlgAlert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

                dlgAlert.setCancelable(true);
                dlgAlert.create();
                dlgAlert.show();

            }
        });
        edit = (ImageView) r.findViewById(R.id.edit_about_me); //clicked pencil edit
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutMe.class);
                startActivity(intent);
                queryFirebase();
            }
        });
        queryFirebase();
        return r;
    }

    //Handles finished activity when a user has clicked an image from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked, the image is called back
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK
                && null != data) {
            final FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
            // Get the Image from data
            filePath = data.getData();

            //Upload to firebase
            uploadToFirebase();

            //Then set the photo from the chosen gallery
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getApplicationContext().getContentResolver(), filePath);
                Log.d("pathfileee", bitmap.toString());
                profilePicture.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Else user did not pick an image
        } else {
            Toast.makeText(getApplicationContext(), "You haven't picked Image",
                    Toast.LENGTH_LONG).show();
        }
    }



    public void queryFirebase()
    {
        final FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("User");
        users.orderByChild("Email").equalTo(fbuser.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            temp = userSnapshot.getValue(User.class);
                            userName.setText(temp.DisplayName);
                            hash_tag.setText(temp.handle);
                            numberFollowers.setText(Integer.toString(temp.followers.size()));
                            numActivityPoints.setText(Integer.toString(temp.ActivityPoints));
                            numLikes.setText(Integer.toString(temp.LikesReceived));
                            about.setText(temp.AboutMe);

                            //TRY because user might not have profile picture yet
                            try {
                                //Convert the URL to aa Bitmap using function, then set the profile picture
                                profilePicture.setImageBitmap(getBitmapFromURL(temp.ProfilePicture));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
    }

    public void uploadToFirebase()
    {
        //Log.d("FILEPATH", filePath.toString());
        final FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        //create the profile picture name using their uid + .jpg
        String childFile = String.valueOf(fbuser.getUid()) + ".jpg";

        //If the file was chosen from gallery then != null
        if(filePath != null) {
            //Create child using the above string
            StorageReference fileRef = storageRef.child(childFile);
            //Create the upload using built-in UploadTask
            UploadTask uploadTask = fileRef.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    //Set the download URL
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //Store URL under the current user
                    FirebaseDatabase.getInstance().getReference().child("User")
                            .child(fbuser.getUid()).child("ProfilePicture").setValue(downloadUrl.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    //stackoverflow function
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}
