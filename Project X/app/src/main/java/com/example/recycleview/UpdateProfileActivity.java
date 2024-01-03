package com.example.recycleview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText editTextUpdateName, editTextUpdateDoB, editTextUpdatemobile;
    private String textFullName, textDob, textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        progressBar = findViewById(R.id.progressBar);

        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(R.id.textView_update_dob);
        editTextUpdatemobile = findViewById(R.id.textView_update_mobile);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        // Show profile
        showProfile(firebaseUser);

        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Date Picker Dialog
                showDatePickerDialog();
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

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                UpdateProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        editTextUpdateDoB.setText(selectedDate);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        textFullName = editTextUpdateName.getText().toString();
        textDob = editTextUpdateDoB.getText().toString();
        textMobile = editTextUpdatemobile.getText().toString();

        if (TextUtils.isEmpty(textFullName) || TextUtils.isEmpty(textDob) || TextUtils.isEmpty(textMobile)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter all fields", Toast.LENGTH_LONG).show();
            return;
        }

        int age = calculateAge(textDob);
        if (age < 0) {
            Toast.makeText(UpdateProfileActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
        } else if (age < 18) {
            Toast.makeText(UpdateProfileActivity.this, "You must be at least 18 years old to update your profile", Toast.LENGTH_SHORT).show();
        }else if (age > 150) {
            Toast.makeText(UpdateProfileActivity.this, "Input correct Age range please", Toast.LENGTH_SHORT).show();
        } else if (textMobile.length() != 10) {
            Toast.makeText(UpdateProfileActivity.this, "Incorrect phone number length", Toast.LENGTH_SHORT).show();
        } else {
            // Update user profile
            updateUserInfo(firebaseUser);
        }
    }

    private int calculateAge(String dobString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date dateOfBirth;
        try {
            dateOfBirth = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }

        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dateOfBirth);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    private void updateUserInfo(FirebaseUser firebaseUser) {
        progressBar.setVisibility(View.VISIBLE);

        ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textDob, textMobile);
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        String userID = firebaseUser.getUid();

        referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(UpdateProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UpdateProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);
        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    textFullName = firebaseUser.getDisplayName();
                    textDob = readUserDetails.doB;
                    textMobile = readUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDob);
                    editTextUpdatemobile.setText(textMobile);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
