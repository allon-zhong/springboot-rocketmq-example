package rocketmq_example.mq;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component
public class MessageListenerConcurrentlyImpl implements MessageListenerConcurrently {
	static Logger LOG = LoggerFactory.getLogger(MessageListenerConcurrentlyImpl.class);
	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

		long start = System.currentTimeMillis();
		try {
//			for (MessageExt messageExt : msgs) {
//				String messagestr = new String(messageExt.getBody(), "UTF-8");
//			
//			}

		} catch (Exception e) {

			for (MessageExt messageExt : msgs) {
				try {
					LOG.error("异常消息,MSGID:{},MSGTag:{},MSG{}", messageExt.getMsgId(), messageExt.getTags(),
							new String(messageExt.getBody(), "UTF-8"));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			LOG.error("处理消息异常{},{}", e.getMessage(), e.getStackTrace());
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			// try {
			// Thread.sleep(10000);
			// return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			// } catch (InterruptedException e1) {
			//
			// }

		}

		LOG.info("消费一次总共耗时:" + (System.currentTimeMillis() - start)+"size:"+msgs.size());
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
