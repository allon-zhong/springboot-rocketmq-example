package rocketmq_example.mqandmysqltraction;

import java.io.Serializable;

public class MyTableModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -170514782700539333L;
	
	private Integer id;
	private String column1;
	private String column2;
	private String column3;
	private String column4;
	private String column5;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getColumn1() {
		return column1;
	}
	public void setColumn1(String column1) {
		this.column1 = column1;
	}
	public String getColumn2() {
		return column2;
	}
	public void setColumn2(String column2) {
		this.column2 = column2;
	}
	public String getColumn3() {
		return column3;
	}
	public void setColumn3(String column3) {
		this.column3 = column3;
	}
	public String getColumn4() {
		return column4;
	}
	public void setColumn4(String column4) {
		this.column4 = column4;
	}
	public String getColumn5() {
		return column5;
	}
	public void setColumn5(String column5) {
		this.column5 = column5;
	}
	
	
}
