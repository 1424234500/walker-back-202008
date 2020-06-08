package com.walker.core.database;

import com.walker.common.util.Tools;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;

import java.util.concurrent.Future;

public class KafkaTest {

    @Test
    public void doKafka() {
        for(String topicName : "hello,hello1,hello2".split(",")) {
            Tools.out(topicName, topicName, Kafka.doProducer(new Kafka.Fun<String, String>() {
                @Override
                public Future<RecordMetadata> make(Producer<String, String> producer) {
                    return producer.send(new ProducerRecord<String, String>(topicName, topicName));
                }
            }));
        }



    }
}