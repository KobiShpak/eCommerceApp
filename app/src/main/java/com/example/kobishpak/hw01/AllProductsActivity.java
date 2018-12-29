package com.example.kobishpak.hw01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kobishpak.hw01.adapter.BookWithKey;
import com.example.kobishpak.hw01.adapter.BooksAdapter;
import com.example.kobishpak.hw01.model.Book;
import com.example.kobishpak.hw01.model.User;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AllProductsActivity extends AppCompatActivity {

    private static final String TAG = "DisplayBooks";
    private ImageView m_UserImageView;
    private TextView m_UserInfoTextView;
    private EditText m_SearchEditText;
    private TextView m_LogoutTextView;
    private Switch mHidePurchasedSwitch;
    private DatabaseReference allBooksRef;
    private DatabaseReference myUserRef;
    private List<BookWithKey> booksList = new ArrayList<>();

    private RecyclerView recyclerView;
    private BooksAdapter booksAdapter;
    private User myUser;
    private FirebaseAuth m_FirebaseAuth;
    private FirebaseUser m_FirebaseUser;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressDialog progressDialog;
    private DrawerLayout m_DrawerLayout;
    private NavigationView m_NavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);
        Log.e(TAG, getString(R.string.display_on_create));

        initializeInstances();

        m_NavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                m_DrawerLayout.closeDrawers();

                return true;
            }
        });
        m_SearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showFilteredBooks();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        m_FirebaseAuth = FirebaseAuth.getInstance();
        m_FirebaseUser = m_FirebaseAuth.getCurrentUser();

        if (m_FirebaseUser == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else {
            myUserRef = FirebaseDatabase.getInstance().getReference("Users/" + m_FirebaseUser.getUid());
            pleaseWait();
            DisplayUserInformation();
            if (m_FirebaseUser.getDisplayName() != null) {
                myUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.e(TAG, "onDataChange(User) >> " + snapshot.getKey());

                        myUser = snapshot.getValue(User.class);

                        getAllBooks();

                        Log.e(TAG, "onDataChange(User) <<");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Log.e(TAG, "onCancelled(Users) >>" + databaseError.getMessage());
                    }
                });
            }
            else {
                myUser = new User();
                getAllBooks();
            }
        }

        mHidePurchasedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                getAllBooks();
            }
        });
    }

    private void showFilteredBooks()
    {
        String searchString = ((EditText)findViewById(R.id.edit_text_search_book)).getText().toString();
        String orderBy = ((RadioButton)findViewById(R.id.radioButtonByRating)).isChecked() ? "rating" : "price";
        Query searchBook;

        Log.e(TAG, "onSearchTextChange() >> searchString="+searchString+ ",orderBy="+orderBy);

        booksList.clear();

        if (!searchString.isEmpty()) {
            searchBook = allBooksRef.orderByChild("name").startAt(searchString).endAt(searchString + "\uf8ff");
        } else {
            searchBook = allBooksRef.orderByChild(orderBy);
        }


        searchBook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange(Query) >> " + snapshot.getKey());

                if (mHidePurchasedSwitch.isChecked()) {
                    hidePurchesdBooks(snapshot);
                } else {
                    updateBooksList(snapshot);
                }

                Log.e(TAG, "onDataChange(Query) <<");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG, "onCancelled() >>" + databaseError.getMessage());
            }

        });
        Log.e(TAG, "onSearchTextChange() <<");

    }

    private void getAllBooks() {
        booksList.clear();
        booksAdapter = new BooksAdapter(booksList,myUser);
        recyclerView.setAdapter(booksAdapter);

        getAllBooksUsingValueListenrs();
    }

    private void getAllBooksUsingValueListenrs() {

        allBooksRef = FirebaseDatabase.getInstance().getReference("Books");
        Query searchBook = allBooksRef.orderByChild("price");
        searchBook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange(Books) >> " + snapshot.getKey());

                if (mHidePurchasedSwitch.isChecked()) {
                    hidePurchesdBooks(snapshot);
                } else {
                    updateBooksList(snapshot);
                }

                Log.e(TAG, "onDataChange(Books) <<");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Books) >>" + databaseError.getMessage());
            }
        });
    }

    private void updateBooksList(DataSnapshot snapshot) {

        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Book book = dataSnapshot.getValue(Book.class);
            Log.e(TAG, "updateBookList() >> adding book: " + book.getName());
            String key = dataSnapshot.getKey();

            booksList.add(new BookWithKey(key, book));
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void hidePurchesdBooks(DataSnapshot snapshot) {

        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Book book = dataSnapshot.getValue(Book.class);
            Log.e(TAG, "updateBookList() >> adding book: " + book.getName());
            String key = dataSnapshot.getKey();

            List<String> listOfUserBooks = myUser.getMyBooks();

            for (String list:listOfUserBooks) {

                if (list.equals(key)) {
                    booksList.add(new BookWithKey(key, book));
                }
            }
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void onRadioButtonCLick(View v) {
        switch (v.getId()) {
            case R.id.radioButtonByPrice:
                ((RadioButton)findViewById(R.id.radioButtonByRating)).setChecked(false);
                break;
            case R.id.radioButtonByRating:
                ((RadioButton)findViewById(R.id.radioButtonByPrice)).setChecked(false);
                break;
        }
        showFilteredBooks();
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_portrait_black_24dp);
        m_UserInfoTextView = findViewById(R.id.textViewUserInfo);
        m_UserImageView = findViewById(R.id.userImageView);
        m_SearchEditText = findViewById(R.id.edit_text_search_book);
        m_DrawerLayout = findViewById(R.id.drawer_layout);
        m_NavigationView = findViewById(R.id.navigation_view);
        m_LogoutTextView = findViewById(R.id.logout);
        recyclerView = findViewById(R.id.books_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mHidePurchasedSwitch = findViewById(R.id.hidePurchasedSwitch);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                m_DrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onClickLogOutButton() {
        m_FirebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        startActivity(new Intent(AllProductsActivity.this, LoginActivity.class));
    }

    private void DisplayUserInformation() {
        if(m_FirebaseUser.getPhotoUrl() != null) {
            Glide.with(AllProductsActivity.this).load(m_FirebaseUser.getPhotoUrl()).into(m_UserImageView);
        }
        else {
            m_UserImageView.setImageResource(R.drawable.anonymous_user_image);
        }

        String userText;
        userText = m_FirebaseUser.getDisplayName() == null ?
        "Hello" :
        String.format("Hello %s", m_FirebaseUser.getDisplayName()) ;

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
