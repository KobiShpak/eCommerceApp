package com.example.kobishpak.hw01;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class DisplayUserInfoActivity extends AppCompatActivity {

    private ImageView m_UserImageView;
    private TextView m_UserInfoTextView;
    private Button m_SendMailButton;
    private Button m_DialButton;
    private Bundle mBundle;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);

        mBundle = getIntent().getExtras();

        m_UserInfoTextView = findViewById(R.id.textViewUserInfo);
        m_DialButton = findViewById(R.id.buttonDial);
        m_SendMailButton = findViewById(R.id.buttonSendMail);
        m_UserImageView = findViewById(R.id.userImageView);

        try {
            Uri imageUri = Uri.parse((String) mBundle.get("userImage"));
            assert imageUri != null;
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            m_UserImageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        m_DialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mBundle.get("userPhoneNumber")));
                startActivity(intent);
            }
        });

        m_SendMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:" + mBundle.get("userEmail") + "?subject=" + "" + "&body=" + "");
                intent.setData(data);
                startActivity(intent);
            }
        });


        String userText = String.format("Name: %s\nEmail: %s\nPhone: %s\nPassword: %s\nGender: %s\nBirthday: %s",
                mBundle.get("userFullName"),
                mBundle.get("userEmail"),
                mBundle.get("userPhoneNumber"),
                mBundle.get("userPassword"),
                mBundle.get("userGender"),
                mBundle.get("date"));

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
