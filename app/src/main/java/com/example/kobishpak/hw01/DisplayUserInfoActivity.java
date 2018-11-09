package com.example.kobishpak.hw01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class DisplayUserInfoActivity extends AppCompatActivity {

    private ImageView m_UserImageView;
    private EditText m_UserInfoEditText;
    private Button m_SendMailButton;
    private Button m_DialButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);

    }


}
