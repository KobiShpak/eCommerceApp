package com.example.kobishpak.hw01;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;

import android.content.pm.PackageManager;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kobishpak.hw01.adapter.ReviewsAdapter;
import com.example.kobishpak.hw01.model.Book;
import com.example.kobishpak.hw01.model.Review;
import com.example.kobishpak.hw01.model.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookDetailsActivity extends AppCompatActivity {

    public final String TAG = "BookDetailsActivity";
    private Book book;
    private String key;
    private User user;

    private Button buy;
    private RecyclerView recyclerViewBookReviews;

    private DatabaseReference bookReviewsRef;
    private StorageReference mBookStorage;
    private List<Review> reviewsList = new ArrayList<>();

    private boolean bookWasPurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        key = getIntent().getStringExtra("key");
        book = getIntent().getParcelableExtra("book");
        user = getIntent().getParcelableExtra("user");
        mBookStorage = FirebaseStorage.getInstance().getReference().child(("Books/"));

        StorageReference thumbRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("Thumbs/" + book.getThumbImage());

        thumbRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                // Load the image using Glide
                Glide.with(BookDetailsActivity.this)
                        .load(uri)
                        .into((ImageView) findViewById(R.id.imageViewBook));
                Log.e(TAG, "DownloadCurrentBook() << SUCCESS");
            }
        });

        ((TextView) findViewById(R.id.textViewName)).setText(book.getName());
        ((TextView) findViewById(R.id.textViewArtist)).setText(book.getArtist());
        ((TextView) findViewById(R.id.textViewGenre)).setText(book.getGenre());
        buy = findViewById(R.id.buttonBuy);

        buy.setText("BUY $" + book.getPrice());
        Iterator i = user.getMyBooks().iterator();
        while (i.hasNext()) {
            if (i.next().equals(key)) {
                bookWasPurchased = true;
                buy.setText(R.string.download);
                break;
            }
        }

        buy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.e(TAG, "buy.onClick() >> file=" + book.getName());

                if (bookWasPurchased) {
                    Log.e(TAG, "buy.onClick() >> Downloading purchased book");
                    //User purchased the book so he can download it
                    downloadCurrentBook(book.getFile());

                } else {
                    //Purchase the book.
                    Log.e(TAG, "buy.onClick() >> Purchase the book");
                    user.getMyBooks().add(key);
                    user.upgdateTotalPurchase(book.getPrice());
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                    bookWasPurchased = true;
                    buy.setText(R.string.download);
                }
                Log.e(TAG, "DownloadBook.onClick() <<");
            }
        });

        recyclerViewBookReviews = findViewById(R.id.book_reviews);
        recyclerViewBookReviews.setHasFixedSize(true);
        recyclerViewBookReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewBookReviews.setItemAnimator(new DefaultItemAnimator());


        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviewsList);
        recyclerViewBookReviews.setAdapter(reviewsAdapter);

        bookReviewsRef = FirebaseDatabase.getInstance().getReference("Books/" + key +"/reviews");

        bookReviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange() >> Books/" + key);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    reviewsList.add(review);
                }
                recyclerViewBookReviews.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onDataChange(Review) <<");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
            }
        });
        Log.e(TAG, "onCreate() <<");

    }

    private void downloadCurrentBook(String bookFile) {

        StorageReference BookReference = mBookStorage.child(bookFile);
        Log.e(TAG, "DownloadCurrentBook() >> bookFile=" + BookReference);

        final String bookName = bookFile;
        BookReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                saveFile(uri, bookName);
                Log.e(TAG, "DownloadCurrentBook() << SUCCESS");
            }
        });
    }

    void saveFile(Uri uri, String bookFile)
    {
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                DownloadManager.Request.NETWORK_MOBILE);
        request.allowScanningByMediaScanner();
        request.setTitle("Book");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, bookFile);
        request.setMimeType("*/*");

        downloadmanager.enqueue(request);
    }
}