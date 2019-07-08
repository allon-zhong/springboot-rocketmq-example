package rocketmq_example.mqandmysqltraction.producer;

import java.util.List;

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
public class TransactionListenerImpl implements TransactionListener {

	static Logger logger = LoggerFactory.getLogger(TransactionListenerImpl.class);


	@Autowired
	MytableService mytableService;

	/**
	 * 一定要设置执行sql时间，尽量不要超时
	 * 
	 */
	@Override
	public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		logger.info("开始执行本地数据库事物  transactionid:{}", msg.getTransactionId());

		LocalTransactionState lts = LocalTransactionState.UNKNOW;

		@SuppressWarnings("unchecked")
		List<MyTableModel> mytablelist = (List<MyTableModel>) arg;

		try {
			//数据库事物执行时间不要超过mq回查时间 默认15分钟
			mytableService.execMytableinsert2(mytablelist, msg.getTransactionId());
			lts = LocalTransactionState.COMMIT_MESSAGE;
		} catch (Exception e) {
			logger.error("数据库事务异常", e);
			lts = LocalTransactionState.ROLLBACK_MESSAGE;
		}

		logger.info("结束执行本地数据库事物  transactionid:{} 返回:{}", msg.getTransactionId(),lts);

		return lts;

	}

	/**
	 * 去数据库查询看看是否存在已经成功发送预提交数据而没有commit成功的mq信息
	 * 每分钟1次默认15次
	 * 
	 * 这里可以做个计数 让MQ重试5次/5分钟就回滚减轻MQ回查的压力
	 * 
	 */
	@Override
	public LocalTransactionState checkLocalTransaction(MessageExt msg) {
		if (mytableService.existMyTableModelByMsgid(msg.getTransactionId())) {
			logger.info("查询到已提交事物 transactionid:{}",msg.getTransactionId());
			return LocalTransactionState.COMMIT_MESSAGE;
		} else {
			logger.info("未查到已提交事物 transactionid:{}",msg.getTransactionId());
			return LocalTransactionState.UNKNOW;
		}

	}

}
