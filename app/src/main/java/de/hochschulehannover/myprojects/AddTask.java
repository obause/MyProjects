package de.hochschulehannover.myprojects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;

import de.hochschulehannover.myprojects.firebase.FirestoreClass;
import de.hochschulehannover.myprojects.helper.DBHelper;
import de.hochschulehannover.myprojects.model.Project;
import de.hochschulehannover.myprojects.model.Task;
import de.hochschulehannover.myprojects.model.TaskList;
import de.hochschulehannover.myprojects.utils.Constants;

public class AddTask extends BaseActivity {

    Toolbar toolbar;
    EditText taskNameEditText;
    AutoCompleteTextView taskStatusText;
    AutoCompleteTextView taskPrioText;
    EditText taskDescrEditText;
    Button createTaskButton;
    Button deleteTaskButton;

    Integer statusIndex;

    Project projectDetails;
    String projectDocumentId;
    String userName;

    //Alt
    Spinner prioSpinner;
    Spinner statusTaskSpinner;
    Integer projectId;
    Integer taskId;
    String taskName;
    String taskPrio;
    String taskStatus;
    ImageButton deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Aufgaben des aktuellen Projekts holen
        if (getIntent().hasExtra(Constants.DOCUMENT_ID)) {
            projectDocumentId = getIntent().getStringExtra(Constants.DOCUMENT_ID);
        }
        if (getIntent().hasExtra(Constants.NAME)) {
            userName = getIntent().getStringExtra(Constants.NAME);
        }
        new FirestoreClass().getProjectDetails(this, projectDocumentId);

        toolbar = findViewById(R.id.addProjectToolbar);
        setupActionBar();

        taskNameEditText = findViewById(R.id.taskNameEditText);
        taskStatusText = findViewById(R.id.taskStatusText);
        taskPrioText = findViewById(R.id.taskPrioText);
        taskDescrEditText = findViewById(R.id.taskDescrEditText);
        createTaskButton = findViewById(R.id.createTaskButton);
        deleteTaskButton = findViewById(R.id.deleteTaskButton);

        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskNameEditText.getText().toString().length() > 0
                        && taskStatusText.getText().toString().length() > 0
                        && taskPrioText.getText().toString().length() > 0) {

                    String taskName = taskNameEditText.getText().toString();
                    String taskStatus = taskStatusText.getText().toString();
                    String taskPrio = taskPrioText.getText().toString();
                    String taskDescr = taskDescrEditText.getText().toString();

                    addTaskToList(statusIndex, taskName, taskStatus, taskPrio, taskDescr);
                } else {
                    showErrorSnackBar("Name, Status und Priorität müssen ausgefüllt werden!");
                }
            }
        });

        //Material DropDown mit Auswahlmöglichkeiten füllen
        String[] statusList = getResources().getStringArray(R.array.task_status_array);
        ArrayAdapter statusAdapter = new ArrayAdapter(this, R.layout.drop_down_item, statusList);
        taskStatusText.setAdapter(statusAdapter);
        String[] prioList = getResources().getStringArray(R.array.task_prio_array);
        ArrayAdapter prioAdapter = new ArrayAdapter(this, R.layout.drop_down_item, prioList);
        taskPrioText.setAdapter(prioAdapter);

        /* Position des ausgewählten Elements vom Status holen
         * Entspricht der Position der Liste in Firestore
         */
        taskStatusText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                statusIndex = i;
            }
        });
        /*
        taskNameEditText = findViewById(R.id.taskNameEditText);
        deleteButton = findViewById(R.id.deleteButton);

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
            deleteButton.setVisibility(View.VISIBLE);
        } else {

        }

         */
    }

    public void addTaskToList(Integer position, String taskName, String status, String priotity,
                              String description) {
        //projectDetails.taskList.remove(projectDetails.taskList.size() -1);

        ArrayList<String> taskAssignedUsersList = new ArrayList<>();
        taskAssignedUsersList.add(userName);

        Task task = new Task(taskName, userName, taskAssignedUsersList, status, priotity, description);
        ArrayList<Task> taskList = projectDetails.taskList.get(position).tasks;
        taskList.add(task);

        TaskList taskStatusList = projectDetails.taskList.get(position);
        TaskList updatedTaskList = new TaskList(taskStatusList.name,
                taskStatusList.createdBy, taskList);

        projectDetails.taskList.set(position, updatedTaskList);

        showDialog("Erstelle Aufgabe...");
        new FirestoreClass().updateTaskList(this, projectDetails);
    }

    public void updateStatusListSuccess() {
        hideDialog();
        showInfoSnackBar("Aufgabenliste erfolgreich aktualisiert!");
        showDialog("Lade Daten...");
        setResult(RESULT_OK);
        finish();
    }

    public void getProjectDetails(Project project) {
        hideDialog();
        projectDetails = project;
    }

    // Custom ActionBar initialisieren
    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp);
            actionBar.setTitle(R.string.create_task);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /*
    public void addTask(View view) {
        DBHelper dbHelper = new DBHelper(this);

        String taskName = taskNameEditText.getText().toString();
        String taskPrio = prioSpinner.getSelectedItem().toString();
        String statusSpinner = statusTaskSpinner.getSelectedItem().toString();

        Integer projectId = getIntent().getExtras().getInt("projectID");

        Log.i("Aufgabenname", taskName + projectId);

        if (taskId == -1) {
            writeTask(dbHelper, taskId,  projectId, taskName, statusSpinner, taskPrio);
            //taskItems.add(taskName);
            //TaskListActivity.readTasks(dbHelper);
            //TaskListActivity.arrayAdapter.notifyDataSetChanged();
            finish();
        }
        else {
            updateTask(dbHelper, taskId,  projectId, taskName, statusSpinner, taskPrio);
            //TaskListActivity.readTasks(dbHelper);
            finish();
        }
    }
    */

    public static void updateTask (DBHelper dbHelper, Integer taskId, Integer projectId, String name, String status, String prio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("projectId", projectId);
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

    public void deleteTask(View view) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        new android.app.AlertDialog.Builder(AddTask.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Aufgabe löschen")
                .setMessage("Möchtest du diese Aufgabe wirklich löschen?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.delete("tasks", "id = ?", new String[]{taskId.toString()});
                        //TaskListActivity.readTasks(dbHelper);
                        //arrayAdapter.notifyDataSetChanged();
                        finish();
                    }
                })
                .setNegativeButton("Nein", null)
                .show();
    }

    protected void readTask (DBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE id = " + taskId, null);

        int nameIndex = cursor.getColumnIndex("name");
        //int idIndex = cursor.getColumnIndex("id");
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
}