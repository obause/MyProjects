package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {

    EditText registerMailEditText;
    EditText registerPasswordEditText;

    private FirebaseAuth mAuth;

    String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase Instanz initialisieren
        mAuth = FirebaseAuth.getInstance();

        registerMailEditText = findViewById(R.id.mailEditText);
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText);
    }

    public void register(View view) {
        mAuth.createUserWithEmailAndPassword(registerMailEditText.getText().toString(), registerPasswordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Registrierung erfolgreich");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Regirstrierung erfolgreich!",
                                    Toast.LENGTH_SHORT).show();
                            //TODO: Zur Projektübersicht weiterleiten
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Registrierung fehlgeschlagen", task.getException());
                            Toast.makeText(RegisterActivity.this, "Leider ist die Registrierung fehlgeschlagen",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}