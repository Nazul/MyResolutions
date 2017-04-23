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
using Amazon.DynamoDBv2.DocumentModel;
using Iteso.MSC.MyResolutions.API.Models;
using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;


namespace Iteso.MSC.MyResolutions.API.App_Code {
  public static class GoalDataAccess {
    private static AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(RegionEndpoint.USEast1);
    private static DynamoDBContext context = new DynamoDBContext(dbClient);

    static GoalDataAccess() { }

    public static bool Add(Goal goal) {
      try {
        // HACK
        //goal.CheckIns = new List<DateTime>();
        //goal.CheckIns.Add(new DateTime(2001, 01, 01));
        //goal.CheckIns.Add(new DateTime(2001, 02, 01));
        //goal.CheckIns.Add(new DateTime(2001, 02, 03));
        //
        Task task = context.SaveAsync<Goal>(goal);
        task.Wait();
        return task.IsCompleted;
      }
      catch (Exception) {
        return false;
      }
    }

    public static List<Goal> GetByEmail(string email) {
      List<Goal> goals;
      AsyncSearch<Goal> query = context.QueryAsync<Goal>(email, QueryOperator.GreaterThan, new string[] { " " });
      Task<List<Goal>> taskQuery = query.GetNextSetAsync(default(CancellationToken));
      taskQuery.Wait();
      goals = taskQuery.Result;
      foreach (var goal in goals) {
        ScanOperationConfig config = new ScanOperationConfig();
        config.ConsistentRead = true;
        config.Filter = new ScanFilter();
        config.Filter.AddCondition("GoalName", ScanOperator.Equal, goal.GoalName);
        var search = context.FromScanAsync<Goal>(config);
        Task<List<Goal>> taskScan = search.GetRemainingAsync();
        taskScan.Wait();
        goal.Members = taskScan.Result.Count;
      }
      return goals;
    }

    public static Goal GetOne(string email, string goalName) {
      try {
        Task<Goal> task = context.LoadAsync<Goal>(email, goalName);
        task.Wait();
        return task.Result;
      }
      catch (Exception) {
        return null;
      }
    }

    public static bool Update(Goal goal) {
      return Add(goal);
    }

    public static bool Patch(Goal goal) {
      try {
        DynamoDBOperationConfig config = new DynamoDBOperationConfig();
        config.IgnoreNullValues = true;
        Task task = context.SaveAsync<Goal>(goal, config);
        task.Wait();
        return task.IsCompleted;
      }
      catch (Exception) {
        return false;
      }
    }

    public static bool Delete(string email, string goalName) {
      try {
        Task task = context.DeleteAsync<Goal>(email, goalName);
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
