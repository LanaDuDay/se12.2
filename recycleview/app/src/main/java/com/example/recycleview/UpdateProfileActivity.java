package com.example.recycleview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText editTextUpdateName, editTextUpdateDoB, editTextUpdatemobile;
    private String textFullName, textDob, textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update Profile Details");

        progressBar = findViewById(R.id.progressBar);

        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(R.id.textView_update_dob);
        editTextUpdatemobile = findViewById(R.id.textView_update_mobile);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //show profile
        showProfile(firebaseUser);

        //Update Email
        Button buttonUpdateEmail = findViewById(R.id.button_profile_update_email);
        /*buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivitu.class);
                startActivity(intent);
                finish();
            }
        });

         */
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textSADoB[] = textDob.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) -1;
                int year = Integer.parseInt(textSADoB[2]);
                DatePickerDialog picker;
                //Date Picker Dialog
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        textFullName = editTextUpdateName.getText().toString();
        textDob = editTextUpdateDoB.getText().toString();
        textMobile = editTextUpdatemobile.getText().toString();
        if(TextUtils.isEmpty(textFullName)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("FullName is required");
            editTextUpdateName.requestFocus();
        } if (TextUtils.isEmpty(textDob)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("FullName is required");
            editTextUpdateDoB.requestFocus();
        }if(textMobile.length() != 10) {
            Toast.makeText(UpdateProfileActivity.this, "Incorrect phone range", Toast.LENGTH_SHORT).show();
        }

        ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textDob, textMobile);

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered");
        progressBar.setVisibility(View.VISIBLE);

        String userID = firebaseUser.getUid();

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileUpdates);

                    Toast.makeText(UpdateProfileActivity.this, "Update Successful!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e){
                        Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }


    //fetcu datam from database
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered");
        progressBar.setVisibility(View.VISIBLE);
        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    textFullName = firebaseUser.getDisplayName();
                    textDob = readUserDetails.doB;
                    textMobile = readUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDob);
                    editTextUpdatemobile.setText(textMobile);
                } else {
                    System.err.println(Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG));
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }
}