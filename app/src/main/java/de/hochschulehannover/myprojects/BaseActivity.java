package de.hochschulehannover.myprojects;

import android.app.Dialog;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity {

    private Boolean doubleBackExit = false;
    private Dialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showDialog(String text) {
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        TextView progressTextView = progressDialog.findViewById(R.id.progressTextView);
        progressTextView.setText(text);
        progressDialog.show();
    }

    protected void hideDialog() {
        progressDialog.dismiss();
    }

    protected void doubleBackExit() {
        if (this.doubleBackExit) {
            super.onBackPressed();
        } else {
            this.doubleBackExit = true;
            Toast.makeText(this, "Noch einmal klicken zum beenden",
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

    protected void showErrorSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
                //.setAction("Action", null).show();
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat
                .getColor(BaseActivity.this, R.color.snackbar_error_color));
        snackbar.show();
    }
}
