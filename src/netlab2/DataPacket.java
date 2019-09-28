package netlab2;
public class DataPacket {
	private int type;
	private Object data;
	int getType() {
		return type;
	}
	void setType(int type) {
		this.type = type; 
	}
	Object getData() {
		return data;
	}
	
	void setData(Object data) {
		this.data = data;
	}
	
}
