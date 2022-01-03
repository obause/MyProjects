package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * <h2>Activity WelcomeActivity</h2>
 * <p>Startseite der App. Diese erbt von {@link BaseActivity}.
 * Von hier kommt der Nutzer zur Registrierung bzw. zum Login.
 * Auch ein direkter Login/Registrierung über Google ist von hier aus möglich.
 * Sollte der Nutzer bereits eingeloggt sein wir man direkt zur
 * Projektliste ({@link ProjectListActivity}) weitergeleitet</p>
 *<p>
 * <b>Autor: Joshua</b>
 * </p>
 */

public class WelcomeActivity extends BaseActivity {

    private Button registerButton;
    private Button loginButton;

    private FirebaseAuth mAuth;

    /**
     * Check, ob der Nutzer bereits eingeloggt ist direkt beim Start der Activity und
     * Weiterleitung zur Projektliste
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check, ob der Nutzer bereits eingeloggt ist
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            goToProjects();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Firebase initialisieren
        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.welcomeRegisterButton);
        loginButton = findViewById(R.id.welcomeLoginButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Weiterleitung zur Projektliste
     */
    public void goToProjects() {
        Intent intent = new Intent(this, ProjectListActivity.class);
        intent.putExtra("name", "Benutzername"); //TODO: Name aus Firebase holen
        startActivity(intent);
    }

}