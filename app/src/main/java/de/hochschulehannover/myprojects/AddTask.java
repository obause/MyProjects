package de.hochschulehannover.myprojects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

/**
 * <h2>Activity AddTask</h2>
 * <p>Activity zum Hinzufügen einer neuen Aufgabe zum Projekt. Diese erbt von {@link BaseActivity}.
 *
 * Eine neu erstellte Aufgabe wird der entsprechenden Aufgabenliste(Backlog, In Arbeit, Fertig)
 * zugeordnet und das Projekt wird anschließend in Firestore aktualisiert
 *</p>
 *<p>
 * <b>Autor: Ole</b>
 * </p>
 */
public class AddTask extends BaseActivity {

    private static final String TAG = "AddTask";

    private Toolbar toolbar;
    private EditText taskNameEditText;
    private AutoCompleteTextView taskStatusText;
    private AutoCompleteTextView taskPrioText;
    private EditText taskDescrEditText;
    private Button createTaskButton;
    private Button deleteTaskButton;

    private Integer statusIndex;
    private Integer oldStatusIndex;

    private Task task;
    private Integer taskPosition;
    private Project projectDetails;
    private String projectDocumentId;
    private String userName;

    //Alt
    private Spinner prioSpinner;
    private Spinner statusTaskSpinner;
    private Integer projectId;
    private Integer taskId;
    private String taskName;
    private String taskPrio;
    private String taskStatus;
    private ImageButton deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Anhand der DocumentId aus Firestore das Projekt mit den Aufgaben holen
        // TODO: Parcelable-Umsetzung einbauen
        if (getIntent().hasExtra(Constants.DOCUMENT_ID) && getIntent().hasExtra(Constants.NAME)) {
            projectDocumentId = getIntent().getStringExtra(Constants.DOCUMENT_ID);
            Log.i(TAG, "DocumentId:" + projectDocumentId);
            userName = getIntent().getStringExtra(Constants.NAME);
            new FirestoreClass().getProjectDetails(this, projectDocumentId);
        }

        toolbar = findViewById(R.id.addProjectToolbar);
        setupActionBar();

        taskNameEditText = findViewById(R.id.taskNameEditText);
        taskStatusText = findViewById(R.id.taskStatusText);
        taskPrioText = findViewById(R.id.taskPrioText);
        taskDescrEditText = findViewById(R.id.taskDescrEditText);
        createTaskButton = findViewById(R.id.createTaskButton);
        deleteTaskButton = findViewById(R.id.deleteTaskButton);
        deleteTaskButton.setVisibility(View.INVISIBLE);

        // Bei Klick auf "Aufgabe erstellen" wird die neue Aufgabe verarbeitet und die aktualisierten
        // Projektdaten an Firestore gesendet und aktualisiert
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

                    // Neuen Task erstellen und zur TaskList des entsprechenden Status hinzufügen
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


