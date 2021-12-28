package de.hochschulehannover.myprojects;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class FaqActivity extends AppCompatActivity {
    TextView headlineFaqTextView;
    TextView frage1;
    TextView frage2;
    TextView frage3;
    TextView frage4;
    TextView frage5;
    TextView frage6;

    TextView antwort1;
    TextView antwort2;
    TextView antwort3;
    TextView antwort4;
    TextView antwort5;
    TextView antwort6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        headlineFaqTextView = findViewById(R.id.headlineFaqTextView);
        frage1 = findViewById(R.id.frage1);
        frage2 = findViewById(R.id.frage2);
        frage3 = findViewById(R.id.frage3);
        frage4 = findViewById(R.id.frage4);
        frage5 = findViewById(R.id.frage5);
        frage6 = findViewById(R.id.frage6);
        antwort1 = findViewById(R.id.antwort1);
        antwort2 = findViewById(R.id.antwort2);
        antwort3 = findViewById(R.id.antwort3);
        antwort4 = findViewById(R.id.antwort4);
        antwort5 = findViewById(R.id.antwort5);
        antwort6 = findViewById(R.id.antwort6);
    }
}