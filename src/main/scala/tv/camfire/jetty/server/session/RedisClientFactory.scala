package tv.camfire.jetty.server.session

import com.redis.RedisClientPool

/**
 * User: jonathan
 * Date: 7/29/13
 * Time: 7:20 PM
 */
class RedisClientFactory(redisHost: String = "localhost",
                         redisPort: Int = 6379,
                         val sessionKeyPrefix: String = ""
                          ) {

  def create(): RedisClientPool = {
    new RedisClientPool(redisHost, redisPort)
  }
}
