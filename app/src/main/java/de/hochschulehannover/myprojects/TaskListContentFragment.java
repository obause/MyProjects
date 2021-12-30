package de.hochschulehannover.myprojects;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import de.hochschulehannover.myprojects.adapter.TaskListAdapter;
import de.hochschulehannover.myprojects.firebase.FirestoreClass;
import de.hochschulehannover.myprojects.model.Project;
import de.hochschulehannover.myprojects.model.Task;
import de.hochschulehannover.myprojects.model.TaskList;
import de.hochschulehannover.myprojects.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListContentFragment extends Fragment {

    private static final int RESULT_OK = 1;
    public static final int UPDATE_TASK_REQUEST_CODE = 1;

    public RecyclerView taskRecyclerView;
    public ArrayList<Task> tasks = new ArrayList<>();
    public TaskListAdapter adapter;

    private View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATUS_PARAM = "status";
    private static final String INDEX_PARAM = "index";

    // TODO: Rename and change types of parameters
    private String status;
    private Integer index;
    private Project project;

    public TaskListContentFragment() {
        // Required empty public constructor
    }

    public TaskListContentFragment(String status, Integer index) {
        this.status = status;
        this.index = index;
    }

    public TaskListContentFragment(String status, Integer index, Project project) {
        this.status = status;
        this.index = index;
        this.project = project;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskListContentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskListContentFragment newInstance(String param1, String param2) {
        TaskListContentFragment fragment = new TaskListContentFragment();
        Bundle args = new Bundle();
        args.putString(STATUS_PARAM, param1);
        args.putString(INDEX_PARAM, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(STATUS_PARAM);
            index = getArguments().getInt(INDEX_PARAM);
            project = getArguments().getParcelable("project");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_task_list_content, container, false);

        Log.i("Fragment", "OnCreateView ausgeführt");

        taskRecyclerView = view.findViewById(R.id.taskListRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskRecyclerView.setHasFixedSize(true);

        project = ((TaskListActivity)getActivity()).getProject();
        if (project != null) {
            tasks = project.taskList.get(index).tasks;
        } else {
            Log.i("TaskListContentFragment", "Es konnten keine Tasks abgerufen werden");
        }

        Log.i("TaskListContentFragment", "TaskList Index:" + String.valueOf(index));


        //TaskListAdapter adapter = new TaskListAdapter(getActivity(), tasks);
        adapter = new TaskListAdapter(getActivity(), tasks);
        taskRecyclerView.setAdapter(adapter);



        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Log.i("Fragment", "OnViewCreated ausgeführt");

        //taskRecyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        /*
        tasks.add(new Task("onViewCreated", "Test", new ArrayList<String>(), "Abgeschlossen",
                "Hoch", "Beschreibung"));

        taskRecyclerView = getView().findViewById(R.id.taskListRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskRecyclerView.setHasFixedSize(true);

        //TaskListAdapter adapter = new TaskListAdapter(getActivity(), tasks);
        adapter = new TaskListAdapter(getActivity(), tasks);
        taskRecyclerView.setAdapter(adapter);
        */
    }

    public void setupRecycler(TaskList taskList, Project project) {
        this.tasks = taskList.tasks;
        this.project = project;

        try {
            /*taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            taskRecyclerView.setHasFixedSize(true);*/
            adapter = new TaskListAdapter(getActivity(), taskList.tasks);
            taskRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            /* Implementierung des Interface aus der Adapterklasse, Bei Klick auf Projekt TaskListActivity
             * starten und documentId des entsprechenden Projekt übergeben
             */
            adapter.setOnClickListener(new TaskListAdapter.ItemClickListener() {
                @Override
                public void onClick(int position, Task task, View view, String s) {
                    /*Intent intent = new Intent(getActivity(), AddTask.class);
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId);
                    intent.putExtra(Constants.NAME, model.userId);
                    startActivity(intent);*/

                    if (s.equals("edit")) {
                        //Toast.makeText(getActivity(), "Edit gedrückt", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), AddTask.class);
                        intent.putExtra(Constants.DOCUMENT_ID, project.documentId);
                        intent.putExtra(Constants.NAME, project.userId);
                        intent.putExtra("edit", true);
                        intent.putExtra("task", task);
                        intent.putExtra("taskPosition", position);
                        startActivityForResult(intent, 1);
                    } else if (s.equals("changeStatus")) {
                        Toast.makeText(getActivity(), "Haken gedrückt", Toast.LENGTH_SHORT).show();
                    }
                    Log.i("TaskListContentFragment", "Task geklickt:" + task.name);
                }
            });

            Log.i("SetupRecycler", "Recycler erfolgreich aufgesetzt:" + status);
        } catch (Exception e) {
            Log.i("SetupRecycler", "Fehler für " + status + "aufgetreten:" + e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UPDATE_TASK_REQUEST_CODE) {
            ((TaskListActivity)getActivity()).updateStatusListSuccess();
        } else {
            Log.e("TaskListActivity","Aufgabenerstellung abgebrochen");
        }
    }
}