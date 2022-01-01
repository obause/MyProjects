package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
 * <h2>Activity RegisterActivity</h2>
 * <p>Registrierungsseite der App. Diese erbt von {@link BaseActivity}.
 * Hier kann sich ein neuer Nutzer mit seiner Email-Adresse oder Google-Account
 * registrieren.
 * Weiterleitung zur Projektliste ({@link ProjectListActivity}) nach erfolgreicher Registrierung</p>
 *<p>
 * <b>Autor(en):</b>
 * </p>
 */

public class RegisterActivity extends BaseActivity {

    private EditText mailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswortEditText;
    private EditText nameEditText;
    private Button registerButton;
    private Toolbar toolbar;
    private SignInButton googleLoginButton;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;

    String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase Instanz initialisieren
        mAuth = FirebaseAuth.getInstance();

        mailEditText = findViewById(R.id.mailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswortEditText = findViewById(R.id.repeatPasswordEditText);
        nameEditText = findViewById(R.id.nameEditText);

        registerButton = findViewById(R.id.loginButton);
        toolbar = findViewById(R.id.toolbar);

        googleLoginButton = findViewById(R.id.googleLoginButton);

        // Ereignisverknüfung des Registrierungsbuttons
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trim, um versehentliche Leerzeichen vor oder nach der Eingabe zu entfernen
                String mail = mailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();
                String repeatPassword = repeatPasswortEditText.getText().toString().trim();

                //Formularüberprüfung und anschließende Registrierung über Firebase
                if (validateForm(name, mail, password, repeatPassword)) {
                    showDialog("Bitte warten...");
                    register(mail, password, name);
                }
            }
        });

        // ActionBar initialisieren
        setupActionBar();
        // GoogleLogin-Button initialisieren
        setupGoogleLogin();
    }

    /**
     * GoogleLogin initialisieren. Bei Klick auf den Google Login Button wird eine Activity von Google
     * für den Login gestartet. Bei erfolgreichem Login wird ein Nutzer bei Firebase angelegt und die
     * entsprechenden Nutzerdaten aus dem Google-Account im Firestore abgelegt.
     */
    private void setupGoogleLogin() {
        // default_web_client_id kann von der IDE nicht gefunden werden ist aber zur Laufzeit verfügbar!
        Log.i("WEBCLIENTID", String.valueOf(R.string.default_web_client_id));
        // GoogleSignIn-Optionen definieren, um die UserID, Email-Adresse etc. von Google zu erhalten.
        // DEFAULT_SIGN_IN beinhaltet bereits die UserID, Email und Profilbild werden extra abgefragt.
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
                showDialog("Bitte warten...");
                Intent intent = mGoogleSignInClient.getSignInIntent();
                //TODO: Veraltete Methode durch neue Umsetzung ändern (registerForActivityResult?)
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }

    /**
     * Registrierung mit Email-Adresse und Passwort. Wenn die Registrierung erfolgreich war wird ein
     * User-Objekt des neuen Nutzers angelegt und für diesen ein Document im Firestore angelegt
     *
     * @param mail Eingegebene Email-Adresse
     * @param password Eingegebenes Passwort
     * @param name Eingegebener Name
     *
     * TODO: Text aus der Exception abgreifen und User anzeigen welcher Fehler bei der Registrierung aufgetreten ist
     */
    private void register(String mail, String password, String name) {
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Registrierung erfolgreich");

                            FirebaseUser firebaseUser = mAuth.getCurrentUser(); //task.getResult().getUser();
                            String emailRegistered = firebaseUser.getEmail();

                            // User-Objekt des neuen Nutzers anlegen
                            User user = new User(firebaseUser.getUid(), name, emailRegistered);

                            // Nutzerdaten in Firestore abspeichern
                            new FirestoreClass().registerUser(RegisterActivity.this, user);
                        } else {
                            // Registrierung fehlgeschlagen, Fehlermeldung anzeigen
                            Log.w(TAG, "Registrierung fehlgeschlagen", task.getException());
                            Toast.makeText(RegisterActivity.this, "Leider ist die Registrierung fehlgeschlagen",
                                    Toast.LENGTH_SHORT).show();
                            hideDialog();
                        }
                    }
                });
    }

    /**
     * Bei erfolgreicher Registrierung dies anzeigen, Ladedialog ausblenden, Nutzerdaten mit {@link FirestoreClass} laden
     * und bei erfolgreichem Laden die Methode signInSuccess() ausführen.
     * @return
     */
    public OnSuccessListener<? super Void> userRegistered() {
        showInfoSnackBar("Dein Account wurde erfolgreich erstellt!");
        hideDialog();
        new FirestoreClass().loadUserData(RegisterActivity.this);
        return null;
    }

    /*
    Wenn der Login erfolgreich war die ProjektListActivity aufrufen
     */

    /**
     * Nach erfolgreicher Registrierung und Laden der Nutzerdaten ruft die FirestoreClass diese Methode
     * auf und übergibt das User-Objekt. Dann wird die {@link ProjectListActivity} für die Projektliste
     * aufgerufen.
     * @param user
     */
    public void signInSuccess(User user) {
        hideDialog();
        Intent intent = new Intent(RegisterActivity.this, ProjectListActivity.class);
        startActivity(intent);
    }

    /**
     * Methode, um nach erfolgreichem Google-Login die Daten aus dem Intent von Google zu erhalten
     * @param requestCode RequestCode der zuvor an die GoogleLogin Activity übergeben wurde
     * @param resultCode resultCode ob der Login erfolgreich war
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Ergebnis aus dem Intent vom GoogleSignIn abrufen und verarbeiten
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Bei erfolgreichem Login, Daten aus Firebase holen
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google-Login fehlgeschlagen", e);
            }
        }
    }

    /**
     * Bei erfolgreichem Login mit Google zur Projektliste weiterleiten
     * @param idToken
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login mit Google-Account erfolgreich
                            Log.d(TAG, "Login mit Google-Account erfolgreich");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            showInfoSnackBar("Login mit Google erfolgreich!");
                            //goToProjects();
                            // Google Nutzerdaten abrufen
                            Log.i("Google Uid:", firebaseUser.getUid());
                            Log.i("Google Email:", firebaseUser.getEmail());
                            Log.i("Google Name:", firebaseUser.getDisplayName());
                            Log.i("Google Photo", String.valueOf(firebaseUser.getPhotoUrl()));
                            String userUid =  firebaseUser.getUid();
                            String userMail =  firebaseUser.getEmail();
                            String userName =  firebaseUser.getDisplayName();
                            String userPhoto =  String.valueOf(firebaseUser.getPhotoUrl());

                            // Wenn der Nutzer neu ist, Collection in Firestore anlegen und Nutzerdaten abspeichern
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                User user = new User(firebaseUser.getUid(), userName, userMail, userPhoto);
                                new FirestoreClass().registerUser(RegisterActivity.this, user);
                                Log.i(TAG, "Neuer Account mit Google-Login erstellt");
                            } else {
                                Log.i(TAG, "Bereits existierender User");
                                showErrorSnackBar("Du hast dich bereits mit deinem Google Account registriert");
                                //TODO: Login implementieren
                                new FirestoreClass().loadUserData(RegisterActivity.this);
                            }

                        } else {
                            // Login mit Google-Account nicht erfolgreich
                            Log.w(TAG, "Login mit Google-Account nicht erfolgreich", task.getException());
                            showErrorSnackBar("Registrierung fehlgeschlagen!");
                            //hideDialog();
                        }
                    }
                });
    }

    /*
    Formular überprüfen und checken, ob alle Felder ausgefüllt sind, sowie die Mindestanforderungen
    an das Passwort überprüfen
     */

    /**
     * Formular überprüfen und checken, ob alle Felder ausgefüllt sind, sowie die Mindestanforderungen
     * an das Passwort überprüfen und beide Passwortfelder abgleichen
     * @param name
     * @param email
     * @param password
     * @param repeatPassword
     * @return
     */
    private Boolean validateForm(String name, String email, String password, String repeatPassword) {
        if (TextUtils.isEmpty(name)) {
            showErrorSnackBar("Bitte einen Namen eingeben");
            return false;
        }
        else if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Bitte eine Email-Adresse eingeben");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorSnackBar("Bitte eine korrekte Email-Adresse eingeben");
            return false;
        }
        else if (TextUtils.isEmpty(password) || password.length() < 6) {
            showErrorSnackBar("Bitte ein Passwort mit mindestens 6 Zeichen eingeben");
            return false;
        }
        else if (!password.equals(repeatPassword)) {
            showErrorSnackBar("Die eingegebenen Passwörter stimmen nicht miteinander überein!");
            return false;
        }
        else {
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