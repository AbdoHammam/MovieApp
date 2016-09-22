package com.example.ecss.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class MovieFragment extends Fragment {
    private String title, url, date, vote, id, overview;
    TrailerAdapter traileradapt;
    String copy[];
    MovieDetail movie_details;
    String keys[];
    ListView listView_trailers;
    ListView listView_reviews;
    ReviewAdapter reviewadapt;
    int watches;


    public MovieFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_movie, container, false);

        listView_trailers = (ListView) rootview.findViewById(R.id.listview_trailer);

        listView_trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + copy[position]));
                startActivity(intent);
            }
        });

        listView_reviews = (ListView) rootview.findViewById(R.id.listview_review);

        TextView textView;
        /*TextView textView = (TextView) rootview.findViewById(R.id.movieTitle);
        title = getActivity().getIntent().getStringExtra("title");
        textView.setText(title);*/
        ImageView imageView = (ImageView) rootview.findViewById(R.id.movieImage);

        movie_details = (MovieDetail) getArguments().getSerializable("film");

        url = movie_details.getPoster_path();
        Picasso.with(getActivity()).load(url).resize(250, 320).into(imageView);

        date = movie_details.getRelease_date();
        textView = (TextView) rootview.findViewById(R.id.releaseDate);
        textView.setText(date);

        title = movie_details.getTitle();

        vote = movie_details.getVote_average();
        textView = (TextView) rootview.findViewById(R.id.averageRate);
        textView.setText(vote);

        overview = movie_details.getOverview();
        textView = (TextView) rootview.findViewById(R.id.overview);
        textView.setText(overview);

        id = movie_details.getMovie_id();

        Button addToFavorites = (Button) rootview.findViewById(R.id.addToFavourites);
        assert addToFavorites != null;

        addToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseHelper db = new DatabaseHelper(getActivity());
                ArrayList<MovieDetail> arrayList = db.getFavorites();

                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getMovie_id().equals(id)) {
                        Toast.makeText(getActivity(), "Movie is already marked", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                db.InsertIntoTable(new MovieDetail(overview, date, id, url, title, vote));
                Toast.makeText(getActivity(), "Movie added to Favourite", Toast.LENGTH_SHORT).show();
            }
        });
        return rootview;
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

    private void fetchTrailers() {
        FetchTrailer fetchTrailer = new FetchTrailer();
        fetchTrailer.execute();
    }

    private void fetchReviews() {
        FetchReviews fetchReviews = new FetchReviews();
        fetchReviews.execute();
    }

    public void onStart() {
        super.onStart();
        fetchTrailers();
        fetchReviews();
    }


    public class FetchTrailer extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchTrailer.class.getSimpleName();

        public FetchTrailer() {

        }

        private String[] MoviesJasonPrase(String moviesPosterStr) throws JSONException {


            JSONObject moviesJson = new JSONObject(moviesPosterStr);
            JSONArray resultsArray = moviesJson.getJSONArray("results");
            keys = new String[resultsArray.length()];
            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject movies = resultsArray.getJSONObject(i);
                keys[i] = movies.getString("key");
            }

            return keys;
        }


        @Override
        protected String[] doInBackground(String... params) {
// These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            //add api key
            final String API_key = "";
            final String API_param = "api_key";
            //  final String link = "http://api.themoviedb.org/3/movie/" + movie_details.movie_id + "?";
// Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            final Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").authority("api.themoviedb.org").appendPath("3")
                    .appendPath("movie").appendPath(id).appendPath("videos")
                    .appendQueryParameter("api_key", "");

            try {

                URL url = new URL(builder.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return MoviesJasonPrase(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(null);
            copy = new String[result.length];
            if (result != null) {
                for (int i = 0; i < result.length; i++) {
                    copy[i] = result[i];
                }
                traileradapt = new TrailerAdapter(getActivity(), result);
                listView_trailers.setAdapter(traileradapt);


            }
        }
    }

    public class FetchReviews extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchTrailer.class.getSimpleName();


        public FetchReviews() {

        }

        private String[] MoviesJasonPrase(String moviesPosterStr) throws JSONException {


            JSONObject moviesJson = new JSONObject(moviesPosterStr);
            JSONArray resultsArray = moviesJson.getJSONArray("results");
            keys = new String[resultsArray.length()];
            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject movies = resultsArray.getJSONObject(i);
                keys[i] = movies.getString("content");
            }

            return keys;
        }


        @Override
        protected String[] doInBackground(String... params) {
// These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            //add api key
            final String API_key = "";
            final String API_param = "api_key";
            //  final String link = "http://api.themoviedb.org/3/movie/" + movie_details.movie_id + "?";
// Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {

                final Uri.Builder builder = new Uri.Builder();
                builder.scheme("https").authority("api.themoviedb.org").appendPath("3")
                        .appendPath("movie").appendPath(id).appendPath("reviews")
                        .appendQueryParameter("api_key", "");
                URL url = new URL(builder.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return MoviesJasonPrase(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (final RuntimeException i) {
                i.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(null);
            String arr[] = new String[result.length];
            if (result != null) {
                reviewadapt = new ReviewAdapter(getActivity(), result);
                listView_reviews.setAdapter(reviewadapt);
            }
        }
    }
}
