package com.example.kobishpak.hw01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DisplayUserInfoActivity extends AppCompatActivity {

    private static final String TAG = "Display";
    private ImageView m_UserImageView;
    private TextView m_UserInfoTextView;
    private Button m_LogOutButton;
    private FirebaseAuth m_FirebaseAuth;
    private FirebaseUser m_FirebaseUser;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);
        Log.e(TAG, getString(R.string.display_on_create));

        initializeInstances();

        m_LogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogOutButton();
            }
        });

        m_FirebaseAuth = FirebaseAuth.getInstance();
        if (m_FirebaseAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else {
            m_FirebaseUser = m_FirebaseAuth.getCurrentUser();
            pleaseWait();
            DisplayUserInformation();
        }
    }

    private void pleaseWait() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading. Please wait...");
        progressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000); // 2000 milliseconds delay for dialog
    }

    private void initializeInstances() {
        m_UserInfoTextView = findViewById(R.id.textViewUserInfo);
        m_UserImageView = findViewById(R.id.userImageView);
        m_LogOutButton = findViewById(R.id.buttonLogOut);
    }

    private void onClickLogOutButton() {
        m_FirebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        startActivity(new Intent(DisplayUserInfoActivity.this, LoginActivity.class));
    }

    private void DisplayUserInformation() {

        Glide.with(DisplayUserInfoActivity.this).load(m_FirebaseUser.getPhotoUrl()).into(m_UserImageView);

        String userText = String.format("Name: %s\nEmail: %s",
                m_FirebaseUser.getDisplayName(),
                m_FirebaseUser.getEmail());

        m_UserInfoTextView.setText(userText);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
