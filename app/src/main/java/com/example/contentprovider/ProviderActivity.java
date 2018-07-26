package com.example.contentprovider;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.administrator.myaidltest.R;
import com.example.contentprovider.bean.Book;
import com.example.contentprovider.bean.User;

import java.net.URI;

public class ProviderActivity extends AppCompatActivity {
    private static final String TAG = "ProviderActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        Uri bookUri = Uri.parse("content://com.example.contentprovider.BookProvider/book");
        Uri userUri = Uri.parse("content://com.example.contentprovider.BookProvider/user");
        ContentValues values = new ContentValues();
        values.put("_id", 6);
        values.put("name", "程序设计的艺术");
        getContentResolver().insert(bookUri, values);

        Cursor bookCursor = getContentResolver().query(bookUri, new String[]{"_id", "name"}, null, null, null);
        while (bookCursor.moveToNext()){
            Book book = new Book();
            book.set_id(bookCursor.getInt(0));
            book.setName(bookCursor.getString(1));
            Log.e(TAG, "onCreate: " + book.toString());
        }
        bookCursor.close();

        Cursor userCursor = getContentResolver().query(userUri, new String[]{"_id", "name", "sex"}, null, null, null);
        while (userCursor.moveToNext()){
            User user = new User();
            user.set_id(userCursor.getInt(0));
            user.setName(userCursor.getString(1));
            user.setSex(userCursor.getInt(2));
            Log.e(TAG, "onCreate: " + user);
        }
        userCursor.close();
    }
}
