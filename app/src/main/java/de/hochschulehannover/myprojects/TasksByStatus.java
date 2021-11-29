package de.hochschulehannover.myprojects;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.hochschulehannover.myprojects.ui.main.SectionsPagerAdapter;
import de.hochschulehannover.myprojects.databinding.ActivityTasksByStatusBinding;

public class TasksByStatus extends AppCompatActivity {

    private ActivityTasksByStatusBinding binding;

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
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}