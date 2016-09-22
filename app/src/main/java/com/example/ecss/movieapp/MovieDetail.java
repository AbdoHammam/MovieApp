package com.example.ecss.movieapp;

import java.io.Serializable;

public class MovieDetail implements Serializable{
    private String overview;
    private String release_date;
    private String movie_id;
    private String poster_path;
    private String title;
    private String vote_average;
    public MovieDetail(){

    }
    public MovieDetail(String overview, String release_date, String movie_id, String poster_path, String title, String vote_average) {
        this.overview = overview;
        this.release_date = release_date;
        this.movie_id = movie_id;
        this.poster_path = poster_path;
        this.title = title;
        this.vote_average = vote_average;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }
}
