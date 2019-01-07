package com.example.kobishpak.hw01.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    private String email;
    private int totalPurchase;
    private int booksCount;
    private List<String> myBooks = new ArrayList<>();
    private String signupMethod;

    public User() {
        this.email = "";
    }

    public User(String email, int booksCount, int totalPurchase, List<String> myBooks, String signupMethod) {
        this.email = email;
        this.totalPurchase = totalPurchase;
        this.booksCount = booksCount;
        this.myBooks = myBooks;
        this.signupMethod = signupMethod;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getMyBooks() {
        return myBooks;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeList(myBooks);
    }

    public User(Parcel in) {
        this.email = in.readString();
        in.readList(myBooks,String.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void updatePurchaseStatus(int purchaseFee)
    {
        this.booksCount++;
        this.totalPurchase += purchaseFee;
    }

    public int getTotalPurchase() {
        return this.totalPurchase;
    }

    public int getMyBooksCount() {
        return this.booksCount;
    }
}
