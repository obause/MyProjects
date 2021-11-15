package de.hochschulehannover.myprojects;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class FaqActivity extends AppCompatActivity {
    TextView headlineFaqTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        headlineFaqTextView = findViewById(R.id.headlineFaqTextView);
    }
}