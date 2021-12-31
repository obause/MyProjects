package de.hochschulehannover.myprojects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hochschulehannover.myprojects.adapter.MemberListAdapter;
import de.hochschulehannover.myprojects.firebase.FirestoreClass;
import de.hochschulehannover.myprojects.model.Project;
import de.hochschulehannover.myprojects.model.User;
import de.hochschulehannover.myprojects.utils.Constants;

public class ProjectMembersActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView memberListRecyclerView;
    private Button addMemberButton;

    private Project projectDetails;
    private ArrayList<User> assignedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_members);

        toolbar = findViewById(R.id.membersToolbar);
        setupActionBar(getString(R.string.members));

        memberListRecyclerView = findViewById(R.id.memberListRecyclerView);

        addMemberButton = findViewById(R.id.addMemberButton);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMember();
            }
        });

        if (getIntent().hasExtra(Constants.PROJECT_DETAILS)) {
            projectDetails = getIntent().getParcelableExtra(Constants.PROJECT_DETAILS);
        }

        showDialog("Lade Mitglieder...");
        new FirestoreClass().getMembersFromProject(this, projectDetails.assignedUsers);
    }

    public void setupMemberList(ArrayList<User> list) {
        hideDialog();

        assignedUsers = list;

        // RecyclerView aufsetzen
        memberListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberListRecyclerView.setHasFixedSize(true);

        MemberListAdapter adapter = new MemberListAdapter(this, list);
        memberListRecyclerView.setAdapter(adapter);
    }

    public void searchMember() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fragment_search_member);

        TextView addMemberTextView = dialog.findViewById(R.id.addMemberTextView);
        addMemberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mailEditText = dialog.findViewById(R.id.memberMailEditText);
                String mail = mailEditText.getText().toString();

                if (mail != null) {
                    dialog.dismiss();
                    showDialog("Füge Nutzer hinzu...");
                    new FirestoreClass().searchMember(ProjectMembersActivity.this, mail);
                } else {
                    showErrorSnackBar("Bitte eine Mail-Adresse eingeben");
                }
            }
        });

        TextView cancelTextView = dialog.findViewById(R.id.cancelTextView);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void memberDetails(User user) {
        projectDetails.assignedUsers.add(user.id);
        new FirestoreClass().addMemberToProject(this, projectDetails, user);
    }

    public void addMemberSuccess(User user) {
        hideDialog();
        assignedUsers.add(user);
        // Layout aktualisieren
        setupMemberList(assignedUsers);
        showInfoSnackBar("Nutzer erfolgreich zum Projekt hinzugefügt!");
    }

    // Custom ActionBar initialisieren
    private void setupActionBar(String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp);
            actionBar.setTitle(title);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}