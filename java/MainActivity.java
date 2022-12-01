package com.example.reactive_android;
/* FIREBASE PROJECT : https://console.firebase.google.com/u/0/project/reactive-android-46869/storage/reactive-android-46869.appspot.com/files/~2F */
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Button singleViewBtn;
    Button doubleViewBtn;
    EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singleViewBtn = findViewById(R.id.single_col_view_btn);
        doubleViewBtn = findViewById(R.id.double_col_view_btn);
        searchText = findViewById(R.id.input_search);


        singleViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                String intent_text = "single" + "/" + searchText.getText().toString();
                intent.putExtra("s", intent_text);
                startActivity(intent);
            }
        });

        doubleViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                String intent_text = "double" + "/" + searchText.getText().toString();
                intent.putExtra("s", intent_text);
                startActivity(intent);
            }
        });

    }

}