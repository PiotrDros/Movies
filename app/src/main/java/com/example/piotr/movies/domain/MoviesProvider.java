package com.example.piotr.movies.domain;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.piotr.movies.domain.MoviesContract.MovieEntry;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Piotr on 2016-03-30.
 */
public class MoviesProvider extends ContentProvider {
    private MoviesDbHelper dbHelper;

    private static final Map<String, String> PROJECTION_MAP;

    static {
        PROJECTION_MAP = new HashMap<>();
        PROJECTION_MAP.put(MovieEntry._ID, MovieEntry._ID);
        PROJECTION_MAP.put(MovieEntry.COLUMN_TITLE, MovieEntry.COLUMN_TITLE);
        PROJECTION_MAP.put(MovieEntry.COLUMN_DIRECTOR, MovieEntry.COLUMN_DIRECTOR);
    }

    private static final int MOVIE_COLLECTION_CODE = 101;
    private static final int MOVIE_SINGLE_CODE = 102;

    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.MovieEntry.TABLE_NAME, MOVIE_COLLECTION_CODE);
        URI_MATCHER.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.MovieEntry.TABLE_NAME + "/#", MOVIE_SINGLE_CODE);

    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MoviesContract.MovieEntry.TABLE_NAME);
        queryBuilder.setProjectionMap(PROJECTION_MAP);
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_COLLECTION_CODE:

                break;
            case MOVIE_SINGLE_CODE:
                queryBuilder.appendWhere(MovieEntry._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Wrong code");
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, null);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);


        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_COLLECTION_CODE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_SINGLE_CODE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Wrong code");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != MOVIE_COLLECTION_CODE) {
            throw new IllegalArgumentException("Wrong code: " +URI_MATCHER.match(uri ));
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final long id = db.insert(MovieEntry.TABLE_NAME, null, values);


        if (id > 0) {
            Uri insertUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(insertUri, null);

            return  insertUri;
        } else {
            throw new RuntimeException(new SQLException("Insert failed"));
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
int count;

        switch (URI_MATCHER.match(uri)) {
            case MOVIE_COLLECTION_CODE:
             count = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_SINGLE_CODE:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + "=" +  rowId, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Wrong code");
        }


        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;

        switch (URI_MATCHER.match(uri)) {
            case MOVIE_COLLECTION_CODE:
                count = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_SINGLE_CODE:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(MovieEntry.TABLE_NAME, values, MovieEntry._ID + "=" + rowId, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Wrong code");
        }

        return count;
    }
}
