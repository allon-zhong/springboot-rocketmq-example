package rocketmq_example.mqandmysqltraction;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import rocketmq_example.mqandmysqltraction.mq.MqProducer;

@Service
public class MytableService {
	static Logger logger = LoggerFactory.getLogger(MytableService.class);

	@Autowired
	IMytableMapper mytable;

	@Autowired
	MqProducer mqProducer;

	@Autowired
	ObjectMapper objMapper;

	@Transactional
	public void execMytableinsert(List<MyTableModel> mytablemodels)
			throws JsonProcessingException, UnsupportedEncodingException, MQClientException {
		for (MyTableModel myTableModel : mytablemodels) {
			// 插入数据库
			mytable.insertmytable(myTableModel);

			logger.info("-----------------------" + myTableModel.getId());
			String objstr = objMapper.writeValueAsString(myTableModel);

			logger.info(objstr);

			Message me = new Message();
			// Topic
			me.setTopic("TopicTest");
			// 标签
			me.setTags("TagA");
			// 设置key 多个用空格分开
			me.setKeys("key1 key2");

			me.putUserProperty("id", myTableModel.getId().toString());
			// 内容
			me.setBody(objstr.getBytes(RemotingHelper.DEFAULT_CHARSET));

			TransactionSendResult tsr = mqProducer.producer.sendMessageInTransaction(me, mytablemodels);

			logger.info(tsr.toString());
		}
	}

	
	/**
	 * 这里可以显示提交事物 返回boolean
	 * 一条一条插入只是为了展现事物的特性
	 * @param mytablemodels
	 * @return
	 */
	@Transactional
	public List<Integer> execMytableinsert2(List<MyTableModel> mytablemodels) {
		
		logger.info("开始执行数据库事物");
		List<Integer> result = new ArrayList<Integer>();
		for (MyTableModel myTableModel : mytablemodels) {
			// 插入数据库
			mytable.insertmytable(myTableModel);
			result.add(myTableModel.getId());
		}

		logger.info("结束执行数据库事物");
		return result;
	}

	public boolean existMyTableModelById(Integer id) {
		MyTableModel myTableModel = mytable.selectMyTableModelById(id);
		if (myTableModel != null && null != myTableModel.getId()) {
			return true;
		}
		return false;
	}
}
