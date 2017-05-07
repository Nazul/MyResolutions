/*
 * Copyright 2017 Mario Contreras <marioc@nazul.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mx.iteso.msc.myresolutions.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import mx.iteso.msc.myresolutions.R;
import mx.iteso.msc.myresolutions.utility.Security;

/**
 * Created by Mario_Contreras on 4/25/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // Meta
    public static final String DATABASE_NAME = "MyResolutions";
    public static final int DATABASE_VERSION = 1;
    // Tables
    public static final String TABLE_USER = "users";
    // Columns: Users
    public static final String COL_USER_EMAIL = "Email";
    public static final String COL_USER_FIRST_NAME = "FirstName";
    public static final String COL_USER_LAST_NAME = "LastName";
    public static final String COL_USER_NICK = "Nick";
    public static final String COL_USER_SYNCED = "Synced";
    // Local instance
    private static DatabaseHandler dataBaseHandler;
    // Context
    private static Context context;
    // For checking if action succeeded
    private boolean valid = false;
    private boolean success = false;
    // Temp user
    private User tmpUser;

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (dataBaseHandler == null) {
            dataBaseHandler = new DatabaseHandler(context.getApplicationContext());
        }
        return dataBaseHandler;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //// Create tables
        // Users
        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + " ("
                + COL_USER_EMAIL + " TEXT NOT NULL PRIMARY KEY, "
                + COL_USER_FIRST_NAME + " TEXT NOT NULL, "
                + COL_USER_LAST_NAME + " TEXT NOT NULL, "
                + COL_USER_NICK + " TEXT NOT NULL DEFAULT ' ', "
                + COL_USER_SYNCED + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not yet required
    }

    //region Users

    public boolean registerUser(User user, String pwdHash) {
        SyncHttpClient client = new SyncHttpClient();
        JSONObject json = new JSONObject();

        tmpUser = user;
        // Register
        try {
            json.put("Email", user.getEmail());
            json.put("Password", pwdHash);
            json.put("FirstName", user.getFirstName());
            json.put("LastName", user.getLastName());
            json.put("Nick", user.getNick());
            StringEntity entity = new StringEntity(json.toString());
            entity.setContentType(new BasicHeader("Content-Type", "application/json"));
            client.post(context, context.getResources().getString(R.string.api_url) + "/Users", entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    addUser(tmpUser);
                    success = true;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    error.printStackTrace();
                    success = false;
                }
            });
        } catch (Exception e) {
            success = false;
        }
        tmpUser = null;
        return success;
    }

    // Adding user
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_FIRST_NAME, user.getFirstName());
        values.put(COL_USER_LAST_NAME, user.getLastName());
        values.put(COL_USER_NICK, user.getNick());
        values.put(COL_USER_SYNCED, user.isSynced());

        db.insert(TABLE_USER, null, values);
        db.close();
    }

    // Getting current user
    public User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, new String[]{COL_USER_EMAIL,
                COL_USER_FIRST_NAME,
                COL_USER_LAST_NAME,
                COL_USER_NICK,
                COL_USER_SYNCED
        }, null, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3));
        user.setSynced(cursor.getInt(3) == 1);
        cursor.close();
        return user;
    }

    // Getting users Count (is logged on)
    public int getUsersCount() {
        String countQuery = "SELECT " + COL_USER_EMAIL + " FROM " + TABLE_USER + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    // Updating user
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_FIRST_NAME, user.getFirstName());
        values.put(COL_USER_LAST_NAME, user.getLastName());
        values.put(COL_USER_NICK, user.getNick());
        values.put(COL_USER_SYNCED, user.isSynced());

        return db.update(TABLE_USER, values, COL_USER_EMAIL + " = ?",
                new String[]{user.getEmail()});
    }

    // Deleting user (logoff)
    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, "", null);
        db.close();
    }

    public boolean userLogon(String email, String password) {
        AsyncHttpClient client = new SyncHttpClient();
        JSONObject json = new JSONObject();

        // Logon
        try {
            json.put("Email", email);
            json.put("Password", Security.getPasswordHash(password));
            StringEntity entity = new StringEntity(json.toString());
            entity.setContentType(new BasicHeader("Content-Type", "application/json"));
            client.post(context, context.getResources().getString(R.string.api_url) + "/Auth", entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    valid = true;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    valid = false;
                }
            });
            //client.wait();
        } catch (Exception e) {
            valid = false;
        }

        // If logon was successful, create a local copy of his profile
        if (valid) {
            try {
                client.get(context.getResources().getString(R.string.api_url) + "/Users/" + email,
                        new AsyncHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                User user = null;
                                try {
                                    user = new User(new JSONObject(new String(responseBody)));
                                    // remove previous cached profile, if exists
                                    deleteUser();
                                    // add profile
                                    addUser(user);
                                    valid = true;
                                } catch (JSONException e) {
                                    valid = false;
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                valid = false;
                            }
                        });
            } catch (Exception e) {
                valid = false;
            }
        }
        return valid;
    }
    //endregion
}

// EOF
