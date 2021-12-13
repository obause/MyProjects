package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import de.hochschulehannover.myprojects.firebase.FirestoreClass;
import de.hochschulehannover.myprojects.model.User;
import de.hochschulehannover.myprojects.var.Constants;

public class ProfileActivity extends BaseActivity {

    Toolbar toolbar;
    CircleImageView userImage;
    EditText nameEditText;
    EditText emailEditText;
    Button updateButton;

    private FirebaseStorage storage;
    private Uri selectedImageUri = null;
    private String profileImageURL = "";
    private User userDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.profileToolbar);

        userImage = findViewById(R.id.userImageView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        updateButton = findViewById(R.id.updateButton);

        setupActionBar();

        new FirestoreClass().loadUserData(this);

        storage = FirebaseStorage.getInstance();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Constants.showImageChooser(ProfileActivity.this);
                } else {
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.READ_STORAGE_PERMISSION_CODE);
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImageUri != null) {
                    uploadUserImage();
                } else {
                    showDialog("Bitte warten");
                    updateUserProfile();
                }
            }
        });
    }

    /*
    Vom Nutzer ausgewähles Bild in Firebase Storage hochladen und URL abrufen
     */
    private void uploadUserImage() {
        showDialog("Lade Bild hoch...");
        if (selectedImageUri != null) {
            // Create a Cloud Storage reference from the app
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("USER_IMAGE" + System.currentTimeMillis() +
                    "." + Constants.getFileExtension(this, selectedImageUri));
            //Datei hochladen
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i("Firebase Image URL", taskSnapshot.getMetadata().toString());
                            //Nach erfolgreichem Upload die URL zum Bild abrufen
                            taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.i("Downloadable Image URL", uri.toString());
                                            profileImageURL = uri.toString();
                                            updateUserProfile();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("UploadImage", "Fehler beim Hochladen des Bildes\n" + e.getMessage());
                            showErrorSnackBar("Beim Hochladen des Bildes ist ein Fehler aufgetreten! " + e.getMessage());
                            hideDialog();
                        }
                    });
        }

    }

    /*
    Methode speichert die zu ändernden Daten in einer HashMap ab und ruft die Methode zum Aktualisieren auf.
     */
    private void updateUserProfile() {
        //HashMap anlegen Key:String, Value:Egal
        HashMap userHashMap = new HashMap<String, Object>();

        if (!profileImageURL.isEmpty() && profileImageURL != userDetails.image) {
            //userHashMap["image"] = profileImageURL;
            userHashMap.put(Constants.IMAGE, profileImageURL);
        }
        if (nameEditText.toString() != userDetails.name) {
            userHashMap.put(Constants.NAME, nameEditText.getText().toString());
        }

        //Neue Daten an Firestore senden
        new FirestoreClass().updateUserData(this, userHashMap);
    }

    public void updateUserProfileSuccess() {
        hideDialog();
        setResult(RESULT_OK);
        finish();
    }

    public void setUserDetails(User user) {

        userDetails = user;

        Glide
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(userImage);

        nameEditText.setText(user.name);
        emailEditText.setText(user.email);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                Glide
                        .with(this)
                        .load(Uri.parse(selectedImageUri.toString()))
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(userImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this);
            } else {
                showErrorSnackBar("Berechtigungen wurden verweigert");
            }
        }
    }

    // Custom ActionBar initialisieren
    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp);
            actionBar.setTitle("Mein Profil");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}