package cn.yrf.redis;

import java.security.Policy;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
 * @yrf 通过java代码对Redis数据库进行进本操作
 */
public class RedisTest {
	@Test
	/*
	 * 通过普通连接存取数据
	 */
	public void test1(){
		// 存储String 类型
		// 1 获得连接对象
		Jedis jedis=new Jedis("127.0.0.1",6379);
		
		// 2 存储数据 
		jedis.set("name", "Tom");
		jedis.set("age", "21");
		jedis.set("addr", "郑州");
		
		//3 获得数据
		System.out.println(jedis.get("name"));
		System.out.println(jedis.get("age"));
		System.out.println(jedis.get("addr"));
		
	}
	
	@Test
	/*
	 * 通过Jedispool 连接数据库
	 * 
	 */
	public void test2(){
		// 1 创建连接池配置对象
		JedisPoolConfig poolConfig=new JedisPoolConfig();
		poolConfig.setMaxIdle(30);   //最大闲置个数
		poolConfig.setMinIdle(10);   //最小闲置个数
		poolConfig.setMaxTotal(50);  //最大连接个数
		
		// 2 创建连接池
		JedisPool pool=new JedisPool(poolConfig,"127.0.0.1",6379);
		
		// 3 从池子中获得资源
		Jedis jedis=pool.getResource();
		
		// 4 操作数据库
		
		jedis.set("xxx", "yyy");
		System.out.println(jedis.get("xxx"));
		
		//获取 name=tom
		System.out.println(jedis.get("name"));
		
		// 5 关闭资源
		jedis.close();
		pool.close();
	}
	
}
