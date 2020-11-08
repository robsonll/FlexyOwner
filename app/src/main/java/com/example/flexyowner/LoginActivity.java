package com.example.flexyowner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flexyowner.ControllerClasses.BusinessOwnerController;
import com.example.flexyowner.ModelClasses.Business;
import com.example.flexyowner.ModelClasses.BusinessOwner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private String TAG = "LoginActivity";
    EditText editTextEmail, editTextPassword;

    private String WEB_CLIENT_ID = "450593198851-lak8at4losegmdh6l6es9johv8ugj8la.apps.googleusercontent.com";
    private String WEB_CLIENT_SECRET = "Xleq5-tdfF5WWGGTlilmq3iO";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Button buttonLogin = findViewById(R.id.buttonLogin);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction();
            }
        });

    }


    //Button methods
    void loginAction(){

        //Check for empty fields and then login

        if (editTextEmail.getText().toString().equals(""))
        {
            Toast.makeText(this, "Please enter email address.", Toast.LENGTH_SHORT).show();
        }
        else if (!isValidEmail(editTextEmail.getText().toString()))
        {
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
        }
        else if(editTextPassword.getText().toString().equals(""))
        {
            Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Get authentication using firebase
            checkInFirebase();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void checkInFirebase() {


        //Firebase authentication
        mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FIREBASE ::", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            try {
                                verifyUser(user);
                            }
                            catch (Exception ex)
                            {
                                Toast.makeText(LoginActivity.this, "Error occured!!", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            Log.w("FIREBASE ::", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }


                    private void verifyUser(FirebaseUser fbUser){

                        final String userUid = fbUser.getUid();
                        final String userName = fbUser.getDisplayName();
                        final String userEmail = fbUser.getEmail();

                        final BusinessOwnerController userController = new BusinessOwnerController();

                        db = FirebaseFirestore.getInstance();

                        CollectionReference buyersReference = db.collection("business");

                        db.collection("business")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (task.isSuccessful()) {

                                            List<Business> listBusiness = new ArrayList<Business>();

                                            BusinessOwnerController businessOwnerController = new BusinessOwnerController();

                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                if(document.get("owners") != null) {

                                                    String strOwners = document.get("owners").toString();

                                                    if (strOwners.indexOf(userEmail) > 0) {

                                                        BusinessOwner user = new BusinessOwner(userEmail, null, null);
                                                        businessOwnerController.storeUser(LoginActivity.this, user);

                                                        listBusiness.add(document.toObject(Business.class));
                                                    }

                                                }

                                            }

                                            if (listBusiness.size() == 1) {
                                                businessOwnerController.storeListBusinesses(LoginActivity.this, listBusiness);
                                                businessOwnerController.storeCurrentBusiness(LoginActivity.this, listBusiness.get(0));

                                                startActivity(new Intent(LoginActivity.this, NavigationMainActivity.class));
                                            } else if (listBusiness.size() > 1) {
                                                businessOwnerController.storeListBusinesses(LoginActivity.this, listBusiness);

                                                startActivity(new Intent(LoginActivity.this, ChooseBusiness.class));
                                            } else {
                                                Toast.makeText(LoginActivity.this, "User not registered as a Business Owner. Contact FLEXY Adm.", Toast.LENGTH_LONG).show();
                                            }

                                        } else {
                                            Log.d("Teste", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });



                    }

                });

    }

}
