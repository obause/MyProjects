package de.hochschulehannover.myprojects.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;

import de.hochschulehannover.myprojects.AddProject;
import de.hochschulehannover.myprojects.BaseActivity;
import de.hochschulehannover.myprojects.MainActivity;
import de.hochschulehannover.myprojects.ProfileActivity;
import de.hochschulehannover.myprojects.ProjectListActivity;
import de.hochschulehannover.myprojects.RegisterActivity;
import de.hochschulehannover.myprojects.TaskListActivity;
import de.hochschulehannover.myprojects.model.Project;
import de.hochschulehannover.myprojects.model.User;
import de.hochschulehannover.myprojects.utils.Constants;

public class FirestoreClass {

    private String TAG = "FirestoreClass";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void registerUser(RegisterActivity activity, User userInfo) {

        db.collection("users").document(getUserId()).set(userInfo)
                .addOnSuccessListener(activity.userRegistered())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FirestoreClass", e.toString());
                    }
                });
    }

    public void createProject(AddProject activity, Project project) {
        db.collection(Constants.PROJECTS_TABLE).document().set(project, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("CreateProject", "Projekt erstellt");
                        activity.showInfoSnackBar("Projekt erfolgreich erstellt");
                        activity.projectCreatedSuccessfully();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.hideDialog();
                        Log.e("CreateProject", "Fehler beim erstellen", e);
                        activity.showErrorSnackBar("Fehler beim Erstellen des Projekts");
                    }
                });
    }

    public void getProjectList(ProjectListActivity activity) {
        db.collection(Constants.PROJECTS_TABLE)
                .whereArrayContains(Constants.ASSIGNED_TO, getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.i(TAG, queryDocumentSnapshots.toString());

                        ArrayList<Project> projectsList = new ArrayList<>();

                        Log.i(TAG, "Anzahl Projekte:" + queryDocumentSnapshots.size());

                        for (QueryDocumentSnapshot i:
                             queryDocumentSnapshots) {
                            Project project = i.toObject(Project.class);
                            project.documentId = i.getId();

                            Log.i(TAG, project.name + " geladen");

                            projectsList.add(project);
                        }
                        Log.i(TAG, "Projekte erfolgreich geladen. Aktualisiere UI...");
                        activity.projectsToUi(projectsList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.hideDialog();
                        Log.e(TAG, "Fehler beim Laden der Projekte:", e);
                        activity.showErrorSnackBar("Fehler beim Laden der Projekte!");
                    }
                });
    }

    public void getProjectDetails(TaskListActivity activity, String documentId) {
        db.collection(Constants.PROJECTS_TABLE)
                .document(documentId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.i(TAG, "projectDetails:" + documentSnapshot.toString());
                        // documentSnapshot der Die Projektinfos beinhaltet in ein Objekt der Klasse Project umwandeln
                        Project project = documentSnapshot.toObject(Project.class);
                        activity.getProjectDetails(project);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.hideDialog();
                        Log.e(TAG, "Fehler beim Laden des Projekts:\n" + e);
                        activity.showErrorSnackBar("Fehler beim Laden des Projekts!");
                    }
                });
    }

    public void loadUserData(BaseActivity activity) {
        loadUserData(activity, false);
    }

    public void loadUserData(BaseActivity activity, Boolean readProjectsList) {
        db.collection("users").document(getUserId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.i(TAG, "Nutzerdaten geladen");
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                User loggedInUser = document.toObject(User.class);
                                if (activity instanceof MainActivity) {
                                    ((MainActivity) activity).signInSuccess(loggedInUser);
                                } else if (activity instanceof ProjectListActivity){
                                    Log.i(TAG, "Aktualisiere Nutzerdaten in UI...");
                                    ((ProjectListActivity) activity).updateUserDetails(loggedInUser, readProjectsList);
                                } else if (activity instanceof ProfileActivity){
                                    ((ProfileActivity) activity).setUserDetails(loggedInUser);
                                } else if (activity instanceof RegisterActivity) {
                                    ((RegisterActivity) activity).signInSuccess(loggedInUser);
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            activity.hideDialog();
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    public String getUserId() {
        Log.i("FirestoreClass", mAuth.getCurrentUser().getUid());
        Log.i("FirestoreClass", mAuth.getUid());
        return mAuth.getCurrentUser().getUid();
    }

    public void updateUserData(ProfileActivity activity, HashMap<String, Object> userHashMap) {
        db.collection(Constants.USERS_TABLE)
                .document(getUserId())
                .update(userHashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        activity.showInfoSnackBar("Nutzerdaten erfolgreich aktualisiert");
                        activity.updateUserProfileSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.hideDialog();
                        Log.e("FirestoreClass", "Fehler beim aktualisieren der Nutzerdaten", e);
                        activity.showErrorSnackBar("Fehler beim aktualisieren der Nutzerdaten");
                    }
                });
    }

}
