package de.hochschulehannover.myprojects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * <h2>Klasse SplashActivity</h2>
 * Eigener Splash-Screen für die App. Ist im Manifest als Launcher-Activity definiert.
 * Umsetzung mit Timer (1,5 Sek).
 * <p>Weitere Implementierung:</p>
 * <ul>
 *     <li>Status-Bar wird entsprechend der Android Version korrekt ausgeblendet.</li>
 *     <li>Es wird überprüft, ob der User bereits eingeloggt ist und zur entsprechenden Activity weitergeleitet</li>
 * </ul>
 *
 *
 * <b>Autor: Joshua</b>
 */

// TODO: Die neue Splash-Screen API benutzen
public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Firebase initialisieren
        mAuth = FirebaseAuth.getInstance();

        // Statusbar ausblenden
        hide_status_bar();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.carbonbl);

        TextView app_name = findViewById(R.id.myprojects_name);
        app_name.setTypeface(typeface);

        // Splashscreen für 1,5 Sekunden anzeigen, danach zum Startbildschirm weiterleiten bzw.
        // zur Projektliste, wenn der Nutzer bereits eingeloggt ist
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //Sobald der Timer abgelaufen ist wird die Methode ausgeführt
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Intent intent;
            if(currentUser != null){
                intent = new Intent(SplashActivity.this, ProjectListActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            }
            startActivity(intent);
            finish();
        }, 1500);
    }

    // Statusbar für den Splash-Screen ausblenden je nach Android-Version (< Android 10 und ab Android 10)
    private void hide_status_bar() {
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT < 30) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                //controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
            }
        }
    }
}