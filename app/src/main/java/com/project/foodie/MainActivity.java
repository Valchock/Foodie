package com.project.foodie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.foodie.Model.UsersModel;
import com.project.foodie.Utils.AppConstants;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    public static final int AUTH_UI_REQUEST_CODE = 1;
    private DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            checkIfUserDetailsAvailableInFb(FirebaseAuth.getInstance().getCurrentUser());
        } else {
            phoneLogin();
        }
    }

    private void phoneLogin() {
        List<AuthUI.IdpConfig> provider = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(provider).build();
        startActivityForResult(intent, AUTH_UI_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTH_UI_REQUEST_CODE) {
            if (resultCode == RESULT_OK && FirebaseAuth.getInstance().getCurrentUser() != null) {
                checkIfUserDetailsAvailableInFb(FirebaseAuth.getInstance().getCurrentUser());
            } else {
                Toast.makeText(this, "Error Signing In", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkIfUserDetailsAvailableInFb(final FirebaseUser currentUser) {
        dbRef.child(AppConstants.DatabaseTables.users).child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UsersModel currentUser = dataSnapshot.getValue(UsersModel.class);
                    goToHomeActivity();
                    finish();
                } else {
                    registerUserDetails(currentUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void registerUserDetails(final FirebaseUser currentUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.register));
        View view = getLayoutInflater().inflate(R.layout.layout_user_registration, null);
        builder.setView(view);
        final TextInputEditText nameEt = view.findViewById(R.id.name_et);
        final TextInputEditText addressEt = view.findViewById(R.id.address_et);
        TextInputEditText phoneEt = view.findViewById(R.id.phone_et);
        phoneEt.setText(currentUser.getPhoneNumber());
        builder.setPositiveButton(getResources().getString(R.string.register), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!nameEt.getText().toString().isEmpty() && !addressEt.getText().toString().isEmpty()) {
                    UsersModel user = new UsersModel(nameEt.getText().toString(), addressEt.getText().toString(), currentUser.getPhoneNumber(), currentUser.getUid());
                    dbRef.child(AppConstants.DatabaseTables.users)
                            .child(currentUser.getUid())
                            .setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        goToHomeActivity();
                                        finish();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Error Signing In", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }
}
