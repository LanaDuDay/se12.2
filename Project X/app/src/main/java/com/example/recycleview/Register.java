package com.example.recycleview;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Register extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword, editTextFullname, editTextDob, editTextMobile;
    private Button buttonReg;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private static final String TAG = "RegisterActivity";
    private DatePickerDialog picker;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), AccountCreatedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextFullname = findViewById(R.id.name);
        editTextDob = findViewById(R.id.dob);
        editTextMobile = findViewById(R.id.phone);
        buttonReg = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        editTextDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, fullName, dob, mobile;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                fullName = String.valueOf(editTextFullname.getText());
                dob = String.valueOf(editTextDob.getText());
                mobile = String.valueOf(editTextMobile.getText());

                // Kiểm tra từng điều kiện một theo thứ tự bạn muốn
                if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Valid email is required");
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password) || password.length() < 6) {
                    editTextPassword.setError("Password should be at least 6 characters");
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(fullName)) {
                    editTextFullname.setError("Full name cannot be empty");
                    editTextFullname.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(dob)) {
                    editTextDob.setError("Date of Birth cannot be empty");
                    editTextDob.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(mobile) || mobile.length() != 10) {
                    editTextMobile.setError("Mobile number should have 10 digits");
                    editTextMobile.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                int age = calculateAge(dob);

                if (age < 0) {
                    Toast.makeText(Register.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (age < 18) {
                    Toast.makeText(Register.this, "Bạn phải trên 18 tuổi để đăng ký", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Tiếp tục xử lý khi tất cả điều kiện đều hợp lệ
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    // Ghi thông tin người dùng vào Firebase Realtime Database
                                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(fullName, dob, mobile);
                                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        firebaseUser.sendEmailVerification();
                                                        Toast.makeText(Register.this, "Account created. Please verify your email", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                    Intent intent = new Intent(getApplicationContext(), AccountCreatedActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e){
                                        editTextPassword.setError("Your password is too weak. Try to use mixes of alphabets, numbers and special characters.");
                                        editTextPassword.requestFocus();

                                    } catch (FirebaseAuthInvalidCredentialsException e){
                                        editTextEmail.setError("Your email is invalid or already in use.");
                                        editTextEmail.requestFocus();
                                    } catch (FirebaseAuthUserCollisionException e){
                                        editTextEmail.setError("Your email is already registered.");
                                        editTextEmail.requestFocus();
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
            }
        });
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
}
