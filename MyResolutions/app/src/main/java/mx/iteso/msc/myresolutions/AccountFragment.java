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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import mx.iteso.msc.myresolutions.dataaccess.User;
import mx.iteso.msc.myresolutions.utility.ImagePicker;

/**
 * Created by Mario_Contreras on 5/2/2017.
 */

public class AccountFragment extends Fragment {
    private View mProgressView;
    private View mAccountFormView;
    ImageView ivUserPhoto;
    TextView tvEmail;
    EditText etPassword;
    EditText etConfirm;
    EditText etFirstName;
    EditText etLastName;
    EditText etNick;
    Bitmap tmpUserPhoto;
    User currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        Bundle bundle = getArguments();
        try {
            currentUser = new User(new JSONObject(bundle.getString("loggedUser")));
        }
        catch (Exception e) {
            currentUser = null;
        }

        mAccountFormView = view.findViewById(R.id.svAccountForm);
        mProgressView = view.findViewById(R.id.bpSaveProgress);
        ivUserPhoto = (ImageView) view.findViewById(R.id.ivUserPhoto);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        etConfirm = (EditText) view.findViewById(R.id.etConfirm);
        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        etLastName = (EditText) view.findViewById(R.id.etLastName);
        etNick = (EditText) view.findViewById(R.id.etNick);
        tmpUserPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.default_user_photo);

        tvEmail.setText(currentUser.getEmail());
        tvEmail.setEnabled(false);

        etFirstName.setText(currentUser.getFirstName());
        etLastName.setText(currentUser.getLastName());
        etNick.setText(currentUser.getNick());

        ImagePicker.setMinQuality(480, 480);

        return view;
    }

    public void ChangePhoto(View v) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            ImagePicker.pickImage(this, "Select your image:");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(getContext(), requestCode, resultCode, data);
        if (bitmap != null) {
            tmpUserPhoto = bitmap;
            ivUserPhoto.setImageBitmap(bitmap);
        }
    }
}

// EOF
