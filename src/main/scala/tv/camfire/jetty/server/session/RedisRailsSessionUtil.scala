package tv.camfire.jetty.server.session

import javax.servlet.http.HttpSession


/**
 * User: jonathan
 * Date: 6/5/13
 * Time: 5:04 PM
 */
object RedisRailsSessionUtil {
  def getWardenUserIdAsString(session: HttpSession): String = {
    val wardenId = getWardenUserId(session)
    if (wardenId == null) null else wardenId.toString
  }

  def getWardenUserIdAsInt(session: HttpSession): Integer = {
    val wardenId = getWardenUserIdAsString(session)
    if (wardenId == null) null else Integer.parseInt(wardenId)
  }

  def getWardenUserId(session: HttpSession): Object = {
    session.getAttribute(".warden.user.user.key[0][0]")
  }
}

