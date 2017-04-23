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
using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Iteso.MSC.MyResolutions.API.Models;
using System;
using System.Threading.Tasks;


namespace Iteso.MSC.MyResolutions.API.App_Code {
  public static class UserDataAccess {
    private static AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(RegionEndpoint.USEast1);
    private static DynamoDBContext context = new DynamoDBContext(dbClient);

    static UserDataAccess() { }

    public static bool Add(User user) {
      try {
        Task task = context.SaveAsync<User>(user);
        task.Wait();
        return task.IsCompleted;
      }
      catch (Exception) {
        return false;
      }
    }

    public static User GetByEmail(string email) {
      Task<User> task = context.LoadAsync<User>(email);
      task.Wait();
      return task.Result;
    }

    public static bool Update(User user) {
      return Add(user);
    }

    public static bool Patch(User user) {
      try {
        DynamoDBOperationConfig config = new DynamoDBOperationConfig();
        config.IgnoreNullValues = true;
        Task task = context.SaveAsync<User>(user, config);
        task.Wait();
        return task.IsCompleted;
      }
      catch (Exception) {
        return false;
      }
    }

    public static bool Login(User cred) {
      User user;
      Task<User> task = context.LoadAsync<User>(cred.Email);
      task.Wait();
      user = task.Result;
      return user.Password == cred.Password && !user.Disabled;
    }

    public static bool Disable(string email) {
      try {
        User user = new User();
        DynamoDBOperationConfig config = new DynamoDBOperationConfig();

        user.Email = email;
        user.Disabled = true;
        config.IgnoreNullValues = true;
        Task task = context.SaveAsync<User>(user, config);
        task.Wait();
        return task.IsCompleted;
      }
      catch (Exception) {
        return false;
      }
    }
  }
}

// EOF
