package rocketmq_example.filter;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

public class SqlConsumer {
	public static void main(String[] args) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name_99");

        consumer.setNamesrvAddr("10.10.6.71:9876;10.10.6.72:9876");
        try {
            consumer.subscribe("TopicTest",
                MessageSelector.bySql("(TAGS is not null and TAGS in ('TagA', 'TagB')) and a is not null and a between 10 and 50"));
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }
        System.out.printf("Consumer Started.%n");
    }
}
