package controllers

import views.html.accessOk
import play.api.mvc.{Action, Controller}
import be.objectify.deadbolt.scalabolt.Scalabolt
import security.{MyUserlessScalaboltHandler, MyScalaboltHandler}

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
object RoleHolderNotPresentController extends Controller with Scalabolt
{

  def loggedIn = SBRoleHolderNotPresent(new MyScalaboltHandler)
                 {
                   Action
                   {
                     Ok(accessOk())
                   }
                 }

  def notLoggedIn = SBRoleHolderNotPresent(new MyUserlessScalaboltHandler)
                    {
                      Action
                      {
                        Ok(accessOk())
                      }
                    }
}