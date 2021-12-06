package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hochschulehannover.myprojects.firebase.FirestoreClass;
import de.hochschulehannover.myprojects.model.User;

public class RegisterActivity extends BaseActivity {

    EditText registerMailEditText;
    EditText registerPasswordEditText;
    EditText nameEditText;
    Button registerButton;
    Toolbar toolbar;

    private FirebaseAuth mAuth;

    String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase Instanz initialisieren
        mAuth = FirebaseAuth.getInstance();

        registerMailEditText = findViewById(R.id.mailEditText);
        registerPasswordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);

        registerButton = findViewById(R.id.loginButton);
        toolbar = findViewById(R.id.toolbar);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trim, um versehentliche Leerzeichen vor oder nach der Eingabe zu entfernen
                String mail = registerMailEditText.getText().toString().trim();
                String password = registerPasswordEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();

                if (validateForm(name, mail, password)) {
                    showDialog("Bitte warten...");
                    register(mail, password, name);
                }
            }
        });

        setupActionBar();
    }

    private void register(String mail, String password, String name) {
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Registrierung erfolgreich");

                            FirebaseUser firebaseUser = mAuth.getCurrentUser(); //task.getResult().getUser();
                            String emailRegistered = firebaseUser.getEmail();

                            User user = new User(firebaseUser.getUid(), name, emailRegistered);

                            new FirestoreClass().registerUser(RegisterActivity.this, user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Registrierung fehlgeschlagen", task.getException());
                            Toast.makeText(RegisterActivity.this, "Leider ist die Registrierung fehlgeschlagen",
                                    Toast.LENGTH_SHORT).show();
                            hideDialog();
                        }
                    }
                });
    }

    public OnSuccessListener<? super Void> userRegistered() {
        Toast.makeText(this, "Du hast dich erfolgreich registriert", Toast.LENGTH_SHORT).show();
        hideDialog();
        //mAuth.signOut();
        //finish();
        return null;
    }

    private Boolean validateForm(String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            showErrorSnackBar("Bitte einen Namen eingeben");
            return false;
        }
        else if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Bitte eine Email-Adresse eingeben");
            return false;
        }
        else if (TextUtils.isEmpty(password) || password.length() <= 6) {
            showErrorSnackBar("Bitte ein Passwort mit mindestens 6 Zeichen eingeben");
            return false;
        } else {
            return true;
        }
    }

    // Custom ActionBar initialisieren
    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}