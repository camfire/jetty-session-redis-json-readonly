package tv.camfire.jetty.server.session

import org.eclipse.jetty.server.session.{AbstractSessionManager, AbstractSession}
import java.util
import scala.collection.JavaConversions._


/**
 * User: jonathan
 * Date: 6/5/13
 * Time: 4:31 PM
 */
class RedisRailsSession(private val sessionManager: AbstractSessionManager,
                        clusterId: String,
                        attributes: util.Map[String, String])
  extends AbstractSession(sessionManager, 0, 0, clusterId) {
  attributes.foreach(kv => {
    super.doPutOrRemove(kv._1, kv._2)
  })
}
