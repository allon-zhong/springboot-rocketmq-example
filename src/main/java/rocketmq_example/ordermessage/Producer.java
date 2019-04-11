package rocketmq_example.ordermessage;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class Producer {
	public static void main(String[] args) throws UnsupportedEncodingException {
		try {
			DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");

			producer.setNamesrvAddr("10.10.6.71:9876;10.10.6.72:9876");
			producer.start();

			String[] tags = new String[] { "TagA", "TagB", "TagC", "TagD", "TagE" };
			for (int i = 0; i < 100; i++) {
				int orderId = i % 10;
				Message msg = new Message("TopicTestjjj", tags[i % tags.length], "KEY" + i,
						("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
				/**
				 * 发送消息有三个参数 1、消息 2、队列选择器实现类 3、选择队列依据字段（依据字段视情况而定）
				 */
				SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
					/**
					 * mqs 主题的所有队列 msg 当前要发送的消息 arg 与 orderId 对应
					 */
					@Override
					public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
						// 转化orderId
						Integer id = (Integer) arg;
						// 用orderId对消息队列数量进行取模
						int index = id % mqs.size();
						// 返回被选中的消息队列
						return mqs.get(index);
					}
				}, orderId);

				System.out.printf("%s%n", sendResult);
			}

			producer.shutdown();
		} catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
