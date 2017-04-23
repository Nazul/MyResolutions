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
using Iteso.MSC.MyResolutions.API.App_Code;
using Iteso.MSC.MyResolutions.API.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;


namespace MyResolutionsAPI.Controllers {
  [Produces("application/json")]
  [Route("api/[controller]")]
  public class GoalsController : Controller {
    // GET: api/Goals/user@domain.com
    [HttpGet("{email}")]
    public IActionResult Get(string email) {
      try {
        List<Goal> goals = GoalDataAccess.GetByEmail(email);

        if (goals == null) {
          return NotFound();
        }
        else {
          return new ObjectResult(goals);
        }
      }
      catch (Exception) {
        return NotFound();
      }
    }

    // GET: api/Goals/user@domain.com/Drink%20Water
    [HttpGet("{email}/{goalName}")]
    public IActionResult Get(string email, string goalName) {
      try {
        Goal goal = GoalDataAccess.GetOne(email, goalName);

        if (goal == null) {
          return NotFound();
        }
        else {
          return new ObjectResult(goal);
        }
      }
      catch (Exception) {
        return NotFound();
      }
    }

    // POST: api/Goals
    [HttpPost]
    public IActionResult Post([FromBody]Goal goal) {
      if (GoalDataAccess.Add(goal)) {
        return Created($"/api/Goals/{goal.Email}", goal);
      }
      else {
        return new BadRequestResult();
      }
    }

    // PUT api/Goals/user@domain.com/Drink%20Water
    [HttpPut("{email}/{goalName}")]
    public IActionResult Put(string email, string goalName, [FromBody]Goal goal) {
      var tmpGoal = GoalDataAccess.GetOne(email, goalName);
      if (tmpGoal == null)
        return NotFound();
      else {
        if (GoalDataAccess.Update(goal)) {
          return new OkObjectResult(GoalDataAccess.GetOne(email, goalName));
        }
        else {
          return new BadRequestResult();
        }
      }
    }

    // PATCH api/Goals/user@domain.com/Drink%20Water
    [HttpPatch("{email}/{goalName}")]
    public IActionResult Patch(string email, string goalName, [FromBody]Goal goal) {
      var tmpGoal = GoalDataAccess.GetOne(email, goalName);
      if (tmpGoal == null)
        return NotFound();
      else {
        goal.Email = email;
        goal.GoalName = goalName;
        if (GoalDataAccess.Patch(goal)) {
          return new OkObjectResult(GoalDataAccess.GetOne(email, goalName));
        }
        else {
          return new BadRequestResult();
        }
      }
    }

    // DELETE api/Goals/user@domain.com/Drink%20Water
    [HttpDelete("{email}/{goalName}")]
    public IActionResult Delete(string email, string goalName) {
      var goal = GoalDataAccess.GetOne(email, goalName);
      if (goal == null)
        return NotFound();
      else {
        if (GoalDataAccess.Delete(email, goalName)) {
          return new OkResult();
        }
        else {
          return new BadRequestResult();
        }
      }
    }
  }
}

// EOF
