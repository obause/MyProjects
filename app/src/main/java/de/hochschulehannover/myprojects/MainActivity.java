package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
 * TODO: Google Login in eigene Klasse
 *<p>
 * <b>Autor: Constantin</b>
 * </p>
 */

public class MainActivity extends BaseActivity {

    EditText mailEditText;
    EditText passwordEditText;
    Button loginButton;
    Button registerButton;
    Toolbar toolbar;
    SignInButton googleLoginButton;
    TextView forgotPasswordTextView;

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
            new FirestoreClass().loadUserData(MainActivity.this);
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
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

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

        // Ereignisverknüpfung des Registrieren-Buttons. Weiterleitung zur RegiterActivity
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Strings werden von der IDE nicht erkannt, sind aber zur Laufzeit verfügbar
        Log.i("WEBCLIENTID", String.valueOf(R.string.default_web_client_id));
        // GoogleSignIn-Optionen definieren, um die UserID, Email-Adresse etc. von Google zu erhalten.
        // DEFAULT_SIGN_IN beinhaltet bereits die UserID, email und Profilbild werden extra abgefragt.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
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

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mailAddress = mailEditText.getText().toString();
                if (mailAddress != null && !mailAddress.isEmpty()) {
                    mAuth.sendPasswordResetEmail(mailEditText.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    showInfoSnackBar("Eine Mail zum zurücksetzen des Passworts wurde gesendet");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showErrorSnackBar("Es konnte keine Mail zum Zurücksetzen des Passworts gesendet werden");
                                }
                            });
                } else {
                    showErrorSnackBar("Bitte Emial-Adresse eingeben, um das Passwort zurück zu setzen");
                }
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
                            new FirestoreClass().loadUserData(MainActivity.this);
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
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Login mit Google erfolgreich!",
                                    Toast.LENGTH_SHORT).show();
                            //goToProjects();
                            Log.i("Google Uid:", firebaseUser.getUid());
                            Log.i("Google Email:", firebaseUser.getEmail());
                            Log.i("Google Name:", firebaseUser.getDisplayName());
                            Log.i("Google Photo", String.valueOf(firebaseUser.getPhotoUrl()));
                            String userUid =  firebaseUser.getUid();
                            String userMail =  firebaseUser.getEmail();
                            String userName =  firebaseUser.getDisplayName();
                            String userPhoto =  String.valueOf(firebaseUser.getPhotoUrl());


                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                //User user = new User(firebaseUser.getUid(), userName, userMail);
                                //new FirestoreClass().registerUser(MainActivity.this, user);
                                Log.i(TAG, "Neuer Account erstellt");
                            } else {
                                Log.i(TAG, "Bereits existierender User");
                                new FirestoreClass().loadUserData(MainActivity.this);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Login fehlgeschlagen!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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