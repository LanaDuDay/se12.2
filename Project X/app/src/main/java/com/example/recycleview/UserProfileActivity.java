package com.example.recycleview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private Button button;
    private TextView textViewWelcome, textViewFullname, textViewEmail, textViewDoB, textViewMobile;
    private ProgressBar progressBar;

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setupButtonClickListeners();
        textViewWelcome = findViewById(R.id.textView_show_welcome);
        textViewFullname = findViewById(R.id.textView_show_fulL_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewDoB = findViewById(R.id.textView_show_dob);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        button = findViewById(R.id.logOut);
        progressBar = findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            // Người dùng chưa đăng nhập, chuyển hướng đến màn hình đăng nhập
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            // Người dùng đã đăng nhập, hiển thị thông tin người dùng
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                textViewWelcome.setText("Welcome, " + displayName + "!");
            } else {
                textViewWelcome.setText("Welcome!");
            }
            showUserProfile(user);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    String fullName = readUserDetails.fullName;
                    String email = firebaseUser.getEmail();
                    String doB = readUserDetails.doB;
                    String mobile = readUserDetails.mobile;

                    textViewFullname.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewMobile.setText(mobile);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setupButtonClickListeners() {
        setOnClickListener(R.id.imageViewButton1, MainActivity3.class);
        setOnClickListener(R.id.imageViewButton2, MainActivity.class);
        setOnClickListener(R.id.imageViewButton3, MainActivity2.class);
        setOnClickListener(R.id.imageViewButton4, UserProfileActivity.class);
    }

    private void setOnClickListener(int imageViewId, final Class<?> targetActivity) {
        ImageView imageView = findViewById(imageViewId);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a ViewPropertyAnimator for the clicked ImageView
                ViewPropertyAnimator animator = v.animate();

                // Add the desired animation effects
                animator.alpha(0.5f)  // Example: Reduce opacity
                        .scaleX(1.2f)  // Example: Zoom in horizontally
                        .scaleY(1.2f)  // Example: Zoom in vertically
                        .setDuration(200)  // Set the duration of the animation (in milliseconds)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // Define Intent for the target activity
                                Intent intent = new Intent(UserProfileActivity.this, targetActivity);

                                // Run the Intent
                                startActivity(intent);

                                // Add transition animation (optional)
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                // Reset the properties after the animation ends
                                v.setAlpha(1.0f);
                                v.setScaleX(1.0f);
                                v.setScaleY(1.0f);
                            }
                        });

                // Start the animation
                animator.start();
            }
        });
    }
}
