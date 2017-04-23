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
using System;
using System.Collections.Generic;


namespace Iteso.MSC.MyResolutions.API.Models {
  [DynamoDBTable("MyResolutionsGoals")]
  public class Goal {
    public Goal() { }

    [DynamoDBHashKey]
    public string Email { get; set; }
    [DynamoDBRangeKey]
    public string GoalName { get; set; }
    [DynamoDBProperty("Category")]
    public string Category { get; set; }
    [DynamoDBProperty("Frequency")]
    public string Frequency { get; set; }
    [DynamoDBProperty("WhenToCheck")]
    public string WhenToCheck { get; set; }
    [DynamoDBProperty("CheckIns")]
    public List<DateTime> CheckIns { get; set; }
    [DynamoDBIgnore]
    public int Members { get; set; }
  }
}

// EOF
