package com.example.theodorachu.newsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;


public class ArticleClicked extends ActionBarActivity {

//    public class MyDatabaseHelper extends SQLiteOpenHelper{
//        private static final String DATABASE_NAME="Database";
//        public MyDatabaseHelper(Context context) {
//            super(context, DATABASE_NAME, null, 1);
//        }
//        @Override
//        public void onCreate(SQLiteDatabase database) {
//            database.execSQL("CREATE TABLE friends (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, vote TEXT);");
//        }
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            db.execSQL("DROP TABLE IF EXISTS friends");
//            onCreate(db);
//        }
//        public void addFriend(String url, String vote)
//        {
//            ContentValues values=new ContentValues(2);
//            values.put("name", url);
//            values.put("vote", vote);
//            getWritableDatabase().insert("friends", "name", values);
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_clicked);
        WebView webview = (WebView) findViewById(R.id.webview);
        Bundle extras = getIntent().getExtras();
        String url = null;
        int index = -1;
        if (extras != null) {
            url = extras.getString("url");
            index = extras.getInt("index");
        }
        webview.loadUrl(url);

        final String setUrl = url;
        final int ind = index;
        ImageButton left = (ImageButton) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.buttons.get(ind).setBackgroundColor(Color.BLUE);
            }
        });
        ImageButton up = (ImageButton) findViewById(R.id.up);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.buttons.get(ind).setBackgroundColor(Color.BLACK);
            }
        });
        ImageButton right = (ImageButton) findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.buttons.get(ind).setBackgroundColor(Color.RED);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article_clicked, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
