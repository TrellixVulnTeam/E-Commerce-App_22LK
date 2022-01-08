package com.example.ecommerce.Repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ecommerce.AdminAddNewProductActivity;
import com.example.ecommerce.AdminCategoryActivity;
import com.example.ecommerce.HomeActivity;
import com.example.ecommerce.LoadingDialog;
import com.example.ecommerce.LoginActivity;
import com.example.ecommerce.Model.User;
import com.example.ecommerce.prevalent.prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AuthRepo {

    private Application application;
    //private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;


//    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
//        return firebaseUserMutableLiveData;
//    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public AuthRepo(Application application){
        this.application = application;
        // firebaseUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


    }


    public void signUp(String email , String password,String userName,String isAdmin){
//        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()){
//                    firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
//                }else{
//                    Toast.makeText(application, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            if(isAdmin.equals("Admin Create Account")) {
                                User user = new User(userName, email, password);
                                String id = task.getResult().getUser().getUid();
                                user.setUserId(id);
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                firebaseUser.sendEmailVerification();
                                database.getReference().child("Admins").child(id).setValue(user);
                                    Toast.makeText(application, "Verification email is sent to you pleas check your email", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(application, LoginActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                application.startActivity(i);

                            }else if(isAdmin.equals("Create Account")){
                                User user = new User(userName,email,password);
                                String id = task.getResult().getUser().getUid();
                                user.setUserId(id);
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                assert firebaseUser != null;
                                firebaseUser.sendEmailVerification();
                                database.getReference().child("Users").child(id).setValue(user);
                                Toast.makeText(application,"Verification email is sent to you pleas check your email", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(application, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                application.startActivity(i);
                            }

                        }else{
                            Toast.makeText(application,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void signIn(String email , String pass , String isAdmine){
//        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()){
//                    firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
//                }else{
//                    Toast.makeText(application, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        firebaseAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(isAdmine.equals("Admin Log in")){
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        String id = task.getResult().getUser().getUid();
                        if(firebaseUser.isEmailVerified()){
                            database.getReference().child("Admins")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           if(task.isSuccessful()){
                                               for (DataSnapshot dataSnapshot :snapshot.getChildren() ) {
                                                   User users = dataSnapshot.getValue(User.class);
                                                   users.setUserId(dataSnapshot.getKey());
                                                   if ((users.getUserId().equals(firebaseUser.getUid()))) {
                                                       Intent i = new Intent(application, AdminCategoryActivity.class);
                                                       Toast.makeText(application,"Signed In successful", Toast.LENGTH_LONG).show();
                                                       i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                       application.startActivity(i);
                                                   }
//
                                               }
                                           }else {
                                               Toast.makeText(application,task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                           }

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });

                        }else {
                            Toast.makeText(application,"Pleas check your email and click on the verification link", Toast.LENGTH_LONG).show();
                        }

                    }
                    else if(isAdmine.equals("Log in")){
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    String id = task.getResult().getUser().getUid();
                    if(firebaseUser.isEmailVerified()){
                        database.getReference().child("Users")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (task.isSuccessful()){
                                            for (DataSnapshot dataSnapshot :snapshot.getChildren() ) {
                                                User users = dataSnapshot.getValue(User.class);
                                                users.setUserId(dataSnapshot.getKey());
                                                if ((users.getUserId().equals(id))) {
                                                    Intent i = new Intent(application, HomeActivity.class);
                                                    Toast.makeText(application,"Signed In successful", Toast.LENGTH_LONG).show();
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    application.startActivity(i);
                                                }
//                                            else {
//                                                Toast.makeText(application, "This account not existed,Pleas Regester and try again\n if you're an normal user click on (You're not an admin) ", Toast.LENGTH_SHORT).show();
//                                            }
                                            }
                                        }else{
                                            Toast.makeText(application,task.getException().getMessage(), Toast.LENGTH_LONG).show(); }

                                        }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                    }else {
                        Toast.makeText(application,"Pleas check your email and click on the verification link", Toast.LENGTH_LONG).show();

                    }
                    }

                }else{
                    Toast.makeText(application,
                            task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    public void signOut(){
        firebaseAuth.signOut();
        Intent i = new Intent(application, LoginActivity.class);
        Toast.makeText(application,"sign out In successful", Toast.LENGTH_LONG).show();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(i);

    }
}