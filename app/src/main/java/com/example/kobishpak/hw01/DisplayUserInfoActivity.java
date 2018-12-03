package com.example.kobishpak.hw01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class DisplayUserInfoActivity extends AppCompatActivity {

    private static final String TAG = "Display";
    private ImageView m_UserImageView;
    private TextView m_UserInfoTextView;
    private Button m_SendMailButton;
    private Button m_DialButton;
    private Button m_LogOutButton;
    private FirebaseAuth m_FirebaseAuth;
    private FirebaseUser m_FirebaseUser;
    private DatabaseReference m_DatabaseReference;
    private UserInformation m_UserInfo;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);
        Log.e(TAG, getString(R.string.display_on_create));

        initializeInstances();

        m_FirebaseAuth = FirebaseAuth.getInstance();
        if (m_FirebaseAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else {
            m_FirebaseUser = m_FirebaseAuth.getCurrentUser();
            m_DatabaseReference = FirebaseDatabase.getInstance().getReference("users");
            pleaseWait();
            readUserInfoFromDatabase();
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
        m_DialButton = findViewById(R.id.buttonDial);
        m_SendMailButton = findViewById(R.id.buttonSendMail);
        m_UserImageView = findViewById(R.id.userImageView);
        m_LogOutButton = findViewById(R.id.buttonLogOut);
        m_LogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogOutButton();
            }
        });
    }

    private void onClickLogOutButton() {
        m_FirebaseAuth.signOut();
        startActivity(new Intent(DisplayUserInfoActivity.this, LoginActivity.class));
    }

    private void readUserInfoFromDatabase() {
        m_DatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                m_UserInfo = new UserInformation();

                if (m_FirebaseAuth.getCurrentUser() != null) {
                    m_UserInfo = dataSnapshot.child(m_FirebaseAuth.getCurrentUser().getUid()).getValue(UserInformation.class);
                }

                DisplayUserInformation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayUserInformation() {

        Log.e(TAG, "URI is:" + m_UserInfo.getM_ImageUri());
//        m_UserImageView.setImageURI(Uri.parse(m_UserInfo.getM_ImageUri()));
        Glide.with(this).load(Uri.parse(m_UserInfo.getM_ImageUri())).into(m_UserImageView);

        m_DialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + m_FirebaseUser.getPhoneNumber()));
                startActivity(intent);
            }
        });

        m_SendMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:" + m_FirebaseUser.getEmail() + "?subject=" + "" + "&body=" + "");
                intent.setData(data);
                startActivity(intent);
            }
        });


        String userText = String.format("Name: %s\nEmail: %s\nPhone: %s\nPassword: %s\nGender: %s\nBirthday: %s",
                m_UserInfo.getM_FullName(),
                m_UserInfo.getM_Email(),
                m_UserInfo.getM_PhoneNumber(),
                m_UserInfo.getM_Password(),
                m_UserInfo.getM_Gender(),
                m_UserInfo.getM_Date());

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
