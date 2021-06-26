package com.example.flixster.models;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.MovieTrailerActivity;
import com.example.flixster.R;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    // movie being displayed
    Movie movie;
    String url;
    String TAG = "MovieDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());

        // Layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s", movie.getTitle()));

        url = "https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

        // set the title and overview
        binding.tvTitle.setText(movie.getTitle());
        binding.tvOverview.setText(movie.getOverview());


        String imageUrl;
        int placeholderOrientation;
        // if phone is in landscape
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // then imageUrl = back drop image
            imageUrl = movie.getPosterPath();
            placeholderOrientation = R.mipmap.placeholder_foreground;
        } else {
            // else imageUrl = poster image
//            imageUrl = movie.getBackdropPath();
            imageUrl = movie.getPosterPath();
            placeholderOrientation = R.mipmap.backdrop_placeholder_foreground;

        }

//        int radius = 30;
//        int margin = 10;

        Glide.with(this)
                .load(imageUrl)
                .placeholder(placeholderOrientation)
//                .centerCrop()
//                .transform(new RoundedCornersTransformation(radius, margin))
                .into(binding.ivPoster);


        // vote average is 0...10,
        float voteAverage = movie.getVoteAverage().floatValue();
        binding.rbVoteAverage.setRating(voteAverage / 2.0f);

        binding.ivPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d(TAG, "onSuccess");
                        Log.d(TAG, json.toString());
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            String youtube_key = jsonArray.getJSONObject(0).getString("key");
                            Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                            intent.putExtra("key", youtube_key);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d(TAG, "onFailure");
                    }
                });
            }
        });

    }
}