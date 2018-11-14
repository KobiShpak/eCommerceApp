package com.example.kobishpak.hw01;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class DisplayUserInfoActivity extends AppCompatActivity {

    private ImageView m_UserImageView;
    private EditText m_UserInfoEditText;
    private Button m_SendMailButton;
    private Button m_DialButton;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);

        bundle = getIntent().getExtras();
        m_DialButton = (Button) findViewById(R.id.buttonDial);
        m_UserImageView = (ImageView) findViewById(R.id.userImageView);
        try {
            Uri imageUri = Uri.parse((String) bundle.get("userImage"));
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
                intent.setData(Uri.parse("tel:" + bundle.get("userPhoneNumber")));
                startActivity(intent);
            }
        });

    }


}
