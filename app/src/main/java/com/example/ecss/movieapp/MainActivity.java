package com.example.ecss.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements TabletUIHandler {
    boolean panes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        panes = findViewById(R.id.details) != null;
        MainActivityFragment fragment =
                (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void SetFilmName(MovieDetail film) {
        if (panes) {

            MovieFragment fragment = new MovieFragment();
            Bundle args = new Bundle();
            args.putSerializable("film", film);
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details, fragment, MovieFragment.class.getSimpleName())
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieActivity.class);
            intent.putExtra("film", film);
            startActivity(intent);
        }
    }
}
