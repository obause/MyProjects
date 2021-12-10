package de.hochschulehannover.myprojects.firebase;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import de.hochschulehannover.myprojects.BaseActivity;
import de.hochschulehannover.myprojects.MainActivity;
import de.hochschulehannover.myprojects.ProfileActivity;
import de.hochschulehannover.myprojects.ProjectListActivity;
import de.hochschulehannover.myprojects.RegisterActivity;
import de.hochschulehannover.myprojects.model.User;

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

    public void loginUser(BaseActivity activity) {
        db.collection("users").document(getUserId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                User loggedInUser = document.toObject(User.class);
                                if (activity instanceof MainActivity) {
                                    ((MainActivity) activity).signInSuccess(loggedInUser);
                                } else if (activity instanceof ProjectListActivity){
                                    ((ProjectListActivity) activity).updateUserDetails(loggedInUser);
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


}
