package com.example.piotr.movies;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.piotr.movies.domain.MoviesContract;

public class AddMovieActivity extends AppCompatActivity {

    EditText titleTextEdit;
    EditText directorTextEdit;
    Button buttonAddMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        titleTextEdit = (EditText) findViewById(R.id.title_text_edit);
        directorTextEdit = (EditText) findViewById(R.id.director_text_edit);
        buttonAddMovie = (Button) findViewById(R.id.buttonAddMovie);
        buttonAddMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovie(titleTextEdit.getText().toString(), directorTextEdit.getText().toString() );
                finish();
            }
        });

    }

    private void addMovie(String title, String director) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, title);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_DIRECTOR, director);


        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        getContentResolver().insert(uri, contentValues);



    }
}
