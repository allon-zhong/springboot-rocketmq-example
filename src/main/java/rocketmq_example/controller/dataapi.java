package rocketmq_example.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import rocketmq_example.mqandmysqltraction.IMytableMapper;
import rocketmq_example.mqandmysqltraction.MyTableModel;
import rocketmq_example.mqandmysqltraction.MytableService;
import rocketmq_example.mqandmysqltraction.mq.MqProducer;

@RestController
public class dataapi {

	protected final static Logger logger = LoggerFactory.getLogger(dataapi.class);
	
	@Autowired
	IMytableMapper mytable;
	
	@Autowired
	MqProducer mqProducer;
	
	@Autowired
	ObjectMapper objMapper;
	

	@Autowired
	MytableService mytableService;
	
	//------------------------------------------------------------------------------------------------------------
	
	
	@PostMapping("/insertbatch")
	public TransactionSendResult batchinsertmytable(@RequestBody List<MyTableModel> mytablemodels) throws JsonProcessingException, UnsupportedEncodingException, MQClientException {
		
		String objstr=objMapper.writeValueAsString(mytablemodels);
		
		logger.info(objstr);
		Message me = new Message();
		// Topic
		me.setTopic("TopicTest");
		// 标签
		me.setTags("TagA");
		// 设置key 多个用空格分开
		me.setKeys("key1 key2");
		//me.setTransactionId(encodeStr);
		// 内容
		me.setBody(objstr.getBytes(RemotingHelper.DEFAULT_CHARSET));
		
		TransactionSendResult tsr=mqProducer.producer.sendMessageInTransaction(me, mytablemodels);
		
		//logger.info(tsr.getTransactionId());
		
		return tsr;
		//return mytable.batchinsertmytable(mytablemodels);
	}
	
	//------------------------------------------------------------------------------------------------------------
	
//	@PostMapping("/insertbatch1")
//	public void insertmytable(@RequestBody List<MyTableModel> mytablemodels) throws JsonProcessingException, UnsupportedEncodingException, MQClientException{
//		mytableService.execMytableinsert(mytablemodels);
//	}

	@GetMapping("/selectbyid")
	public boolean insertmytable(@RequestParam("id") Integer id) {
		return mytableService.existMyTableModelById(id);
	}
	
	//------------------------------------------------------------------------------------------------------------
	/**
	 * 我的例子
	 * @param mytablemodels
	 * @return
	 * @throws JsonProcessingException
	 * @throws UnsupportedEncodingException
	 * @throws MQClientException
	 */
	@PostMapping("/insertbatch1")
	public TransactionSendResult batchinsertmytable2(@RequestBody List<MyTableModel> mytablemodels) throws JsonProcessingException, UnsupportedEncodingException, MQClientException {
		
		String objstr=objMapper.writeValueAsString(mytablemodels);
		
		logger.info(objstr);
		
		Message me = new Message();
		// Topic
		me.setTopic("TopicTest");
		// 标签
		me.setTags("TagA");
		// 设置key 多个用空格分开
		me.setKeys("key1 key2");
		//me.setTransactionId(encodeStr);
		// 内容
		me.setBody(objstr.getBytes(RemotingHelper.DEFAULT_CHARSET));
		
		
		TransactionSendResult tsr=mqProducer.producer.sendMessageInTransaction(me, mytablemodels);
		
		return tsr;
	}
	
}
