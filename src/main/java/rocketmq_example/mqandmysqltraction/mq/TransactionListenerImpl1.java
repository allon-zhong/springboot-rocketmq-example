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
 * 
 * 
 * 
 * @author zyg
 *
 */
@Component
public class TransactionListenerImpl1 implements TransactionListener {

	static Logger logger = LoggerFactory.getLogger(TransactionListenerImpl1.class);

	// 记录数据库执行状态，防止MQ commit message的时候断网等情况。
	//如果提交事物状态为COMMIT_MESSAGE broker没有收到 正好本服务也死掉了 或重启了 也会丢失 提交信息
	private ConcurrentHashMap<String, LocalTransactionState> countHashMap = new ConcurrentHashMap<>();

	@Autowired
	MytableService mytableService;

	/**
	 * 一定要设置执行sql时间，以免超时
	 * 
	 */
	@Override
	public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		logger.info(msg.toString());

		LocalTransactionState lts = LocalTransactionState.UNKNOW;

		@SuppressWarnings("unchecked")
		List<MyTableModel> mytablelist = (List<MyTableModel>) arg;

		countHashMap.put(msg.getTransactionId(), lts);

		try {
			mytableService.execMytableinsert2(mytablelist);
			lts = LocalTransactionState.COMMIT_MESSAGE;
		} catch (Exception e) {
			logger.error("数据库事务异常", e);
			lts = LocalTransactionState.ROLLBACK_MESSAGE;
		}

		// 如果事物不报异常 就直接返回LocalTransactionState.COMMIT_MESSAGE

		// 否则回滚事物

		// 超时处理

		// 这个方法和发送是同步处理，先发送half消息然后在执行事物，如果超时会报异常
		
		return lts;

	}

	/**
	 * 插入数据异步的时候这里 就要做逻辑处理，保存当前状态进行回滚或提交处理
	 * 
	 * 
	 */
	@Override
	public LocalTransactionState checkLocalTransaction(MessageExt msg) {

		LocalTransactionState ltscheck = countHashMap.get(msg.getTransactionId());
		if (null != ltscheck) {
			countHashMap.remove(msg.getTransactionId());
		} else {
			return LocalTransactionState.UNKNOW;
		}
		return countHashMap.get(msg.getTransactionId());
	}

}
