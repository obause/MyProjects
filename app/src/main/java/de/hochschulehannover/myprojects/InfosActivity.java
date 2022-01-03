package de.hochschulehannover.myprojects;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

/**
 * <h2>Activityklasse FaqActivity</h2>
 *
 *<p>
 * <b>Autor: Constantin</b>
 * </p>
 */
public class InfosActivity extends AppCompatActivity {

    TextView headlineTextView;
    TextView authorsTextView;
    TextView versionTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);
        headlineTextView = findViewById(R.id.headlineTextView);
        authorsTextView = findViewById(R.id.authorsTextView);
        versionTextView = findViewById(R.id.versionTextView);

    }
    public void goToFaq(View view) {

        Intent intent = new Intent(InfosActivity.this, FaqActivity.class);
        startActivity(intent);
    }
}