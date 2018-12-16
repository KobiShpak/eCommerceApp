package com.example.kobishpak.hw01.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kobishpak.hw01.BookDetailsActivity;
import com.example.kobishpak.hw01.R;
import com.example.kobishpak.hw01.model.Book;
import com.example.kobishpak.hw01.model.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Iterator;
import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private final String TAG = "BooksAdapter";

    private List<BookWithKey> booksList;

    private User user;


    public BooksAdapter(List<BookWithKey> booksList, User user) {

        this.booksList = booksList;
        this.user = user;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e(TAG,"onCreateViewHolder() >>");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);

        Log.e(TAG,"onCreateViewHolder() <<");
        return new BookViewHolder(parent.getContext(),itemView);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {

        Log.e(TAG,"onBindViewHolder() >> " + position);

        Book book = booksList.get(position).getBook();
        String bookKey = booksList.get(position).getKey();


        StorageReference thumbRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("thumbs/"+book.getThumbImage());
        // Load the image using Glide
        Glide.with(holder.getContext())
               // .using(new FirebaseImageLoader())
                .load(thumbRef)
                .into(holder.getThumbImage());

        holder.setSelectedBook(book);
        holder.setSelectedBookKey(bookKey);
        holder.getName().setText(book.getName());
        holder.setBookFile(book.getFile());
        holder.getGenre().setText(book.getGenre());
        holder.getArtist().setText(book.getArtist());

        holder.setThumbFile(book.getThumbImage());
        if (book.getReviewsCount() >0) {
            holder.getReviewsCount().setText("("+book.getReviewsCount()+")");
            holder.getRating().setRating((float)(book.getRating() / book.getReviewsCount()));
        }
        //Check if the user already purchased the book if set the text to Play
        //If not to BUY $X
        holder.getPrice().setText("$"+book.getPrice());

        Iterator i = user.getMyBooks().iterator();
        while (i.hasNext()) {
            if (i.next().equals(bookKey)) {
                holder.getPrice().setTextColor(ContextCompat.getColor(holder.getContext(),R.color.colorPrimary));
                break;
            }
        }

        Log.e(TAG,"onBindViewHolder() << "+ position);
    }


    @Override
    public int getItemCount() {
        return booksList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {


        private CardView bookCardView;
        private ImageView thumbImage;
        private TextView name;
        private TextView artist;
        private TextView genre;
        private TextView price;
        private TextView reviewsCount;
        private String bookFile;
        private String thumbFile;
        private Context context;
        private RatingBar rating;
        private Book selectedBook;
        private String selectedBookKey;

        public BookViewHolder(Context context, View view) {

            super(view);

            bookCardView = (CardView) view.findViewById(R.id.card_view_book);
            thumbImage = (ImageView) view.findViewById(R.id.book_thumb_image);
            name = (TextView) view.findViewById(R.id.book_name);
            artist = (TextView) view.findViewById(R.id.book_reviewer_mail);
            genre = (TextView) view.findViewById(R.id.book_genre);
            price = (TextView) view.findViewById(R.id.book_price);
            reviewsCount = (TextView) view.findViewById(R.id.book_review_count);
            rating = (RatingBar) view.findViewById(R.id.book_rating);



            this.context = context;

            bookCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e(TAG, "CardView.onClick() >> name=" + selectedBook.getName());

                    Context context = view.getContext();
                    Intent intent = new Intent(context, BookDetailsActivity.class);
                    intent.putExtra("book", selectedBook);
                    intent.putExtra("key", selectedBookKey);
                    intent.putExtra("user",user);
                    context.startActivity(intent);
                }
            });
        }

        public TextView getPrice() {
            return price;
        }

        public TextView getName() {
            return name;
        }

        public ImageView getThumbImage() {
            return thumbImage;
        }

        public void setBookFile(String file) {
            this.bookFile = file;
        }

        public TextView getArtist() {
            return artist;
        }

        public TextView getGenre() {
            return genre;
        }

        public void setThumbFile(String thumbFile) {
            this.thumbFile = thumbFile;
        }

        public Context getContext() {
            return context;
        }

        public RatingBar getRating() {
            return rating;
        }

        public void setSelectedBook(Book selectedBook) {
            this.selectedBook = selectedBook;
        }

        public void setSelectedBookKey(String selectedBookKey) {
            this.selectedBookKey = selectedBookKey;
        }

        public TextView getReviewsCount() {return reviewsCount;}
    }
}
