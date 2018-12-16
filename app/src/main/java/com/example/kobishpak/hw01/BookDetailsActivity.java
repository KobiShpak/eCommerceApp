package com.example.kobishpak.hw01;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookDetailsActivity extends AppCompatActivity {

    public final String TAG = "BookDetailsActivity";
    private Book book;
    private String key;
    private User user;

    private Button buyPlay;
    private MediaPlayer mediaPlayer;
    private RecyclerView recyclerViewBookReviews;

    private DatabaseReference bookReviewsRef;

    private List<Review> reviewsList =  new ArrayList<>();

    private boolean bookWasPurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        key = getIntent().getStringExtra("key");
        book = getIntent().getParcelableExtra("book");
        user = getIntent().getParcelableExtra("user");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        StorageReference thumbRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("thumbs/" + book.getThumbImage());

        // Load the image using Glide
        Glide.with(this)
                //.using(new FirebaseImageLoader())
                .load(thumbRef)
                .into((ImageView) findViewById(R.id.imageViewBook));

        ((TextView) findViewById(R.id.textViewName)).setText(book.getName());
        ((TextView) findViewById(R.id.textViewArtist)).setText(book.getArtist());
        ((TextView) findViewById(R.id.textViewGenre)).setText(book.getGenre());
        buyPlay = ((Button) findViewById(R.id.buttonBuyPlay));

        buyPlay.setText("BUY $" + book.getPrice());
        Iterator i = user.getMyBooks().iterator();
        while (i.hasNext()) {
            if (i.next().equals(key)) {
                bookWasPurchased = true;
                buyPlay.setText("PLAY");
                break;
            }
        }


        buyPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.e(TAG, "buyPlay.onClick() >> file=" + book.getName());

                if (bookWasPurchased) {
                    Log.e(TAG, "buyPlay.onClick() >> Playing purchased book");
                    //User purchased the book so he can play it
                    playCurrentBook(book.getFile());

                } else {
                    //Purchase the book.
                    Log.e(TAG, "buyPlay.onClick() >> Purchase the book");
                    user.getMyBooks().add(key);
                    user.upgdateTotalPurchase(book.getPrice());
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                    bookWasPurchased = true;
                    buyPlay.setText("PLAY");
                }
                Log.e(TAG, "playBook.onClick() <<");
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
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange() >> Books/" + key);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    reviewsList.add(review);
                }
                recyclerViewBookReviews.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onDataChange(Review) <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
            }
        });
        Log.e(TAG, "onCreate() <<");

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayingCurrentBook();
    }

    private void playCurrentBook(String bookFile) {

        Log.e(TAG, "playCurrentBook() >> bookFile=" + bookFile);

        if (stopPlayingCurrentBook()) {
            Log.e(TAG, "playCurrentBook() << Stop playing current book");
            return;
        }

        FirebaseStorage.getInstance()
                .getReference("books/" + bookFile)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Log.e(TAG, "onSuccess() >> " + downloadUrl.toString());

                        try {

                            mediaPlayer.setDataSource(downloadUrl.toString());
                            mediaPlayer.prepare(); // might take long! (for buffering, etc)
                            mediaPlayer.start();
                            buyPlay.setText("STOP");


                        } catch (Exception e) {
                            Log.w(TAG, "playBook() error:" + e.getMessage());
                        }

                        Log.e(TAG, "onSuccess() <<");
                    }
                });
        Log.e(TAG, "playCurrentBook() << ");
    }

    private boolean stopPlayingCurrentBook() {

        if (mediaPlayer.isPlaying()) {
            Log.e(TAG, "onSuccess() >> Stop the media player");
            //Stop the media player
            mediaPlayer.stop();
            mediaPlayer.reset();
            buyPlay.setText("PLAY");
            return true;
        }
        return false;
    }
}