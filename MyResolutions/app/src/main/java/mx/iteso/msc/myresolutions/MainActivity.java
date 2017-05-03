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

package mx.iteso.msc.myresolutions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import mx.iteso.msc.myresolutions.dataaccess.DatabaseHandler;
import mx.iteso.msc.myresolutions.dataaccess.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tvUser;
    private TextView tvUserEmail;
    private ImageView ivUserPhoto;
    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        tvUser = (TextView) hView.findViewById(R.id.tvUser);
        tvUserEmail = (TextView) hView.findViewById(R.id.tvUserEmail);
        ivUserPhoto = (ImageView) hView.findViewById(R.id.ivUserPhoto);
    }

    private void updateUI() {
        // Check if there is already a session
        if (DatabaseHandler.getInstance(getApplicationContext()).getUsersCount() > 0) {
            loggedUser = DatabaseHandler.getInstance(getApplicationContext()).getUser();
            if (loggedUser.getNick().equals(" ")) {
                tvUser.setText(loggedUser.getFirstName() + " " + loggedUser.getLastName());
            } else {
                tvUser.setText(loggedUser.getNick());
            }
            tvUserEmail.setText(loggedUser.getEmail());
            try {
                File image = new File(getApplicationContext().getFilesDir(), loggedUser.getEmail() + ".png");
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                ivUserPhoto.setImageBitmap(bitmap);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

//    public void LoginScreen(View v) {
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//    }

//    public void Logout(View v) {
//        DatabaseHandler.getInstance(getApplicationContext()).deleteUser();
//        updateUI();
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_logout) {
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_assignment) {
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_sync) {
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_login) {
            DatabaseHandler.getInstance(getApplicationContext()).deleteUser();
            updateUI();
        } else if (id == R.id.nav_logout) {
            DatabaseHandler.getInstance(getApplicationContext()).deleteUser();
            updateUI();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
// EOF
