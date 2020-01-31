package com.android.chatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        toolbar = findViewById(R.id.toolbarresult);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Toast.makeText(this, "Result Activity", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = new MenuInflater(ResultActivity.this);
        menuInflater.inflate(R.menu.main_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         switch (item.getItemId()){
             case  R.id.about:
                 Toast.makeText(this, "About Selected", Toast.LENGTH_SHORT).show();
                 break;
             case R.id.settings:
                 Toast.makeText(this, "Settings Selected", Toast.LENGTH_SHORT).show();
                 break;
             case R.id.share:
                 Toast.makeText(this, "Contact Selected", Toast.LENGTH_SHORT).show();
                 break;
             case R.id.help:

                 Toast.makeText(this, "Help Selected", Toast.LENGTH_SHORT).show();

                 break;

             default:
                 return  false;

         }
        return true;

    }

}
