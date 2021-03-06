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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mario_Contreras on 4/25/2017.
 */

public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String nick;
    private boolean synced;

    public User() {
    }

    public User(String email, String firstName, String lastName, String nick) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nick = nick;
    }

    public User(JSONObject user) {
        try {
            this.email = user.getString("email");
            this.firstName = user.getString("firstName");
            this.lastName = user.getString("lastName");
            this.nick = user.getString("nick");
            this.synced = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public JSONObject toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("firstName", firstName);
            json.put("lastName", lastName);
            json.put("nick", nick);
            return json;
        } catch (Exception e) {
            return null;
        }
    }
}

// EOF
