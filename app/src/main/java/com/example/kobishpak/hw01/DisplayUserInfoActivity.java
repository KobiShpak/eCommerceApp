package com.example.kobishpak.hw01;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DisplayUserInfoActivity extends AppCompatActivity {

    private static final String TAG = "Display";
    private ImageView m_UserImageView;
    private TextView m_UserInfoTextView;
    private Button m_SendMailButton;
    private Button m_DialButton;
    private FirebaseAuth m_FirebaseAuth;
    private FirebaseUser m_User;
    private DatabaseReference m_DatabaseReferece;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);
        Log.e(TAG, "Display==> OnCreate!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        m_FirebaseAuth = FirebaseAuth.getInstance();
        if (m_FirebaseAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        m_User = m_FirebaseAuth.getCurrentUser();
        m_DatabaseReferece = FirebaseDatabase.getInstance().getReference();
        m_UserInfoTextView = findViewById(R.id.textViewUserInfo);
        m_DialButton = findViewById(R.id.buttonDial);
        m_SendMailButton = findViewById(R.id.buttonSendMail);
        m_UserImageView = findViewById(R.id.userImageView);

        /*try {
            Uri imageUri = Uri.parse((String) mBundle.get("userImage"));
            assert imageUri != null;
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            m_UserImageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }*/

        m_DialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + m_User.getPhoneNumber()));
                startActivity(intent);
            }
        });

        m_SendMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:" + m_User.getEmail() + "?subject=" + "" + "&body=" + "");
                intent.setData(data);
                startActivity(intent);
            }
        });


        String userText = String.format("Name: %s\nEmail: %s\nPhone: %s\nPassword: %s\nGender: %s\nBirthday: %s",
                m_User.getDisplayName(),
                m_User.getEmail(),
                "",
                "",
                "",
                "");

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
