package rocketmq_example.mqandmysqltraction.customer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import rocketmq_example.mqandmysqltraction.MyTableModel;
import rocketmq_example.mqandmysqltraction.MytableService;


/**
 * 生产者和消费者测试的时候记得注掉一中的一个以免观察不出效果
 * @author zyg
 *
 */
@Component
public class TransactionCustomer {
	static Logger logger = LoggerFactory.getLogger(TransactionCustomer.class);

	public DefaultMQPushConsumer dcustomer = null;

	@Autowired
	MytableService mytableService;
	
	/**
	 * 保证序列化和反序列化是utf-8编码  ,RemotingHelper.DEFAULT_CHARSET
	 * 如果不是 要对应的修改编码
	 */
	@Autowired
	ObjectMapper objMapper;

	static AtomicInteger aint=new AtomicInteger(0);
	
	@PostConstruct
	private void init() throws MQClientException {

		 /*
         * Instantiate with specified consumer group name.
         */
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("TransactionTopicTestCG");

		consumer.setNamesrvAddr("10.10.6.71:9876;10.10.6.72:9876");
		consumer.setConsumeThreadMax(30);
		consumer.setConsumeThreadMin(10);
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
        
    	JavaType javaType =objMapper.getTypeFactory().constructParametricType(ArrayList.class, MyTableModel.class);  
    	
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @SuppressWarnings("unchecked")
			@Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                ConsumeConcurrentlyContext context) {
            	for (MessageExt me : msgs) {
            		//消费数量
            		int count=aint.addAndGet(1);
            		logger.info("count:{} msgid:{}",count,me.getMsgId());
            		 
					try {
						List<MyTableModel> tmlist = (List<MyTableModel>)objMapper.readValue(me.getBody(), javaType);
					
						for (MyTableModel myTableModel : tmlist) {
							myTableModel.setMsgid(me.getMsgId());
							logger.info("msgid:{}",me.getMsgId());
							long start=System.currentTimeMillis();
							mytableService.insetmsg(myTableModel);
							logger.info("保存一条数据耗时:{}",System.currentTimeMillis()-start);
						}
					} catch (IOException e) {
						logger.error("消费mq异常",e);
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
            	
				}
                //System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        /*
         *  Launch the consumer instance.
         */
        consumer.start();
        this.dcustomer=consumer;
	}
	
	

	@PreDestroy
	public void Destroy() {
		dcustomer.shutdown();
	}
}
