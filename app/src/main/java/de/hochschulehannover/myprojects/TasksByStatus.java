package de.hochschulehannover.myprojects;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import de.hochschulehannover.myprojects.ui.main.SectionsPagerAdapter;
import de.hochschulehannover.myprojects.databinding.ActivityTasksByStatusBinding;

public class TasksByStatus extends AppCompatActivity {

    private ActivityTasksByStatusBinding binding;
    static Integer projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTasksByStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new BacklogFragment("Backlog"), "Backlog");
        sectionsPagerAdapter.addFragment(new InProgressFragment("In Arbeit"), "In Bearbeitung");
        sectionsPagerAdapter.addFragment(new DoneFragment("Abgeschlossen"), "Abgeschlossen");

        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        projectId = extras.getInt("projectID");

        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(TasksByStatus.this, AddTask.class);
                intent.putExtra("projectID", projectId);
                startActivity(intent);
            }
        });
    }

    public void deleteProject() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        new android.app.AlertDialog.Builder(TasksByStatus.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Projekt löschen")
                .setMessage("Möchtest du dieses Projekt wirklich löschen?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.delete("projects", "id = ?", new String[]{projectId.toString()});
                        db.delete("tasks", "projectId = ?", new String[]{projectId.toString()});
                        ProjectListActivity.readProjects(dbHelper);
                        ProjectListActivity.arrayAdapter.notifyDataSetChanged();
                        Toast.makeText(TasksByStatus.this, "Projekt wurde gelöscht!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Nein", null)
                .show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        if (item.getItemId()==R.id.info){
            goToInfo();
        }
        if (item.getItemId() == R.id.deleteProject) {
            deleteProject();
        }
        return true;
    }
    public void goToInfo() {
        Intent intent = new Intent(TasksByStatus.this, InfosActivity.class);
        startActivity(intent);
    }

}