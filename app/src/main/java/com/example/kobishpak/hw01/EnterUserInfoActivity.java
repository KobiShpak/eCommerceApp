package com.example.kobishpak.hw01;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Pattern;

public class EnterUserInfoActivity extends AppCompatActivity {

    private static final int RADIO_BUTTON_NOT_CHECKED = -1;
    public static final int GET_FROM_GALLERY = 2;

    private EditText m_FullNameEditText;
    private EditText m_EmailEditText;
    private EditText m_PhoneNumberEditText;
    private EditText m_PasswordEditText;
    private RadioGroup m_GenderRadioButton;
    private EditText m_DateEditText;
    private Button m_ImageUploadButton;
    private Button m_SubmitButton;

    private boolean doubleBackToExitPressedOnce = false;
    private boolean isImageUploaded = false;
    private Bitmap userImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_user_info);

        InitialiseInstances();

        m_FullNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(!hasFocus){
                    if (((EditText)v).getText().toString().isEmpty())
                    {
                        ((EditText)v).setError(getString(R.string.error_invalid_name));
                    }
                }
            }
        });

        m_EmailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(!hasFocus){
                    if (!isEmailValid(((EditText)v).getText().toString()))
                    {
                        ((EditText)v).setError(getString(R.string.error_invalid_email));
                    }
                }
            }
        });

        m_PhoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(!hasFocus){
                    if (!isPhoneNumberValid(((EditText)v).getText().toString()))
                    {
                        ((EditText)v).setError(getString(R.string.error_invalid_phone));
                    }
                }
            }
        });

        m_DateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(!hasFocus){
                    if (!isDateValid(((EditText)v).getText().toString()))
                    {
                        ((EditText)v).setError(getString(R.string.error_invalid_date));
                    }
                }
            }
        });

        m_SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickSubmitButton();
            }
        });

        m_ImageUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnUserImageClick();
            }
        });
    }

    private void OnUserImageClick() {

        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    private void InitialiseInstances() {
        m_ImageUploadButton = findViewById(R.id.buttonUpload);
        m_FullNameEditText = findViewById(R.id.fullName);
        m_EmailEditText = findViewById(R.id.emailAddress);
        m_PhoneNumberEditText = findViewById(R.id.phoneNumber);
        m_PasswordEditText = findViewById(R.id.password);
        m_GenderRadioButton = findViewById(R.id.genderRadioGroup);
        m_DateEditText = findViewById(R.id.birthday);
        m_SubmitButton = findViewById(R.id.buttonSubmit);
    }

    private void OnClickSubmitButton(){

        // Reset errors displayed in the form.
        m_FullNameEditText.setError(null);
        m_EmailEditText.setError(null);
        m_PasswordEditText.setError(null);
        m_PhoneNumberEditText.setError(null);
        m_DateEditText.setError(null);

        // Get the texts
        String email = m_EmailEditText.getText().toString();
        String fullName = m_FullNameEditText.getText().toString();
        String password = m_PasswordEditText.getText().toString();
        String phoneNumber = m_PhoneNumberEditText.getText().toString();
        String birthday = m_DateEditText.getText().toString();

        boolean cancel1 = false;
        boolean cancel2 = false;
        boolean cancel3 = false;
        boolean cancel4 = false;
        boolean cancel5 = false;
        boolean cancel6 = false;
        boolean cancel7 = false;

        View focusView = null;

        if (!isImageUploaded)
        {
            Toast.makeText(this, "Please add user image", Toast.LENGTH_SHORT).show();
            cancel1 = true;
        }

        if (fullName.isEmpty())
        {
            Toast.makeText(this, R.string.error_invalid_name, Toast.LENGTH_SHORT);
            focusView = m_FullNameEditText;
            cancel2 = true;
        }

        if (!isPasswordValid(password))
        {
            m_PasswordEditText.setError(getString(R.string.error_invalid_register_password));
            focusView = m_PasswordEditText;
            cancel3 = true;
        }

        if (!isEmailValid(email))
        {
            m_EmailEditText.setError(getString(R.string.error_invalid_email));
            m_EmailEditText.setBackgroundColor(ContextCompat.getColor(this,R.color.colorEditTextError));
            focusView = m_EmailEditText;
            cancel4 = true;
        }

        if (!isPhoneNumberValid((phoneNumber)))
        {
            m_PhoneNumberEditText.setError(getString(R.string.error_invalid_phone));
            focusView = m_PhoneNumberEditText;
            cancel5 = true;
        }

        if (m_GenderRadioButton.getCheckedRadioButtonId() == RADIO_BUTTON_NOT_CHECKED)
        {
            Toast.makeText(this, "Gender is not checked !", Toast.LENGTH_SHORT).show();
            cancel6 = true;
        }

        if (!isDateValid(birthday))
        {
            m_DateEditText.setError(getString(R.string.error_invalid_date));
            cancel7 = true;
        }

        if (cancel1 || cancel2 || cancel3 || cancel4 || cancel5 || cancel6 || cancel7) {
            // There was an error
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            NextActivity();
        }
    }

    private boolean isDateValid(String date){
        String dateRegEx="^[0-3]{1}[0-9]{1}/[0-1]{1}[1-2]{1}/[1-9]{1}[0-9]{3}$";
        Pattern pattern = Pattern.compile(dateRegEx);
        Matcher matcher = pattern.matcher(date);

        return matcher.matches();
    }

    private boolean isEmailValid(String email) {
        return ( (email.isEmpty()) || (email.contains("@") && email.contains(".com") &&  email.length() > 10 && email.length() < 30));
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
        Intent intent = new Intent(EnterUserInfoActivity.this, DisplayUserInfoActivity.class);

        int selectedId = m_GenderRadioButton.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        intent.putExtra("userGender", radioButton.getText().toString());
        intent.putExtra("userImage", userImage);
        intent.putExtra("userEmail", m_EmailEditText.getText().toString());
        intent.putExtra("userPassword", m_PasswordEditText.getText().toString());
        intent.putExtra("userFullName", m_FullNameEditText.getText().toString());
        intent.putExtra("userPhoneNumber", m_PhoneNumberEditText.getText().toString());
        intent.putExtra("date", m_DateEditText.getText().toString());

        // TODO add bundle

        startActivity(intent, new Bundle());
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

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                userImage = BitmapFactory.decodeStream(imageStream);

                if (userImage != null){
                    isImageUploaded = true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                isImageUploaded = false;
            }

        }else {
            Toast.makeText(EnterUserInfoActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            isImageUploaded = false;

        }
    }
}
