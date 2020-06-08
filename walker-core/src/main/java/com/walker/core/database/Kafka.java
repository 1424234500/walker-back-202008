package com.walker.core.database;


import com.walker.core.aop.TestAdapter;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * kafka队列
 * 连接池管理
 * 提供连接 销毁连接
 * 单例
 * @author walker
 *
 */
public class Kafka extends TestAdapter{
	protected static Logger log = LoggerFactory.getLogger(Kafka.class);

	Properties properties = new Properties();
	public Kafka setProperties(String key, String value){
		this.properties.setProperty(key, value);
		return this;
	}
	public Properties getProperties(){
		return (Properties) this.properties.clone();
	}

	public <K, V> KafkaProducer<K, V> getProducer() {
		return new KafkaProducer<K, V>(properties);
	}
	/**
	 * 连接数计数器
	 */
	int cc = 0;
	/**
	 * 查询次数计数器
	 */
	int get = 0;
	/**
	 * 关闭计数器
	 */
	int close = 0;

	/**
	 * 回调环绕执行 kafka 操作
	 */
	public static <K, V> Future<RecordMetadata> doProducer(Fun<K, V> fun){
		Future<RecordMetadata> res = null;
		if(fun != null) {
			Producer<K, V> kafka = Kafka.getInstance().getProducer();
			try {
				res = fun.make(kafka);
			}catch (Exception e){
				log.debug(e.getMessage(), e);
				throw e;
			}finally {
				Kafka.getInstance().close(kafka);
			}
		} else {
			log.error("fun is null");
		}
		return res;
	}
	/**
	 * 回调环绕执行 kafka 操作
	 */
	public static <K, V> Future<RecordMetadata> doSendMessage(String topic, K key, V value){
		return doProducer(new Fun<K, V>() {
			@Override
			public Future<RecordMetadata> make(Producer<K, V> producer) {
				return producer.send(new ProducerRecord<K, V>(topic, key, value));
			}
		});
	}
	public <K, V> void close(Producer<K, V> kafka){
		if(kafka != null){
			kafka.close();
			get--;
		}
	}
	/**
	 * 从缓存获取并初始化
	 * @return
	 */
	public boolean doInit(){
		Cache<String> cache = CacheMgr.getInstance();
		properties.put("bootstrap.servers", cache.get("kafka_bootstrap.servers", "127.0.0.1:8097"));//xxx服务器ip ,1xxx:9092,xxx:9092
		properties.put("acks", "all");//所有follower都响应了才认为消息提交成功，即"committed"
		properties.put("retries", 0);//retries = MAX 无限重试，直到你意识到出现了问题:)
		properties.put("batch.size", 16384);//producer将试图批处理消息记录，以减少请求次数.默认的批量处理消息字节数 batch.size当批量的数据大小达到设定值后，就会立即发送，不顾下面的linger.ms
		properties.put("linger.ms", 1);//延迟1ms发送，这项设置将通过增加小的延迟来完成--即，不是立即发送一条记录，producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理
		properties.put("buffer.memory", 33554432);//producer可以用来缓存数据的内存大小。
		properties.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


		log.info("kafka init ----------------------- " + cc++);
		test();
		log.info(this.toString());
		if(cc > 1) {
			log.error("----------------------------");
			log.error("---------单例失败？？？？？------------");
			log.error("----------------------------");
		}
		return true;
	}

	@Override
	public boolean doUninit() {
		log.warn("uninit");
		return super.doUninit();
	}
	@Override
	public boolean doTest() {
		return getProducer() != null;
	}
	private Kafka() {
		init();
	}
	
	public static Kafka getInstance() {
		return SingletonFactory.instance;
	}
	//单例
	private static class SingletonFactory{
		static Kafka instance ;
		static {
			log.warn("singleton instance construct " + SingletonFactory.class);
			instance = new Kafka();
		}
	}
	public String toString() {
		return "kafka " + properties.toString();
	}
	
	/**
	 * jedis获取 执行后 自动关闭
	 */
	public interface Fun<K, V>{
	 	Future<RecordMetadata> make(Producer<K, V> producer) ;
	}


	/**
	 * 自动提交offset
	 */
	public void commitAuto(List<String> topics) {
		Properties props = getProperties();
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(topics);
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			for (ConsumerRecord<String, String> record : records)
				System.err.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
		}
	}

	/**
	 * 手动提交offset
	 */
	public void commitControl(List<String> topics) {
		Properties props = getProperties();
		props.put("enable.auto.commit", "false");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(topics);
		final int minBatchSize = 2;
		List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			for (ConsumerRecord<String, String> record : records) {
				buffer.add(record);
			}
			if (buffer.size() >= minBatchSize) {
				/////////////////////////////////////////do yourself
				// 阻塞同步提交
				consumer.commitSync();
				buffer.clear();
			}
		}
	}

	/**
	 * 手动设置分区
	 */
	public void setOffSet(List<String> topics)   {
		Properties props = getProperties();
		props.put("enable.auto.commit", "false");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(topics);

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
			// 处理每个分区消息后, 提交偏移量
			for (TopicPartition partition : records.partitions()) {
				List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);

				for (ConsumerRecord<String, String> record : partitionRecords) {
					System.out.println(record.offset() + ": " + record.value());
				}
				long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
				consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
			}
		}
	}

	/**
	 * 手动设置消息offset
	 */
	public void setSeek(List<String> topics ) {
		Properties props = getProperties();
		props.put("enable.auto.commit", "false");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(topics);
		consumer.seek(new TopicPartition("http_zhixin", 0), 797670770);
		ConsumerRecords<String, String> records = consumer.poll(100);

		for (ConsumerRecord<String, String> record : records) {
			System.err.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
			consumer.commitSync();
		}

	}


}	
