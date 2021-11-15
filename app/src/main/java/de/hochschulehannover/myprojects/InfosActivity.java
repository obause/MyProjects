package de.hochschulehannover.myprojects;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class InfosActivity extends AppCompatActivity {

    TextView headlineTextView;
    TextView authorsTextView;
    Button faqButton;
    TextView versionTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);
        headlineTextView = findViewById(R.id.headlineTextView);
        authorsTextView = findViewById(R.id.authorsTextView);
        versionTextView = findViewById(R.id.versionTextView);
        faqButton = findViewById(R.id.faqButton);
    }
}