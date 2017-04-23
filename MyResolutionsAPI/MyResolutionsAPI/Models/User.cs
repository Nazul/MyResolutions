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
using Amazon.DynamoDBv2.DataModel;


namespace Iteso.MSC.MyResolutions.API.Models {
  [DynamoDBTable("MyResolutionsUsers")]
  public class User {
    public User(string email, string password, string firstName, string lastName, string nick) {
      Email = email;
      Password = password;
      FirstName = firstName;
      LastName = lastName;
      Nick = nick;
      Disabled = false;
    }

    public User() { }

    [DynamoDBHashKey]
    public string Email { get; set; }
    [DynamoDBProperty("Password")]
    public string Password { get; set; }
    [DynamoDBProperty("FirstName")]
    public string FirstName { get; set; }
    [DynamoDBProperty("LastName")]
    public string LastName { get; set; }
    [DynamoDBProperty("Nick")]
    public string Nick { get; set; }
    [DynamoDBProperty("Disabled")]
    public bool Disabled { get; set; }
  }
}

// EOF
