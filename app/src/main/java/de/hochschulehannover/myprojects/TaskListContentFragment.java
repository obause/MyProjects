package de.hochschulehannover.myprojects;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.hochschulehannover.myprojects.adapter.TaskListAdapter;
import de.hochschulehannover.myprojects.model.Project;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListContentFragment extends Fragment {

    public RecyclerView taskRecyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String status;
    private String mParam2;

    public TaskListContentFragment() {
        // Required empty public constructor
    }

    public TaskListContentFragment(String status) {
        // Required empty public constructor
        this.status = status;
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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        taskRecyclerView = getView().findViewById(R.id.taskListRecyclerView);
    }

    public void setupRecycler(Project project) {
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskRecyclerView.setHasFixedSize(true);

        TaskListAdapter adapter = new TaskListAdapter(getActivity(), project.taskList);
        taskRecyclerView.setAdapter(adapter);
    }
}