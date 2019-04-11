package rocketmq_example.mqandmysqltraction.mq;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rocketmq_example.mqandmysqltraction.MyTableModel;
import rocketmq_example.mqandmysqltraction.MytableService;

/**
 * 把数据库事物嵌套在mq事物当中不能显示抛出异常 
 * 
 * @author zyg
 *
 */
@Component
public class TransactionListenerImpl1 implements TransactionListener {

	static Logger logger = LoggerFactory.getLogger(TransactionListenerImpl1.class);

	private ConcurrentHashMap<String, Integer> countHashMap = new ConcurrentHashMap<>();


	@Autowired
	MytableService mytableService;

	@Override
	public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		logger.info(msg.toString());
		
		@SuppressWarnings("unchecked")
		List<MyTableModel> mytablelist=(List<MyTableModel>)arg;
		
		try {
			List<Integer> integerlist= mytableService.execMytableinsert2(mytablelist);
			
			countHashMap.put(msg.getTransactionId(), integerlist.size());
		} catch (Exception e) {
			logger.error("数据库事务异常",e);
			return LocalTransactionState.ROLLBACK_MESSAGE;
		}
		
		//如果事物不报异常  就直接返回LocalTransactionState.COMMIT_MESSAGE
		 
		 //否则回滚事物
		
		//超时处理
		
		//这个方法和发送是同步处理，先发送half消息然后在执行事物，如果超时会报异常
		
		return LocalTransactionState.COMMIT_MESSAGE;

	}

	@Override
	public LocalTransactionState checkLocalTransaction(MessageExt msg) {

		return LocalTransactionState.COMMIT_MESSAGE;	
	}

	
}
