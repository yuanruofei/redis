# 第1章 redis基本操作
## 1.1开启redis-server,启动成功如下图:
 <img src="./img/1_1.png"/>

## 1.2 安装RedisDesktopManager, 安装完成启动如下图
 <img src="./img/1_2.png"/>

## 1.3 创建连接 默认127.0.0.1或者localhost 端口号 6379

## 1.4 创建一个java或者javaweb项目 导入jar包 

 <img src="./img/1_3.png"/>

## 1.5 编写一个普通的RedisTest测试类
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
}

运行test1(),控制台打印如下

 <img src="./img/1_4.png"/>

## 1.6 通过JedisPool连接数据库,代码如下:

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
		System.out.println("name");
		// 5 关闭资源
		jedis.close();
		pool.close();
	}
运行test1(),控制台打印如下

<img src="./img/1_5.png"/>
 
# 第2章 redis进阶
## 2.1 写一个RedisPoolUtils类
## 2.2 redis缓存共有5种 
## 2.3 总结:redis采用二进制存储,不需要设置编码格式
分别为String类型 list类型  set类型 zset类型 hash类型

public class RedisPoolUtils {

	private static JedisPool pool=null;
	static{
		// 加载配置文件
		InputStream is=RedisPoolUtils.class.getClassLoader().getResourceAsStream("redis.properties");
		Properties pro=new Properties();
		try {
			pro.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 创建连接池配置对象
		JedisPoolConfig poolConfig=new JedisPoolConfig();
	poolConfig.setMaxIdle(Integer.parseInt(pro.get("redis.maxIdle").toString()));
	poolConfig.setMinIdle(Integer.parseInt(pro.get("redis.minIdle").toString()));
	poolConfig.setMaxTotal(Integer.parseInt(pro.get("redis.maxTotal").toString()));
		pool=new JedisPool(poolConfig,pro.getProperty("redis.url"),Integer.parseInt(pro.get("redis.port").toString()));	
	}
	// 获得jedis资源的方法
	public static Jedis getJedis(){
		return pool.getResource();
	}
@Test

	/*
	 * 1 redis 存取string类型
	 * 数据插入成功返回 OK
	 */
	public void Test1(){
		Jedis jedis=new Jedis();
		jedis.set("sex", "男");
		System.out.println(jedis.get("sex"));
		//取出 name=tom
		System.out.println(jedis.get("name"));
	}

	@Test
	/*
	 * 2 redis 存取list类型 
	 * 数据添加成功返回添加个数 栈存储 允许字段重复
	 */
	public void test2(){
		Jedis jedis=new Jedis();
		/*jedis.lpush("num1", "0");
		jedis.lpush("num2", "0","1");
		jedis.lpush("num3", "0","1","2");
		// 弹栈取出 后进先出
		System.out.println(jedis.lrange("num1", 0, 0));
		System.out.println(jedis.lrange("num2", 0, -1));
		System.out.println(jedis.lrange("num3", 0, -1));*/
		// lpop 元素从头部弹出 删除元素 0 1 2 弹出2 ==>0 1
		//System.out.println(jedis.lpop("num3"));  // 2
		//System.out.println(jedis.lrange("num3", 0, -1)); //1 0
		// rpop 元素从尾部弹出 删除尾部 0 1 尾部弹出0==> 1
		System.out.println(jedis.rpop("num3")); // 0
		System.out.println(jedis.lrange("num3",0,-1));  // 1
	}

	@Test
	/*
	 * 3 存储set数据类型
	 * set不允许出现重复元素
	 */
	public void test3(){
		Jedis jedis=new Jedis();
		jedis.sadd("myuser", "tom","jerry","steven","yrf");
		// 获得所有myuser所有成员 
		System.out.println(jedis.smembers("myuser"));
		System.out.println(jedis.scard("myuser"));  // 获得数量 4
		// 获取某个成员 存在的话返回 true
		System.out.println(jedis.sismember("myuser", "tom"));  //true 
		System.out.println(jedis.sismember("myuser", "yom"));  //false
		//删除语句 
		System.out.println(jedis.srem("myuser", "yrf"));
		System.out.println(jedis.smembers("myuser"));
		// 插入数据
		jedis.sadd("myuser1", "lili","serry","tom");
		//返回差集 返回myuser的差集
		System.out.println(jedis.sdiff("myuser","myuser1"));
		//返回交集
		System.out.println(jedis.sinter("myuser","myuser1"));
		//返回交集并存储
	System.out.println(jedis.sinterstore("myuser2","myuser","myuser1"));
		//返回并集
		System.out.println(jedis.sunion("myuser","myuser1"));
	}

	@Test
	/*
	 * 4 存储zset类型
	 */
	public void test4(){
		Jedis jedis=new Jedis();
		jedis.zadd("score", 100,"tom");
		jedis.zadd("score", 90,"jerry");
		jedis.zadd("score", 95,"rose");
		// 查看元素个数
		System.out.println(jedis.zcard("score"));
		// 返回double类型的数据
		System.out.println(jedis.zscore("score", "tom"));
		//返回多少元素在查找范围内
		System.out.println(jedis.zcount("score", 90, 100));
		//移出tom jerry
		System.out.println(jedis.zrem("score", "tom","jerry"));
	}
	@Test
	/*
	 *5 hash存储 
	 */
	public void test5(){
		Jedis jedis=new Jedis();
		jedis.hset("user", "name", "tom");
		jedis.hset("user", "age", "18");
		jedis.hset("user", "sex", "男");
		jedis.hset("user", "addr", "郑州");
		//查找所有元素
		System.out.println(jedis.hgetAll("user"));
		//查找指定元素
		System.out.println(jedis.hget("user", "name"));
		//查找字段的value值
		System.out.println(jedis.hmget("user", "name","age","sex","addr"));
		//判断字段是否存在 true false
		System.out.println(jedis.hexists("user", "name"));
		//将字段 age增加值  18 +20  ==>38
		System.out.println(jedis.hincrBy("user", "age", 20));
	}
}


