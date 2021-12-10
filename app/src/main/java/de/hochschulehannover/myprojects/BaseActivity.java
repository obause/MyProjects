package de.hochschulehannover.myprojects;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

/**
 * <h2>Oberklasse BaseActivity</h2>
 * Oberklasse für unsere Activity-Klassen. Diese erbt von {@link AppCompatActivity}.
 * Alle Activities werden von der BaseActivity erben,
 * sodass wir für alle Activities einheitliche Funktionen haben.
 * Die Klasse beinhaltet grundlegende Funktionen, die regelmäßig benötigt werden, um
 * eine Redundanz von diesen Methoden zu verhindern.
 * <br><br>
 * Implementierungen:
 * <ul>
 *     <li><b>showDialog(String text):</b> Ladedialog bei Verbindung zu Firebase anzeigen</li>
 *     <li><b>hideDialog:</b> Ladedialog wieder ausblenden</li>
 *     <li><b>doubleBackExit():</b> App erst nach zweimaligem Tippen des Zurück-Buttons beenden</li>
 *     <li><b>showErrorSnackbar(String message):</b>Snackbar mit rotem Hintergrund für Fehlermeldungen</li>
 * </ul>
 *
 *
 * <b>Autor(en):</b>
 */

public class BaseActivity extends AppCompatActivity {

    private Boolean doubleBackExit = false;
    private Dialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
    Ladedialog bei Verbindung zu Firebase anzeigen
     */
    protected void showDialog(String text) {
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        TextView progressTextView = progressDialog.findViewById(R.id.progressTextView);
        progressTextView.setText(text);
        progressDialog.show();
    }

    /*
    Ladedialog wieder ausblenden
     */
    public void hideDialog() {
        try {
            progressDialog.dismiss();
        } catch (NullPointerException npe) {
            Log.i("BaseActivity", "Kein Ladedialog vorhanden\n" + npe);
        }

    }

    /*
    App erst nach zweimaligem Tippen des Zurück-Buttons beenden
     */
    protected void doubleBackExit() {
        if (this.doubleBackExit) {
            super.onBackPressed();
        } else {
            this.doubleBackExit = true;
            Toast.makeText(this, "Noch einmal zurück tippen zum beenden",
                    Toast.LENGTH_LONG).show();
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Sobald der Timer abgelaufen ist wird die Methode ausgeführt
                doubleBackExit = false;
            }
        }, 2000);
    }

    /*
    Snackbar mit rotem Hintergrund für das Anzeigen von Fehlermeldungen
     */
    public void showErrorSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
                //.setAction("Action", null).show();
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat
                .getColor(BaseActivity.this, R.color.snackbar_error_color));
        snackbar.show();
    }

    public void showInfoSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        //.setAction("Action", null).show();
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat
                .getColor(BaseActivity.this, R.color.snackbar_info_color));
        snackbar.show();
    }
}
