package com.example.rhrn.RightHereRightNow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.rhrn.RightHereRightNow.MapsFragment.getBitmapFromURL;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Bradley Wang on 2/13/2017.
 * Edit by Matt on 3/8/201 --> Queried displayName and updated the profile page name
 * Edit by Matt 3/28/2017-4/1/2017 --> Populate profile, change profile pic via upload/capture
 */
public class ProfilePageFragment extends Fragment {
    public TextView userName,
            hash_tag,
            numberFollowers,
            numActivityPoints,
            numLikes,
            about;
    public EditText profileMain;
    public ImageView profilePicture, edit,editDisplay;
    public ImageButton changeProfile;

    //Posts
    public ImageView miniProfilePicture;
    public TextView miniUserName,
                    miniHandle,
                    body,
                    postsNumLikes,
                    postsNumComments;

    //Populating list of posts and events
    public ListView postList, eventList;
    public ArrayList<Post> postArray;
    public ArrayList<Event> eventArray;
    public NotificationFragment.PostAdapter postAdapter;
    public TrendingFragment.EventAdapter eventAdapter;


    public User temp;
    ProgressDialog pd;
    private static final int SELECT_PICTURE = 100;
    //request int for using camera
    private static final int CAPTURE_PICTURE = 200;
    public FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();

    // creating an instance of Firebase Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //creating a storage reference.
    StorageReference storageRef = storage.getReferenceFromUrl("gs://righthererightnow-72e20.appspot.com");
    Uri filePath;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.profile_page_layout, container, false);
//        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
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
                        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(imageIntent, CAPTURE_PICTURE);
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
                Intent intent = new Intent(getApplicationContext(), AboutMeActivity.class);
                startActivity(intent);
            }
        });

        profileMain = (EditText) r.findViewById(R.id.profile_name_main);
        editDisplay = (ImageView) r.findViewById(R.id.edit_display); //clicked pencil edit
        editDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDisplayName = profileMain.getText().toString().trim();
                editDisplay.clearFocus();
                InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(editDisplay.getWindowToken(), 0);
                Toast.makeText(getApplicationContext(),"Display Name Changed to " + newDisplayName, Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("User")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("DisplayName").setValue(newDisplayName);
            }
        });

        //Posts
        miniProfilePicture = (ImageView) r.findViewById(R.id.mini_profile_picture);
        miniUserName = (TextView) r.findViewById(R.id.mini_name);
        miniHandle = (TextView) r.findViewById(R.id.mini_user_handle);
        body = (TextView) r.findViewById(R.id.user_post_body);
        postsNumLikes = (TextView) r.findViewById(R.id.number_likes);
        postsNumComments = (TextView) r.findViewById(R.id.number_comments);
        postList = (ListView)r.findViewById(R.id.post_list);
        eventList = (ListView) r.findViewById(R.id.event_list);
        postArray = new ArrayList<>();
        eventArray = new ArrayList<>();

        queryFirebase();
        populatePost();
        populateEvent();
        return r;
    }

    //Handles finished activity when a user has clicked an image from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked, the image is called back
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK
                && null != data) {
            // Get the Image from data
            filePath = data.getData();
            //Upload to firebase
            uploadToFirebase();
            //Then set the photo from the chosen gallery
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getApplicationContext().getContentResolver(), filePath);
                Log.d("pathfileee", bitmap.toString());
                //Picasso.with(getContext()).load(filePath).into(profilePicture);
                profilePicture.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Else user did not pick an image
        } else if (requestCode == CAPTURE_PICTURE && resultCode == RESULT_OK) {
            // Get the Image from data
            filePath = data.getData();
            //Upload to firebase
            uploadToFirebase();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePicture.setImageBitmap(imageBitmap);
            //Else user did not pick an image
        }else {
            Toast.makeText(getApplicationContext(), "You haven't picked Image",
                    Toast.LENGTH_LONG).show();
        }
    }



    public void queryFirebase() {
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("User");
        users.orderByChild("uid").equalTo(fbuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            temp = userSnapshot.getValue(User.class);
                            userName.setText(temp.DisplayName);
                            hash_tag.setText(temp.handle);
                            numActivityPoints.setText(Integer.toString(temp.ActivityPoints));
                            numLikes.setText(Integer.toString(temp.LikesReceived));
                            about.setText(temp.AboutMe);

                            //TRY because user might not have profile picture yet
                            try {
                                //Convert the URL to aa Bitmap using function, then set the profile picture
                                if(temp.ProfilePicture != null)
                                    Picasso.with(getContext()).load(temp.ProfilePicture).into(profilePicture);
                                    //profilePicture.setImageBitmap(getBitmapFromURL(temp.ProfilePicture));
                                //else
                                    //profilePicture.setImageResource(R.mipmap.ic_launcher);
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


    //populate posts from firebase
    public void populatePost()
    {
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("Post");
        users.orderByChild("ownerID").limitToLast(2).equalTo(fbuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Post post = userSnapshot.getValue(Post.class);
                            //Most recent first
                            postArray.add(0,post);
                        }
                        postAdapter = new NotificationFragment.PostAdapter(getContext(),postArray);
                        postList.setAdapter(postAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
    }

    public void populateEvent()
    {
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("Event");
        //two events since events views are big
        users.orderByChild("ownerID").limitToLast(2).equalTo(fbuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Event event = userSnapshot.getValue(Event.class);
                            //Most recent first
                            eventArray.add(0,event);
                        }
                        eventAdapter = new TrendingFragment.EventAdapter(getContext(),eventArray);
                        eventList.setAdapter(eventAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
    }
}
