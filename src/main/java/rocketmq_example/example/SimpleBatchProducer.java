package rocketmq_example.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class SimpleBatchProducer {
	public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("BatchProducerGroupName");
        producer.setNamesrvAddr("10.10.6.71:9876;10.10.6.72:9876");
        producer.start();

        //If you just send messages of no more than 1MiB at a time, it is easy to use batch
        //Messages of the same batch should have: same topic, same waitStoreMsgOK and no schedule support
        String topic = "BatchTest";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(topic, "Tag,Tag1", "OrderID001 OrderID005", "Hello world 0".getBytes()));
        messages.add(new Message(topic, "Tag,Tag2", "OrderID002 OrderID008", "Hello world 1".getBytes()));
        messages.add(new Message(topic, "Tag,Tag3", "OrderID003 OrderID009", "Hello world 2".getBytes()));

        producer.send(messages);
        producer.shutdown();
    }
}
