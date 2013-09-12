package tv.camfire.jetty.server.session

import org.eclipse.jetty.server.session.AbstractSessionIdManager
import javax.servlet.http.{HttpSession, HttpServletRequest}

/**
 * User: jonathan
 * Date: 6/5/13
 * Time: 4:30 PM
 */
class RedisRailsSessionIdManager()
  extends AbstractSessionIdManager {

  def getClusterId(nodeId: String): String = nodeId

  def getNodeId(clusterId: String, request: HttpServletRequest): String = clusterId

  /**
   * NOTE: This body is empty because we are 'fake' adding the session. Rails is the place where sessions actually
   * get created/destroyed.
   */

  def removeSession(session: HttpSession) {}

  def addSession(session: HttpSession) {}

  /**
   * NOTE: The below methods are intended for adding and removing session. This implementation we just want to access
   * what rails provides, thus we do not want to manage any of this in java land. We will insure none of these methods
   * get called by including a filter that rejects requests that do not already have a session associated with them.
   */

  def renewSessionId(oldClusterId: String, oldNodeId: String, request: HttpServletRequest) {
//    throw new UnsupportedOperationException()
  }

  def idInUse(id: String): Boolean = true
//  def idInUse(id: String): Boolean = throw new UnsupportedOperationException()


  def invalidateAll(id: String) {}
//  def invalidateAll(id: String) {throw new UnsupportedOperationException()}
}
