package tv.camfire.jetty.server.session

import org.eclipse.jetty.server.session.{AbstractSession, AbstractSessionManager}
import javax.servlet.http.{HttpSessionEvent, HttpServletRequest}
import java.util
import scala.collection.JavaConverters._
import org.slf4j.LoggerFactory

//import us.spectr.rails.session.RailsSessionProcessorObject

import com.redis._
import com.redis.S
import com.redis.E
import com.redis.U
import scala.collection.JavaConversions._
import java.util.Collections
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * User: jonathan
 * Date: 6/5/13
 * Time: 4:29 PM
 */
class RedisRailsSessionManager(redisClientFactory: RedisClientFactory) extends AbstractSessionManager {
  private val log = LoggerFactory.getLogger(getClass)
  implicit protected val jsonFormats: Formats = DefaultFormats

  val clients = redisClientFactory.create()
  val subClients = redisClientFactory.create()
  val sub = new Sub()


  def getSession(idInCluster: String): AbstractSession = {
    loadFromStore(idInCluster)
  }

  def getFakeSession(idInCluster: String): AbstractSession = {
    new RedisRailsSession(this, idInCluster, Collections.emptyMap())
  }

  private def loadFromStore(clusterId: String): RedisRailsSession = {
    val sessionKey = getRedisSessionKeyId(clusterId)
    var redisData = ""
    clients.withClient {
      client => {
        redisData = client.get(sessionKey).get
      }
    }
    val redisJson = parse(redisData)
    val attributes = Extraction.flatten(redisJson)
    new RedisRailsSession(this, clusterId, attributes)
  }

  def getRedisSessionKeyId(sessionId: String): String = {
    "%s%s".format(redisClientFactory.sessionKeyPrefix, sessionId)
  }

  class Sub() {
    val sessionManager = RedisRailsSessionManager.this

    val subscribeClient = redisClientFactory.create()
//    val subscribeClient = new RedisClient("localhost", 6379)
    //    val SESSION_GLOB = "__keyspace@0__:session:*"
    val SESSION_GLOB = "__keyspace@0__:*"
//    val SESSION_GLOB = "__keyspace@0__:session:*"

    subClients.withClient {
      client => {
        client.pSubscribe(SESSION_GLOB)(callback)
      }
    }

//    subscribeClient.pSubscribe(SESSION_GLOB)(callback)

    def callback(pubsub: PubSubMessage) = pubsub match {
      case E(exception) =>
        println("Fatal error caused by dead consumer. Please init new consumer reconnecting to master or connect to backup")
        println(exception)
        println(exception.printStackTrace())
      case S(channel, no) =>
        println("subscribed to " + channel + " and count = " + no)
      case U(channel, no) =>
        println("unsubscribed from " + channel + " and count = " + no)
      case M(channel, msg) =>
        msg match {
          case "set" =>
            val sessionId = getSessionId(channel)
            println("Observed that a new session [%s] was created externally, attempting to manage session...".format(sessionId))
            val session = sessionManager.getSession(sessionId)
            sessionManager.addSession(session)
          case "expired" | "del" => // TODO: should this be a ||?
          //            val sessionId = getSessionId(channel)
          //            sessionManager.removeSession(sessionId)
          case x =>
            println("received message on channel " + channel + " as : " + x)
        }
    }

    def getSessionId(channel: String): String = {
      // When using keyspaces the channel should always look as follows unless the feature is changed in redis:
      // __keyspace@0__:session:8d86adeb7162009a3f524ca9a1431e9f
      channel.stripPrefix(getChannelSessionPrefix)
    }

    private def getChannelSessionPrefix: String = {
      val channelPrefix = "__keyspace@0__:"
      "%s%s".format(channelPrefix, redisClientFactory.sessionKeyPrefix)
    }
  }

  /**
   *
   */


  def addSession(session: AbstractSession) {
    // TODO: Possibly add a meta data table?
    val created = true

    if (created) {
      //      _sessionsStats.increment
      if (_sessionListeners != null) {
        val event: HttpSessionEvent = new HttpSessionEvent(session)
        for (listener <- _sessionListeners) listener.sessionCreated(event)
      }
    }
  }

  def removeSession(idInCluster: String): Boolean = {
    val removed = true
    if (removed) {
      //      _sessionsStats.decrement();
      //      _sessionTimeStats.set(round((System.currentTimeMillis() - session.getCreationTime())/1000.0));
    }
    if (_sessionListeners != null) {
      val session = this.getFakeSession(idInCluster)
      val event = new HttpSessionEvent(session)
      for (listener <- _sessionListeners) listener.sessionDestroyed(event)
    }

    removed
  }


  def invalidateSessions() {
  }

  /**
   * NOTE: The below methods are intended for adding and removing session. This implementation we just want to access
   * what rails provides, thus we do not want to manage any of this in java land. We will insure none of these methods
   * get called by including a filter that rejects requests that do not already have a session associated with them.
   */


  def renewSessionId(oldClusterId: String, oldNodeId: String, newClusterId: String, newNodeId: String) {
    throw new UnsupportedOperationException()
  }

  def newSession(request: HttpServletRequest): AbstractSession = {
    throw new UnsupportedOperationException()
  }

  private def storeSession() {
    //    //TODO: Determine if we should really be saving from java land. Will rails overwrite any changes made?
    //    String toStore = RailsSessionProcessorObject.marshalMap(session.getAttributeMap());
    //
    //    LOG.debug("[RedisSessionManager] storeSession - Storing session id={}", session.getClusterId());
    //    jedisExecutor.execute(new JedisCallback<Object>() {
    //      @Override
    //      public Object execute(Jedis jedis) {
    //        return jedis.multi(new TransactionBlock() {
    //          @Override
    //          public void execute() throws JedisException {
    //            //                            super.set(getRedisSessionKeyId(session.getId()), )
    //            //                            super.hmset(key, toStore);
    //            //                            super.expireAt(key, session.expiryTime);
    //          }
    //        });
    //      }
    //    });
  }

}
