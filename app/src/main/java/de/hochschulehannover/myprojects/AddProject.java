package de.hochschulehannover.myprojects;

//import de.hochschulehannover.myprojects.ProjectListActivity.projectItems;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddProject extends AppCompatActivity {

    EditText projectNameEditText;
    Spinner statusSpinner;

    TextView startDateEditText;
    TextView endDateEditText;
    DatePickerDialog picker;

    Button pickColorButton;

    public void addProject(View view) {
        DBHelper dbHelper = new DBHelper(this);

        String projectName = projectNameEditText.getText().toString();
        String projectStatus = statusSpinner.getSelectedItem().toString();
        String projectStartDate = startDateEditText.getText().toString();
        String projectEndDate = endDateEditText.getText().toString();

        writeProject(dbHelper, projectName, projectStatus, projectStartDate, projectEndDate);
        ProjectListActivity.projectItems.add(projectName);
        ProjectListActivity.readProjects(dbHelper);
        ProjectListActivity.arrayAdapter.notifyDataSetChanged();
        finish();
    }

    public static void writeProject (DBHelper dbHelper, String name, String status, String projectStartDate, String projectEndDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("status", status);
        values.put("startDate", projectStartDate);
        values.put("endDate", projectEndDate);
        db.insert("projects", null, values);
        ProjectListActivity.readProjects(dbHelper);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        pickColorButton = findViewById(R.id.pickColorButton);

        pickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker colorPicker = new ColorPicker(AddProject.this);
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position,int color) {
                        pickColorButton.setBackgroundColor(color);
                        //view.background.colorFilter = BlendModeColorFilter(Color.parseColor("#343434"), BlendMode.SRC_ATOP)
                        //GradientDrawable bgShape = (GradientDrawable)pickColorButton.getBackground();
                        //bgShape.setColor(color);
                    }

                    @Override
                    public void onCancel(){
                        // put code
                    }
                })
                        .setTitle("Wähle eine Farbe für dein Projekt")
                        .setRoundColorButton(true)
                        .show();

            }
        });

        projectNameEditText = findViewById(R.id.projectNameEditText);

        startDateEditText = findViewById(R.id.startDateEditText);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(AddProject.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startDateEditText.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        endDateEditText = findViewById(R.id.endDateEditText);
        endDateEditText.setInputType(InputType.TYPE_NULL);
        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(AddProject.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endDateEditText.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        statusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.project_status_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        statusSpinner.setAdapter(adapter);
    }
}