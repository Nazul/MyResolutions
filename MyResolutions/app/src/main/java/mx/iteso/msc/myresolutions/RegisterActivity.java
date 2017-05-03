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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import mx.iteso.msc.myresolutions.dataaccess.DatabaseHandler;
import mx.iteso.msc.myresolutions.dataaccess.User;
import mx.iteso.msc.myresolutions.mx.iteso.msc.myresolutions.utility.CloudStorage;
import mx.iteso.msc.myresolutions.mx.iteso.msc.myresolutions.utility.ImagePicker;
import mx.iteso.msc.myresolutions.mx.iteso.msc.myresolutions.utility.Security;

/**
 * Created by Mario_Contreras on 4/30/2017.
 */

public class RegisterActivity extends AppCompatActivity {
    private View mProgressView;
    private View mRegisterFormView;
    ImageView ivUserPhoto;
    EditText etEmail;
    EditText etPassword;
    EditText etConfirm;
    EditText etFirstName;
    EditText etLastName;
    EditText etNick;
    RegisterUserTask mRegTask;
    Bitmap tmpUserPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImagePicker.setMinQuality(480, 480);

        mRegisterFormView = findViewById(R.id.svRegisterForm);
        mProgressView = findViewById(R.id.bpRegisterProgress);
        ivUserPhoto = (ImageView) findViewById(R.id.ivUserPhoto);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirm = (EditText) findViewById(R.id.etConfirm);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etNick = (EditText) findViewById(R.id.etNick);
        tmpUserPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.default_user_photo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        if (bitmap != null) {
            tmpUserPhoto = bitmap;
            ivUserPhoto.setImageBitmap(bitmap);
        }
    }

    public void ChangePhoto(View v) {
        ImagePicker.pickImage(this, "Select your image:");
    }

    public void Register(View v) {
        if (mRegTask != null) {
            return;
        }

        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirm.setError(null);
        etFirstName.setError(null);
        etLastName.setError(null);

        // Store values
        User user = new User(etEmail.getText().toString(), etFirstName.getText().toString(), etLastName.getText().toString(), etNick.getText().toString());
        String password = etPassword.getText().toString();
        String pwdConfirm = etConfirm.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(user.getEmail())) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(user.getEmail())) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(user.getFirstName())) {
            etFirstName.setError(getString(R.string.error_field_required));
            focusView = etFirstName;
            cancel = true;
        }

        if (TextUtils.isEmpty(user.getLastName())) {
            etLastName.setError(getString(R.string.error_field_required));
            focusView = etLastName;
            cancel = true;
        }

        if (!password.equals(pwdConfirm)) {
            etConfirm.setError(getString(R.string.error_invalid_password));
            focusView = etConfirm;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task
            showProgress(true);
            mRegTask = new RegisterActivity.RegisterUserTask(this, user, password);
            mRegTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class RegisterUserTask extends AsyncTask<Void, Void, Boolean> {
        private final AppCompatActivity mActivity;
        private final User mUser;
        private final String mPwdHash;

        RegisterUserTask(AppCompatActivity activity, User user, String password) {
            mActivity = activity;
            mUser = user;
            mPwdHash = Security.getPasswordHash(password);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            savePhoto();
            return DatabaseHandler.getInstance(getApplicationContext()).registerUser(mUser, mPwdHash);
        }

        // From:
        // http://stackoverflow.com/questions/649154/save-bitmap-to-location
        private void savePhoto() {
            // Local copy
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(getApplicationContext().getFilesDir() + "/" + etEmail.getText().toString() + ".png");
                tmpUserPhoto.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Save to AWS S3
            AmazonS3 client = CloudStorage.getS3Client();
            TransferUtility transferUtility = new TransferUtility(client, getApplicationContext());
            TransferObserver observer = transferUtility.upload(
                    "myresolutions", /* The bucket to upload to */
                    etEmail.getText().toString() + ".png", /* The key for the uploaded object */
                    new File(getApplicationContext().getFilesDir(), etEmail.getText().toString() + ".png") /* The file where the data to upload exists */
            );
            do {
                observer.refresh();
            } while (TransferState.WAITING.equals(observer.getState())
                    || TransferState.WAITING_FOR_NETWORK.equals(observer.getState())
                    || TransferState.IN_PROGRESS.equals(observer.getState()));
            observer.getState();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                etEmail.setError("Something went wrong");
                etEmail.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mRegTask = null;
            showProgress(false);
        }
    }
}

// EOF
