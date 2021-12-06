package de.hochschulehannover.myprojects.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import de.hochschulehannover.myprojects.RegisterActivity;
import de.hochschulehannover.myprojects.model.User;

public class FirestoreClass {

    //private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void registerUser(RegisterActivity activity, User userInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(getUserId()).set(userInfo)
                .addOnSuccessListener(activity.userRegistered())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FirestoreClass", e.toString());
                    }
                });
    }

    public String getUserId() {
        Log.i("FirestoreClass", mAuth.getCurrentUser().getUid());
        Log.i("FirestoreClass", mAuth.getUid());
        return mAuth.getCurrentUser().getUid();
    }


}
