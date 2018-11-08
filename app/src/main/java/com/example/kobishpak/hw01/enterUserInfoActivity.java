package com.example.kobishpak.hw01;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.regex.Pattern;

public class enterUserInfoActivity extends AppCompatActivity {

    private final int RADIO_BUTTON_NOT_CHECKED = -1;

    private ImageView m_UserImageView;
    private EditText m_FullNameEditText;
    private EditText m_EmailEditText;
    private EditText m_PhoneNumberEditText;
    private EditText m_PasswordEditText;
    private RadioGroup m_GenderRadioButton;
    private EditText m_DateEditText;
    private Button m_SubmitButton;

    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_user_info);

        InitialiseInstances();
        
        m_SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OnClickSubmitButton();
            }
        });
    }

    private void InitialiseInstances()
    {
        m_UserImageView = findViewById(R.id.userImageView);
        m_FullNameEditText = findViewById(R.id.fullName);
        m_EmailEditText = findViewById(R.id.emailAddress);
        m_PhoneNumberEditText = findViewById(R.id.phoneNumber);
        m_PasswordEditText = findViewById(R.id.password);
        m_GenderRadioButton = findViewById(R.id.genderRadioGroup);
        m_DateEditText = findViewById(R.id.birthday);
        m_SubmitButton = findViewById(R.id.submit);
    }

    private void OnClickSubmitButton(){

        // Reset errors displayed in the form.
        m_EmailEditText.setError(null);
        m_PasswordEditText.setError(null);
        m_PhoneNumberEditText.setError(null);

        // Get the texts
        String email = m_EmailEditText.getText().toString();
        String password = m_PasswordEditText.getText().toString();
        String phoneNumber = m_PhoneNumberEditText.getText().toString();

        boolean cancel1 = false;
        boolean cancel2 = false;
        boolean cancel3 = false;
        boolean cancel4 = false;
        boolean cancel5 = false;
        View focusView = null;

        if (m_UserImageView.getDrawable() == null)
        {
            Toast.makeText(this, "Please add user image", Toast.LENGTH_SHORT).show();
            cancel1 = true;
        }

        if (!isPasswordValid(password))
        {
            m_PasswordEditText.setError(getString(R.string.error_invalid_register_password));
            focusView = m_PasswordEditText;
            cancel2 = true;
        }

        if (!isEmailValid(email))
        {
            m_EmailEditText.setError(getString(R.string.error_invalid_email));
            focusView = m_EmailEditText;
            cancel3 = true;
        }

        if (!isPhoneNumberValid((phoneNumber)))
        {
            m_PhoneNumberEditText.setError(getString(R.string.error_invalid_phone));
            focusView = m_PhoneNumberEditText;
            cancel4 = true;
        }

        if (m_GenderRadioButton.getCheckedRadioButtonId() == RADIO_BUTTON_NOT_CHECKED)
        {
            Toast.makeText(this, "Gender is not checked !", Toast.LENGTH_SHORT).show();
            cancel5 = true;
        }

        if (cancel1 || cancel2 || cancel3 || cancel4 || cancel5) {
            // There was an error
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            NextActivity();
        }
    }

    private boolean isEmailValid(String email) {
        return (email.contains("@") && email.contains(".com") &&  email.length() > 10 && email.length() < 30);
    }

    private boolean isPasswordValid(String password) {

        Pattern spacialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        // Means that password has to be with at least 1 Lowercase letter, 1 Uppercase, 1 number, and on special char

        return (password.length() > 5 && password.length() < 18 &&
                spacialCharPatten.matcher(password).find() && UpperCasePatten.matcher(password).find()
                && lowerCasePatten.matcher(password).find() && digitCasePatten.matcher(password).find());
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        return (digitCasePatten.matcher(phoneNumber).find() && phoneNumber.length() == 10);
    }

    private void NextActivity(){
        Intent intent = new Intent("");

        // TODO: add the new activity
        // TODO: add extras to next screen?
        startActivity(intent);
        finish();
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
