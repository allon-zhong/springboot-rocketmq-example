package rocketmq_example.mqandmysqltraction;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MytableService {
	static Logger logger = LoggerFactory.getLogger(MytableService.class);

	@Autowired
	IMytableMapper mytable;

	@Autowired
	ObjectMapper objMapper;

	/**
	 * 这里可以显示提交事物 返回boolean 一条一条插入只是为了展现事物的特性 获取所有异常 处理你的业务逻辑等等
	 * 
	 * @param mytablemodels
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class, timeout = 60000)
	public List<Integer> execMytableinsert2(List<MyTableModel> mytablemodels, String msgid) {

		// logger.info("开始执行数据库事物");
		List<Integer> result = new ArrayList<Integer>();
		for (MyTableModel myTableModel : mytablemodels) {
			// 插入数据库
			myTableModel.setMsgid(msgid);
			mytable.insertmytable(myTableModel);
			result.add(myTableModel.getId());
		}

		// logger.info("结束执行数据库事物");
		return result;
	}

	public boolean existMyTableModelById(Integer id) {
		MyTableModel myTableModel = mytable.selectMyTableModelById(id);
		if (myTableModel != null && null != myTableModel.getId()) {
			return true;
		}
		return false;
	}

	/**
	 * 查询是否存在已经发送过的msgid消息
	 * 
	 * @param msgid
	 * @return
	 */
	public boolean existMyTableModelByMsgid(String msgid) {
		int count = mytable.selectMyTableModelByMsgid(msgid);
		if (count > 0) {
			return true;
		}
		return false;
	}

	public void insetmsg(MyTableModel mytablemodel) {
		try {
			mytable.insertmsgrecord(mytablemodel);

		} catch (org.springframework.dao.DuplicateKeyException e) {
			logger.error("主键冲突异常被捕获",e);
		}
	}
}
