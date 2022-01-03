package de.hochschulehannover.myprojects;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hochschulehannover.myprojects.helper.DBHelper;

/**
 * <h2>Fragmentklasse DoneFragment</h2>
 *
 *<p>
 * <b>Autor: Joshua</b>
 * </p>
 */
public class DoneFragment extends Fragment {

    ListView taskListView;

    public static ArrayList<String> taskItems = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;
    static Integer projectId;
    public static Map<String, Integer> map = new HashMap<String, Integer>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DoneFragment(String status) {
        // Required empty public constructor
        this.mParam1 = status.toString();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskListFragment.
     */
    // TODO: Rename and change types and number of parameters
    /*public static TaskListFragment newInstance(String param1, String param2) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView projectIDTextView = (TextView) getView().findViewById(R.id.projectIDTextView);

        Bundle extras = getActivity().getIntent().getExtras();
        projectId = extras.getInt("projectID");
        projectIDTextView.setText("Projektnr.: " + projectId.toString());

        taskListView = getView().findViewById(R.id.taskListView);

        DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext());
        taskItems.clear();
        readTasks(dbHelper);

        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, taskItems);
        taskListView.setAdapter(arrayAdapter);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Integer taskId = map.get(taskListView.getItemAtPosition(i).toString());
                Intent intent = new Intent(getActivity().getApplicationContext(), AddTask.class);
                intent.putExtra("taskID", taskId);
                startActivity(intent);
            }
        });

    }

    public void readTasks (DBHelper dbHelper) {
        Log.i("Fragment", "In readTasks Methode");
        Log.i("Status", this.mParam1.toString());
        taskItems.clear();
        map.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE projectId = " + projectId + " AND status = \"" + this.mParam1 + "\"", null);

        int nameIndex = cursor.getColumnIndex("name");
        int idIndex = cursor.getColumnIndex("id");
        while (cursor.moveToNext()) {
            //   Log.i("Taskname", cursor.getString(nameIndex));
            taskItems.add(cursor.getString(nameIndex));
            map.put(cursor.getString(nameIndex), cursor.getInt(idIndex));
        }
        /*for (int i = 0; i <= 10; i++) {
            taskItems.add("TextText");
        }*/
    }
}