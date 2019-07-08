package rocketmq_example.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
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
import rocketmq_example.mqandmysqltraction.producer.TransactionProducer;

@RestController
public class dataapi {

	protected final static Logger logger = LoggerFactory.getLogger(dataapi.class);
	
	@Autowired
	IMytableMapper mytable;
	
	//@Autowired
	TransactionProducer mqProducer;
	
	/**
	 * 保证序列化和反序列化是utf-8编码  ,RemotingHelper.DEFAULT_CHARSET
	 * 如果不是 要对应的修改编码
	 */
	@Autowired
	ObjectMapper objMapper;
	

	@Autowired
	MytableService mytableService;
	
	@GetMapping("/selectbyid")
	public boolean insertmytable(@RequestParam("id") Integer id) {
		return mytableService.existMyTableModelById(id);
	}
	
	@GetMapping("/insertmsgid")
	public int insertmsgid() {
		return mytable.insertmsgrecord(new MyTableModel());
	}
	
	/**
	 * 
	 * @param mytablemodels
	 * @return
	 * @throws JsonProcessingException
	 * @throws UnsupportedEncodingException
	 * @throws MQClientException
	 */
	//@PostMapping("/insertbatch1")
	public TransactionSendResult batchinsertmytable2(@RequestBody List<MyTableModel> mytablemodels) throws JsonProcessingException, UnsupportedEncodingException, MQClientException {
		
		byte[] body=objMapper.writeValueAsBytes(mytablemodels);
		
		Message me = new Message();
		//用户自定义消息属性可以用做过滤使用
		me.putUserProperty("userid", mytablemodels.get(0).getColumn1());
		// Topic
		me.setTopic("TransactionTopicTest1");
		// 标签
		me.setTags(mytablemodels.get(0).getColumn1());
		// 设置key 多个用空格分开
		me.setKeys(mytablemodels.get(0).getColumn1()+" "+mytablemodels.get(0).getColumn2());
		// 内容
		me.setBody(body);
		
		//这里可以处理简单的业务逻辑
		
		
		
		TransactionSendResult tsr=mqProducer.producer.sendMessageInTransaction(me, mytablemodels);
		
		if(tsr.getSendStatus()==SendStatus.SEND_OK&&tsr.getLocalTransactionState()==LocalTransactionState.COMMIT_MESSAGE)
		{
			// 这里认为发送成功了
			logger.info("我的一系列操作完成了 msgid:{}",tsr.getMsgId());
			//logger.info("事物执行成功");
		}else{
			logger.info("我的一系列操作有问题 msgid:{}",tsr.getMsgId());
		}
		
		
		return tsr;
	}
	
}
