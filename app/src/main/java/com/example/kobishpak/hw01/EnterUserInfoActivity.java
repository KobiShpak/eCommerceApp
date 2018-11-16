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
import android.widget.ImageView;
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
    private ImageView m_UserImageView;
    private Uri m_ImageUri;

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

        m_PasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(!hasFocus){
                    if (!isPasswordValid(((EditText)v).getText().toString()))
                    {
                        ((EditText)v).setError(getString(R.string.error_invalid_register_password));
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
        m_UserImageView = findViewById(R.id.userImageView);
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
        boolean cancel8 = false;

        View focusView = null;

        if (!isImageUploaded)
        {
            Toast.makeText(this, "Please add user image", Toast.LENGTH_SHORT).show();
            cancel1 = true;
        }
        else
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(m_ImageUri.toString(), options);
            if (options.outWidth != -1 && options.outHeight != -1) {
                // This is an image file.
            }
            else {
                Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
                cancel8 = true;
            }
        }

        if (!isFullNameValid(fullName))
        {
            m_FullNameEditText.setError(getString(R.string.error_invalid_name));
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
            focusView = m_DateEditText;
            cancel7 = true;
        }

        if (cancel1 || cancel2 || cancel3 || cancel4 || cancel5 || cancel6 || cancel7 || cancel8) {
            // There was an error
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            NextActivity();
        }
    }

    private boolean isFullNameValid(String fullName) {
        // Full name must contain letters only
        String regEx="^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(fullName);

        return matcher.matches();
    }

    private boolean isDateValid(String date){
        // DD/MM/YYYY
        String regEx="^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(date);

        return matcher.matches();
    }

    private boolean isEmailValid(String email) {
        String regEx="^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        // Password must be between 4 and 8 digits long and include at least one numeric digit.
        String regEx="^(?=.*[0-9]).{4,8}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        // Matches: (123) 456 7899
        //(123).456.7899
        //(123)-456-7899
        //123-456-7899
        //123 456 7899
        //1234567899
        String regEx="^\\(?([0-9]{3})\\)?([ .-]?)([0-9]{3})\\2([0-9]{4})$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }

    private void NextActivity(){
        Intent intent = new Intent(EnterUserInfoActivity.this, DisplayUserInfoActivity.class);

        int selectedId = m_GenderRadioButton.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        intent.putExtra("userGender", radioButton.getText().toString());
        intent.putExtra("userImage", m_ImageUri.toString());
        intent.putExtra("userEmail", m_EmailEditText.getText().toString());
        intent.putExtra("userPassword", m_PasswordEditText.getText().toString());
        intent.putExtra("userFullName", m_FullNameEditText.getText().toString());
        intent.putExtra("userPhoneNumber", m_PhoneNumberEditText.getText().toString());
        intent.putExtra("date", m_DateEditText.getText().toString());

        // TODO add bundle

        startActivity(intent, new Bundle());
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
                m_ImageUri = data.getData();
                assert m_ImageUri != null;
                InputStream imageStream = getContentResolver().openInputStream(m_ImageUri);// not final
                m_UserImageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));

                if (m_UserImageView != null){
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
