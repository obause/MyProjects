package de.hochschulehannover.myprojects;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class AddTask extends AppCompatActivity {

    EditText taskNameEditText;
    Spinner prioSpinner;
    Spinner statusTaskSpinner;


    public void addTask(View view) {
        DBHelper dbHelper = new DBHelper(this);

        String taskName = taskNameEditText.getText().toString();
        String taskPrio = prioSpinner.getSelectedItem().toString();
        String statusSpinner = statusTaskSpinner.getSelectedItem().toString();


        writeProject(dbHelper,taskName , statusSpinner, taskPrio);
        TaskListActivity.taskItems.add(taskName);
        TaskListActivity.arrayAdapter.notifyDataSetChanged();
        finish();
    }

    public static void writeProject (DBHelper dbHelper, String name, String status, String prio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("status", status);
        values.put("prio", prio);
        db.insert("tasks", null, values);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskNameEditText = findViewById(R.id.taskNameEditText);





        prioSpinner = (Spinner) findViewById(R.id.prioSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_prio_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        prioSpinner.setAdapter(adapter);

        statusTaskSpinner = (Spinner) findViewById(R.id.statusTaskSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.task_status_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        statusTaskSpinner.setAdapter(adapter2);
    }
}