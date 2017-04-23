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


namespace Iteso.MSC.MyResolutions.API.Controllers {
  [Produces("application/json")]
  [Route("api/[controller]")]
  public class UsersController : Controller {
    // GET api/Users/user@domain.com
    [HttpGet("{email}")]
    public IActionResult Get(string email) {
      try {
        User user = UserDataAccess.GetByEmail(email);

        if (user == null) {
          return NotFound();
        }
        else {
          return new ObjectResult(user);
        }
      }
      catch (Exception) {
        return NotFound();
      }
    }

    // POST api/Users
    [HttpPost]
    public IActionResult Post([FromBody]User user) {
      if (UserDataAccess.Add(user)) {
        return Created($"/api/Users/{user.Email}", user);
      }
      else {
        return new BadRequestResult();
      }
    }

    // PUT api/Users/user@domain.com
    [HttpPut("{email}")]
    public IActionResult Put(string email, [FromBody]User user) {
      var tmpUser = UserDataAccess.GetByEmail(email);
      if (tmpUser == null)
        return NotFound();
      else {
        if (UserDataAccess.Update(user)) {
          return new OkObjectResult(UserDataAccess.GetByEmail(email));
        }
        else {
          return new BadRequestResult();
        }
      }
    }

    // PATCH api/Users/user@domain.com
    [HttpPatch("{email}")]
    public IActionResult Patch(string email, [FromBody]User user) {
      var tmpUser = UserDataAccess.GetByEmail(email);
      if (tmpUser == null)
        return NotFound();
      else {
        user.Email = email;
        if (UserDataAccess.Patch(user)) {
          return new OkObjectResult(UserDataAccess.GetByEmail(email));
        }
        else {
          return new BadRequestResult();
        }
      }
    }

    // DELETE api/Users/user@domain.com
    [HttpDelete("{email}")]
    public IActionResult Delete(string email) {
      var user = UserDataAccess.GetByEmail(email);
      if (user == null)
        return NotFound();
      else {
        if (UserDataAccess.Disable(email)) {
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
