package de.hochschulehannover.myprojects;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

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
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToProjects() {
        Intent intent = new Intent(this, ProjectListActivity.class);
        intent.putExtra("name", "Benutzername"); //TODO: Benutzernamen aus Firebase holen
        startActivity(intent);
    }

}