package de.hochschulehannover.myprojects;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hochschulehannover.myprojects.adapter.TaskListAdapter;
import de.hochschulehannover.myprojects.model.Project;
import de.hochschulehannover.myprojects.model.Task;
import de.hochschulehannover.myprojects.model.TaskList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListContentFragment extends Fragment {

    public RecyclerView taskRecyclerView;
    public ArrayList<Task> tasks = new ArrayList<>();
    public TaskListAdapter adapter;

    TextView testTextView;

    private View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATUS_PARAM = "status";
    private static final String INDEX_PARAM = "index";

    // TODO: Rename and change types of parameters
    private String status;
    private Integer index;

    public TaskListContentFragment() {
        // Required empty public constructor
    }

    public TaskListContentFragment(String status, Integer index) {
        this.status = status;
        this.index = index;
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

        testTextView = view.findViewById(R.id.testTextView);

        //TaskListAdapter adapter = new TaskListAdapter(getActivity(), tasks);
        adapter = new TaskListAdapter(getActivity(), tasks);
        taskRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Log.i("Fragment", "OnViewCreated ausgeführt");

        //taskRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        /*
        tasks.add(new Task("onViewCreated", "Test", new ArrayList<String>(), "Abgeschlossen",
                "Hoch", "Beschreibung"));

        taskRecyclerView = getView().findViewById(R.id.taskListRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskRecyclerView.setHasFixedSize(true);

        //TaskListAdapter adapter = new TaskListAdapter(getActivity(), tasks);
        adapter = new TaskListAdapter(getActivity(), tasks);
        taskRecyclerView.setAdapter(adapter);

        TextView testTextView = view.findViewById(R.id.testTextView);
        testTextView.setText("Funktioniert mit view.find...");

        TextView testTextView2 = getView().findViewById(R.id.testTextView2);
        testTextView2.setText("Funktioniert mit getview().find...");
        */
    }

    public void setupRecycler(TaskList taskList) {
        tasks = taskList.tasks;
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskRecyclerView.setHasFixedSize(true);

        TaskListAdapter adapter = new TaskListAdapter(getActivity(), taskList.tasks);
        taskRecyclerView.setAdapter(adapter);
        //TextView testTextView = view.findViewById(R.id.testTextView);
        testTextView.setText("Funktioniert...");
        adapter.notifyDataSetChanged();
    }
}