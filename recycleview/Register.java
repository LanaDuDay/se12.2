package com.example.recycleview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.nfc.Tag;
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

import java.util.Calendar;

public class Register extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword, editTextFullname, editTextDob, editTextMobile;
    private Button buttonReg;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textView;
    private static final String Tag = "RegisterActivity";
    private DatePickerDialog picker;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
        textView = findViewById(R.id.loginNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        editTextDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                //Date Picker Dialog
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
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Email cannot be empty");
                    editTextEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(Register.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextEmail.setError("Valid email is required");
                    editTextEmail.requestFocus();
                } else if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Password cannot be empty");
                    editTextPassword.requestFocus();
                } else if (password.toString().length() < 6) {
                    Toast.makeText(Register.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Password too weak");
                    editTextPassword.requestFocus();
                } else if(mobile.length() != 10) {
                    Toast.makeText(Register.this, "Incorrect phone range", Toast.LENGTH_SHORT).show();
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);

                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    //Cho user data vao Firebase realtime Database.
                                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(fullName, dob, mobile);

                                    //Extact user từ database
                                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                firebaseUser.sendEmailVerification();
                                                Toast.makeText(Register.this, "Account created. Please verify your email", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e){
                                        editTextPassword.setError("Your password is too weak. Try to use mixes of alphabets, numbers and special characters.");
                                        editTextPassword.requestFocus();

                                    } catch (FirebaseAuthInvalidCredentialsException e){
                                        editTextPassword.setError("Your email is invalid or already in use.");
                                        editTextPassword.requestFocus();
                                    } catch (FirebaseAuthUserCollisionException e){
                                        editTextPassword.setError("Your email is already registered.");
                                        editTextPassword.requestFocus();
                                    } catch (Exception e) {
                                        Log.e(Tag, e.getMessage());
                                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    // If sign in fails, display a message to the user.



                                }
                            }
                        });


            }
        });
    }
}