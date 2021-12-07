package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.ArrayUtils;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import de.hochschulehannover.myprojects.firebase.FirestoreClass;
import de.hochschulehannover.myprojects.model.User;

public class ProfileActivity extends BaseActivity {

    Toolbar toolbar;
    CircleImageView userImage;
    EditText nameEditText;
    EditText emailEditText;

    // TODO: In Constants Klasse packen
    private static int READ_STORAGE_PERMISSION_CODE = 1;
    private static int PICK_IMAGE_REQUEST_CODE = 2;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.profileToolbar);

        userImage = findViewById(R.id.userImageView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);

        setupActionBar();

        new FirestoreClass().loginUser(this);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showImageChooser();
                } else {
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_STORAGE_PERMISSION_CODE);
                }
            }
        });
    }

    private void showImageChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // TODO: Veraltete Funktion ersetzen
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE);
    }

    public void setUserDetails(User user) {
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

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data.getData() != null) {
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

        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser();
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