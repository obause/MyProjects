package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import de.hochschulehannover.myprojects.firebase.FirestoreClass;
import de.hochschulehannover.myprojects.model.User;

public class ProjectListActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbarProjectList;
    private NavigationView navigationView;

    ListView projectListView;

    public static ArrayList<String> projectItems = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;
    static Map<String, Integer> map = new HashMap<String, Integer>();

    public static void readProjects (DBHelper dbHelper) {
        projectItems.clear();
        map.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name FROM projects", null);

        int nameIndex = cursor.getColumnIndex("name");
        int idIndex = cursor.getColumnIndex("id");
        while (cursor.moveToNext()) {
            projectItems.add(cursor.getString(nameIndex));
            map.put(cursor.getString(nameIndex), cursor.getInt(idIndex));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);


        //final LayoutInflater inflater = getLayoutInflater();
        //final View toolbar = inflater.inflate(R.layout.app_bar_main, null);
        //toolbarProjectList = toolbar.findViewById(R.id.projectListToolbar);

        toolbarProjectList = findViewById(R.id.projectListToolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        projectListView = findViewById(R.id.projectListView);

        setupActionBar();
        navigationView.setNavigationItemSelectedListener(this);

        new FirestoreClass().loginUser(this);

        DBHelper dbHelper = new DBHelper(this);

        projectItems.clear();
        map.clear();

        readProjects(dbHelper);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, projectItems);
        projectListView.setAdapter(arrayAdapter);

        projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Integer projectID = map.get(projectListView.getItemAtPosition(i).toString());
                Intent intent = new Intent(getApplicationContext(), TasksByStatus.class);
                intent.putExtra("projectID",projectID);
                startActivity(intent);
            }
        });

    }

    public void updateUserDetails(User user) {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView userImage = findViewById(R.id.userImageView);

        Glide
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(userImage);

        TextView navUsername = findViewById(R.id.usernameTextView);
        navUsername.setText(user.name);
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
        return true;
    }
    public void goToInfo() {
        Intent intent = new Intent(ProjectListActivity.this, InfosActivity.class);
        startActivity(intent);
    }

    public void createProject(View view) {
        Intent intent = new Intent(ProjectListActivity.this, AddProject.class);
        startActivity(intent);
    }

    private void setupActionBar() {
        setSupportActionBar(toolbarProjectList);
        toolbarProjectList.setNavigationIcon(R.drawable.ic_action_navigation_menu);

        toolbarProjectList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleDrawer();
            }
        });
    }

    private void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            doubleBackExit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_my_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        if (item.getItemId()==R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}