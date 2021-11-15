package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    EditText mailEditText;
    EditText passwordEditText;

    String TAG = "MainActivity";

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
        setContentView(R.layout.activity_main);

        // Firebase initialisieren
        mAuth = FirebaseAuth.getInstance();

        mailEditText = findViewById(R.id.mailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    public void login(View view) {
        mAuth.signInWithEmailAndPassword(mailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Weiterleitung zur Projektliste, wenn der Nutzer sich eingeloggt hat
                            Log.d(TAG, "Login erfolgreich");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToProjects();
                        } else {
                            // Fehlermeldung, falls der Login fehlschlägt
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Login fehlgeschlagen!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void goToProjects() {
        Intent intent = new Intent(MainActivity.this, ProjectListActivity.class);
        intent.putExtra("Name", mailEditText.getText());
        startActivity(intent);
    }
}