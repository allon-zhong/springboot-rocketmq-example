package rocketmq_example.mqandmysqltraction;

import java.util.List;


public interface IMytableMapper {

	
	public int batchinsertmytable(List<MyTableModel> listrestlt); 

	public int insertmytable(MyTableModel mytablemodel); 

	public MyTableModel selectMyTableModelById(int id);
}
