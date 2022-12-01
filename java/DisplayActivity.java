package com.example.reactive_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.operators.flowable.FlowableObserveOn;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DisplayActivity extends AppCompatActivity {

    ProgressBar progressBar;
    RecyclerView recyclerView;
    private String search_text;
    private String view;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        String[] intent_text = intent.getStringExtra("s").split("/");
        view = intent_text[0];
        search_text = intent_text[1];

        Log.d("SEARCH_TEXT:", intent_text[0] + " " + intent_text[1]);
        progressBar = findViewById(R.id.progressBar);

        /* MAKE PROGRESS BAR INVISIBLE TILL USER CLICK SEARCH IMAGE */
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.recyclerView);

        /* SET LAYOUT */
        if(intent_text[0].equals("single")){
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }else if(intent_text[0].equals("double")){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL,false));
        }

        searchImage();
    }


    public void searchImage() {

        Toast.makeText(DisplayActivity.this, "Searching starts", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);

        SearchTask searchTask = new SearchTask(DisplayActivity.this);
        searchTask.setSearchkey(search_text);

        Single<String> searchObservable = Single.fromCallable(searchTask);
        searchObservable = searchObservable.subscribeOn(Schedulers.io());
        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());
        searchObservable.subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@NonNull String s) {
                Toast.makeText(DisplayActivity.this, "Searching Ends", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                loadImage(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Toast.makeText(DisplayActivity.this, "Searching Error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }


    public void loadImage(String response) {

        ImageRetrievalTask imageRetrievalTask = new ImageRetrievalTask(DisplayActivity.this);
        imageRetrievalTask.setData(response);

        Toast.makeText(DisplayActivity.this, "Image loading starts", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);

        Single<List<Bitmap>> searchObservable = Single.fromCallable(imageRetrievalTask);
        searchObservable = searchObservable.subscribeOn(Schedulers.io());
        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());
        searchObservable.subscribe(new SingleObserver<List<Bitmap>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull List<Bitmap> bitmaps) {
                Toast.makeText(DisplayActivity.this, "Image loading Ends", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                DisplayAdapter adapter = new DisplayAdapter(bitmaps, storage, storageReference);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Toast.makeText(DisplayActivity.this, "Image loading error, search again", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}