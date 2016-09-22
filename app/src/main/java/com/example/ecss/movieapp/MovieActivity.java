package com.example.ecss.movieapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieActivity extends AppCompatActivity {

    MovieDetail film;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        if (null == savedInstanceState) {
            Bundle extras = getIntent().getExtras();

            film = (MovieDetail) getIntent().getSerializableExtra("film");

            MovieFragment fragment = new MovieFragment();
            fragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().add(R.id.cont, fragment)
                    .commit();

        }


    }
}
