package rocketmq_example.mqandmysqltraction.mq;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rocketmq_example.mqandmysqltraction.MytableService;

//@Component
public class TransactionListenerImpl implements TransactionListener {

	static Logger logger = LoggerFactory.getLogger(TransactionListenerImpl.class);

	private ConcurrentHashMap<String, Integer> countHashMap = new ConcurrentHashMap<>();

	private final static int MAX_COUNT = 5;

	@Autowired
	MytableService mytableService;

	@Override
	public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		logger.info(msg.toString());
		return LocalTransactionState.UNKNOW;

	}

	@Override
	public LocalTransactionState checkLocalTransaction(MessageExt msg) {

		logger.info("检查事物 -----{}", msg.getUserProperty("id"));
		boolean exist = mytableService.existMyTableModelById(Integer.valueOf(msg.getUserProperty("id")));

		logger.info("检查事物结果 -----{}", exist);
		if (exist) {

			logger.info("检查事物 -----成功提交");
			return LocalTransactionState.COMMIT_MESSAGE;
		} else {

			logger.info("检查事物 -----失败提交");
			return rollBackOrUnown(msg.getUserProperty("id"));
		}
	}

	public LocalTransactionState rollBackOrUnown(String id) {
		Integer num = countHashMap.get(id);
		if (num != null && ++num > MAX_COUNT) {
			countHashMap.remove(id);
			return LocalTransactionState.ROLLBACK_MESSAGE;
		}
		if (num == null) {
			num = new Integer(1);
		}
		countHashMap.put(id, num);
		return LocalTransactionState.UNKNOW;
	}
}
