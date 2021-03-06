package rocketmq_example.quickstart;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

public class Consumer {

	static AtomicInteger aint=new AtomicInteger(0);
	public static void main(String[] args) throws InterruptedException, MQClientException {

		
        /*
         * Instantiate with specified consumer group name.
         */
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("TransactionTopicTestCG");

		consumer.setNamesrvAddr("10.10.6.71:9876;10.10.6.72:9876");
		consumer.setConsumeThreadMax(30);
		consumer.setConsumeThreadMin(5);
		consumer.setConsumeMessageBatchMaxSize(32);
		consumer.setMaxReconsumeTimes(16);
		consumer.setConsumeConcurrentlyMaxSpan(2000);
		consumer.setPullInterval(0);
        /*
         * Specify name server addresses.
         * <p/>
         *
         * Alternatively, you may specify name server addresses via exporting environmental variable: NAMESRV_ADDR
         * <pre>
         * {@code
         * consumer.setNamesrvAddr("name-server1-ip:9876;name-server2-ip:9876");
         * }
         * </pre>
         */

        /*
         * Specify where to start in case the specified consumer group is a brand new one.
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);

        /*
         * Subscribe one more more topics to consume.
         */
        consumer.subscribe("TransactionTopicTest1", "*");

        /*
         *  Register callback to execute on arrival of messages fetched from brokers.
         */
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                ConsumeConcurrentlyContext context) {
            	for (MessageExt me : msgs) {
            		//消费数量
            		int count=aint.addAndGet(1);
                	System.out.println(count+" "+me.getMsgId()+" "+new String(me.getBody()));
				}
                //System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        /*
         *  Launch the consumer instance.
         */
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }
}