        // Position des ausgewählten Elements vom Status holen
        // Entspricht der Position der Liste in Firestore
        taskStatusText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                statusIndex = i;
            }
        });

        // Wenn beide Extras existieren handelt es sich nicht um eine neue Aufgabe, sonder es wurde
        // eine bereits bestehende angeklickt -> "Bearbeitungsmodus"
        if (getIntent().hasExtra("task") && getIntent().hasExtra("taskPosition")) {

            // Angeklickte Aufgabe aus Intent holen
            task = getIntent().getExtras().getParcelable("task");
            taskPosition = getIntent().getIntExtra("taskPosition", 0);
            oldStatusIndex = statusIndex;

            taskNameEditText.setText(task.name);
            taskStatusText.setText(task.status, false);
            taskPrioText.setText(task.priotity, false);
            taskDescrEditText.setText(task.description);
            createTaskButton.setText("Aufgabe aktualisieren");
            deleteTaskButton.setVisibility(View.VISIBLE);

            Log.i(TAG, "Task Status:" + task.status);
            if (task.status.equals("Backlog")) {
                oldStatusIndex = Constants.BACKLOG_INDEX;
            } else if (task.status.equals("In Arbeit")) {
                oldStatusIndex = Constants.PROGRESS_INDEX;
            } else if (task.status.equals("Abgeschlossen")) {
                oldStatusIndex = Constants.DONE_INDEX;
            }
            Log.i("Positionen", "statusIndex: " + statusIndex);
            Log.i("Positionen", "oldStatusIndex: " + oldStatusIndex);
            Log.i("Positionen", "taskPosition: " + taskPosition);

            // Ereignisbehandlung des Buttons überschreiben -> Bestehende Aufgabe wird aktualisiert
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

                        updateTaskInList(statusIndex, taskName, taskStatus, taskPrio, taskDescr);
                    } else {
                        showErrorSnackBar("Name, Status und Priorität müssen ausgefüllt werden!");
                    }
                }
            });

            deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteTask(oldStatusIndex);
                }
            });
        }
        /* Alte Umsetzung
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

    /**
     * Diese Methode erstellt ein neues Task Objekt ({@link Task}). Danach wird die entsprechende
     * Aufgabenliste aus dem aktuellen Projekt geholt und die Aufgabe wird zu dieser Liste hinzugefügt.
     * Danach wird die alte Aufgabenliste im Projekt-Objekt mit der aktualisierten Aufgabenliste überschrieben.
     * Anschließend wird das aktualisierte Projekt-Objekt an die FirestoreClass übergeben welche das
     * aktualisierte Projekt in Firestore aktualisiert.
     * @param position Position der Aufgabenliste die aktualisiert werden soll
     * @param taskName Name der Aufgabe
     * @param status Status der Aufgabe
     * @param priotity Priorität der Aufgabe
     * @param description Beschreibung der Aufgabe
     */
    public void addTaskToList(Integer position, String taskName, String status, String priotity,
                              String description) {
        //projectDetails.taskList.remove(projectDetails.taskList.size() -1);

        ArrayList<String> taskAssignedUsersList = new ArrayList<>();
        taskAssignedUsersList.add(userName);

        Task task = new Task(taskName, userName, taskAssignedUsersList, status, priotity, description);
        ArrayList<Task> taskList = projectDetails.taskList.get(position).tasks;
        Log.i(TAG, "TaskList Länge(addTask):" + taskList.size());
        taskList.add(task);
        Log.i(TAG, "TaskList Länge danach(addTask):" + taskList.size());

        TaskList taskStatusList = projectDetails.taskList.get(position);
        TaskList updatedTaskList = new TaskList(taskStatusList.name,
                taskStatusList.createdBy, taskList);

        projectDetails.taskList.set(position, updatedTaskList);

        showDialog("Erstelle Aufgabe...");
        new FirestoreClass().updateTaskList(this, projectDetails);
    }

    /**
     * Diese Methode aktualisiert eine bereits bestehende Aufgabe in der entsprechenden Aufgabenliste.
     * Sollte sich der Status der Aufgabe geändert haben wird die Aufgabe aus der Aufgabenliste des alten
     * Status entfernt und in die Aufgabenliste des neuen Status hinzugefügt.
     * Anschließend wird das aktualisierte Projekt im Firestore aktualisiert.
     * @param position
     * @param taskName
     * @param status
     * @param priotity
     * @param description
     */
    public void updateTaskInList(Integer position, String taskName, String status, String priotity,
                                 String description) {
        //ArrayList<String> taskAssignedUsersList = new ArrayList<>();
        //taskAssignedUsersList.add(userName);

        Log.i(TAG, "ProjectDetails: " + projectDetails.name);
        Log.i(TAG, "ProjectDetails: " + projectDocumentId);

        // Aktualisierte Aufgabe Objekt erstellen
        Task updatedTask = task;
        updatedTask.name = taskName;
        updatedTask.status = status;
        updatedTask.priotity = priotity;
        updatedTask.description = description;

        // Liste mit Tasks des bisherigen Status holen
        ArrayList<Task> taskList = projectDetails.taskList.get(oldStatusIndex).tasks;
        Log.i(TAG, "TaskList Länge:" + taskList.size());
        Log.i(TAG, "Alte TaskListe:" + projectDetails.taskList.get(oldStatusIndex).name);
        Log.i(TAG, "Neue TaskListe:" + projectDetails.taskList.get(statusIndex).name);

        // Wenn sich der Status geändert hat, Task aus bisheriger Liste löschen und in neue einfügen
        if (oldStatusIndex != statusIndex) {
            Log.i(TAG, "Task Status hat sich geändert");
            // Task aus bisheriger Liste löschen
            taskList.remove(taskPosition.intValue());

            // Objekt des neuen Status holen
            TaskList newTaskStatusList = projectDetails.taskList.get(statusIndex);

            // Liste mit Tasks des neuen Status holen
            ArrayList<Task> otherStatusTaskList = projectDetails.taskList.get(statusIndex).tasks;
            // Task in diese Liste hinzufügen
            otherStatusTaskList.add(updatedTask);

            // Neues geändertes TaskList Objekt erstellen
            TaskList updatedTaskList = new TaskList(newTaskStatusList.name,
                    newTaskStatusList.createdBy, otherStatusTaskList);

            // Geupdatete TaskList des neuen Status mit alter ersetzen
            projectDetails.taskList.set(statusIndex, updatedTaskList);
        } else {
            // Geänderten Task in Liste ersetzen
            taskList.set(taskPosition, updatedTask);
        }

        TaskList taskStatusList = projectDetails.taskList.get(oldStatusIndex);
        TaskList updatedTaskList = new TaskList(taskStatusList.name,
                taskStatusList.createdBy, taskList);

        projectDetails.taskList.set(oldStatusIndex, updatedTaskList);

        showDialog("Aktualisiere Aufgabe...");
        new FirestoreClass().updateTaskList(this, projectDetails);
    }

    /**
     * Diese Methode löscht eine Aufgabe aus der entsprechenden Aufgabenliste.
     * Danach wird das Projekt in Firestore aktualisiert
     * @param position
     */
    public void deleteTask(Integer position) {

        new android.app.AlertDialog.Builder(AddTask.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Aufgabe löschen")
                .setMessage("Möchtest du diese Aufgabe wirklich löschen?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<Task> taskList = projectDetails.taskList.get(position).tasks;
                        Log.i(TAG, "TaskPosition: " + taskPosition);
                        Log.i(TAG, "TaskList vorher: " + taskList.size() + taskList.toString());
                        taskList.remove(taskPosition.intValue());
                        Log.i(TAG, "TaskList danach: " + taskList.size() + taskList.toString());

                        TaskList taskStatusList = projectDetails.taskList.get(position);
                        TaskList updatedTaskList = new TaskList(taskStatusList.name,
                                taskStatusList.createdBy, taskList);

                        projectDetails.taskList.set(position, updatedTaskList);

                        showDialog("Lösche Aufgabe...");
                        new FirestoreClass().updateTaskList(AddTask.this, projectDetails);
                    }
                })
                .setNegativeButton("Nein", null)
                .show();
    }

    /**
     * Diese Methode wird vpn der {@link FirestoreClass} aufgerufen, wenn eine Aufgabenliste
     * im Firestore erfolgreich aktualisiert werden konnte
     */
    public void updateStatusListSuccess() {
        hideDialog();
        showInfoSnackBar("Aufgabenliste erfolgreich aktualisiert!");
        setResult(RESULT_OK);
        finish();
    }

    /**
     * Diese Methode wird von der {@link FirestoreClass} aufgerufen, um die aus Firestore geholten
     * Projektdaten an die Activity zu übergeben.
     * @param project
     */
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

    // Ab hier alte nicht mehr benutzte Methoden (Soll irgendwann wieder aufgenommen werden)
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