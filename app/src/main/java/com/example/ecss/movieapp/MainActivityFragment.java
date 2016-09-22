package com.example.ecss.movieapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    ArrayList<MovieDetail> data = null;
    String choice = "popular";
    MovieDetail[] items;
    GridView gridView;
    imageButtonAdapter image_adapter;
    TabletUIHandler handler;
    int watches;

    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootview.findViewById(R.id.moviesGrid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                final MovieDetail movie = image_adapter.getItem(position);
                ((TabletUIHandler) getActivity()).SetFilmName(movie);

            }

        });
        return rootview;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MovieFetch task = new MovieFetch();

        if (item.getItemId() == R.id.mostPopular) {
            choice = "popular";
            task.execute("http://api.themoviedb.org/3/movie/" + choice + "?api_key=");

        } else if (item.getItemId() == R.id.highestRated) {
            choice = "top_rated";
            task.execute("http://api.themoviedb.org/3/movie/" + choice + "?api_key=");
        } else if (item.getItemId() == R.id.favourites) {

            DatabaseHelper db = new DatabaseHelper(getActivity());
            data = db.getFavorites();
            if (data != null) {
                image_adapter = new imageButtonAdapter(getActivity(), data);
                gridView.setAdapter(image_adapter);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();

        MovieFetch task = new MovieFetch();
        task.execute("http://api.themoviedb.org/3/movie/" + choice + "?api_key=");
    }

    @Override
    public void onResume() {
        super.onResume();

        watches++;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("watch", watches);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            watches = savedInstanceState.getInt("watch");
        }
    }

    public class MovieFetch extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            String moviePath = voids[0];
            String jsonString = null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            try {

                URL url = new URL(moviePath);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonString = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return jsonString;
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String json) {
            try {
                MoviesJsonParse(json);
                if (items != null) {
                    image_adapter = new imageButtonAdapter(getActivity(), Arrays.asList(items));
                    gridView.setAdapter(image_adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void MoviesJsonParse(String moviesPosterStr) throws JSONException {
            if (moviesPosterStr == null) return;
            JSONObject moviesJson = new JSONObject(moviesPosterStr);
            JSONArray resultsArray = moviesJson.getJSONArray("results");
            items = new MovieDetail[resultsArray.length()];

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject movies = resultsArray.getJSONObject(i);
                items[i] = new MovieDetail(movies.getString("overview"), movies.getString("release_date"), movies.getString("id"), "http://image.tmdb.org/t/p/w500/" + movies.getString("poster_path"), movies.getString("original_title"), movies.getString("vote_average"));
            }
        }
    }
}