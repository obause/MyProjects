package de.hochschulehannover.myprojects;

import static de.hochschulehannover.myprojects.TaskListActivity.arrayAdapter;
import static de.hochschulehannover.myprojects.TaskListActivity.taskItems;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.PreparedStatement;
import java.util.Calendar;

public class AddTask extends AppCompatActivity {

    EditText taskNameEditText;
    Spinner prioSpinner;
    Spinner statusTaskSpinner;
    Integer projectId;
    Integer taskId;
    String taskName;
    String taskPrio;
    String taskStatus;


    public void addTask(View view) {
        DBHelper dbHelper = new DBHelper(this);

        String taskName = taskNameEditText.getText().toString();
        String taskPrio = prioSpinner.getSelectedItem().toString();
        String statusSpinner = statusTaskSpinner.getSelectedItem().toString();

        Integer projectId = getIntent().getExtras().getInt("projectID");

        Log.i("Aufgabenname", taskName + projectId);

        if (taskId == -1) {
            writeTask(dbHelper, taskId,  projectId, taskName, statusSpinner, taskPrio);
            taskItems.add(taskName);
            arrayAdapter.notifyDataSetChanged();
            finish();
        }
        else {
            /*updateTask(dbHelper, taskId,  projectId, taskName, statusSpinner, taskPrio);
            taskItems.clear();
            TaskListActivity.readTasks(dbHelper);
            arrayAdapter.notifyDataSetChanged();
            finish();*/
            //TODO: Taskliste updaten
        }

    }

    public static void updateTask (DBHelper dbHelper, Integer taskId, Integer projectId, String name, String status, String prio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("projectId", projectId);
        values.put("name", name);
        values.put("status", status);
        values.put("prio", prio);
        db.update("tasks", values, "id = ?", new String[]{taskId.toString()});

        //db.rawQuery("UPDATE tasks SET name='" + name + "', status='" + status + "', prio='" + prio + "' WHERE id=" + taskId, null);
    }

    public static void writeTask (DBHelper dbHelper, Integer taskId, Integer projectId, String name, String status, String prio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("projectId", projectId);
        values.put("name", name);
        values.put("status", status);
        values.put("prio", prio);
        db.insert("tasks", null, values);
    }

    protected void readTask (DBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE id = " + taskId, null);

        int nameIndex = cursor.getColumnIndex("name");
        int idIndex = cursor.getColumnIndex("id");
        int prioIndex = cursor.getColumnIndex("prio");
        int statusIndex = cursor.getColumnIndex("status");
        while (cursor.moveToNext()) {
            taskName = cursor.getString(nameIndex);
            taskStatus = cursor.getString(statusIndex);
            taskPrio = cursor.getString(prioIndex);
        }
    }

    public static void selectSpinnerItemByValue(Spinner spnr, String value) {
        //SimpleCursorAdapter adapter = (SimpleCursorAdapter) spnr.getAdapter();
        for (int position = 0; position < spnr.getCount(); position++) {
            if(spnr.getItemAtPosition(position).equals(value)) {
                spnr.setSelection(position);
                return;
            }
        }
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

        Intent intent = getIntent();
        taskId = intent.getIntExtra("taskID", -1);

        DBHelper dbHelper = new DBHelper(this);

        if (taskId != -1) {
            readTask(dbHelper);
            taskNameEditText.setText(taskName);
            selectSpinnerItemByValue(statusTaskSpinner, taskStatus);
            selectSpinnerItemByValue(prioSpinner, taskPrio);
        } else {

        }
    }
}