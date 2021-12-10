package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import de.hochschulehannover.myprojects.firebase.FirestoreClass;
import de.hochschulehannover.myprojects.model.User;

/**
 * <h2>Activity MainActivity</h2>
 * <p>Loginseite der App. Diese erbt von {@link BaseActivity}.
 * Bereits registrierte Nutzer können sich hier einloggen.
 * Weiterleitung zur Projektliste ({@link ProjectListActivity}) nach erfolgreichem Login</p>
 * TODO: Klassenname umbenennen. Passt nicht mehr so wirklich.
 *<p>
 * <b>Autor(en):</b>
 * </p>
 */

public class MainActivity extends BaseActivity {

    EditText mailEditText;
    EditText passwordEditText;
    Button loginButton;
    Button registerButton;
    Toolbar toolbar;
    SignInButton googleLoginButton;

    String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;

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

        mailEditText = findViewById(R.id.mailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.startRegisterButton);
        toolbar = findViewById(R.id.toolbar);
        googleLoginButton = findViewById(R.id.googleLoginButton);

        setupActionBar();

        // Ereignisverknüpfung des Login-Buttons
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Trim, um versehentliche Leerzeichen zu entfernen
                String mail = mailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                //Eingaben überprüfen und Llogin-Methode ausführen
                if (validateForm(mail, password)) {
                    showDialog("Bitte warten");
                    login(mail, password);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // GoogleSignIn-Optionen definieren, um die UserID, Email-Adresse etc. von Google zu erhalten.
        // DEFAULT_SIGN_IN beinhaltet bereits die UserID, email wird extra abgefragt.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //GoogleSignInClient mit zuvor angegebenen Optionen initialisieren.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Firebase initialisieren
        mAuth = FirebaseAuth.getInstance();

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = mGoogleSignInClient.getSignInIntent();
                //TODO: Veraltete Methode durch neue Umsetzung ändern (registerForActivityResult?)
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

    }

    /*
    Login bei Firebase mit Email und Passwort. Anlegen eines User-Objekt mit den Nutzerdaten aus Firestore
    mithilfe der loginUser-Methode aus der FirestoreClass
     */
    public void login(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Weiterleitung zur Projektliste, wenn der Nutzer sich eingeloggt hat
                            Log.d(TAG, "Login erfolgreich");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Login erfolgreich!",
                                    Toast.LENGTH_SHORT).show();
                            new FirestoreClass().loginUser(MainActivity.this);
                        } else {
                            // Fehlermeldung, falls der Login fehlschlägt
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Login fehlgeschlagen!",
                                    Toast.LENGTH_SHORT).show();
                            hideDialog();
                        }
                    }
                });

    }

    /*
    Wenn der Login erfolgreich war die ProjektListActivity aufrufen
     */
    public void signInSuccess(User user) {
        hideDialog();
        Intent intent = new Intent(MainActivity.this, ProjectListActivity.class);
        startActivity(intent);
    }

    /*
    Eingegebene Daten im Formular überprüfen
     */
    private Boolean validateForm(String email, String password) {
        if (TextUtils.isEmpty(email)) {
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

    /*
    Methode, um nach erfolgreichem Google-Login die Daten aus dem Intent von Google zu erhalten
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Ergebnis aus dem Intent vom GoogleSignIn abrufen und verarbeiten
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Bei erfolgreichem Login, Daten aus Firebase holen
                //TODO: Firestore implementieren und User-Objekt anlegen
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google-Login fehlgeschlagen", e);
            }
        }
    }

    /*
    Bei erfolgreichem Login mit Google zur Projektliste weiterleiten
    TODO: Neue Implementierung mit Firestore Daten und User-Klasse umsetzen
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Login mit Google erfolgreich!",
                                    Toast.LENGTH_SHORT).show();
                            goToProjects();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Login fehlgeschlagen!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goToProjects() {
        Intent intent = new Intent(MainActivity.this, ProjectListActivity.class);
        intent.putExtra("Name", mailEditText.getText());
        startActivity(intent);
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