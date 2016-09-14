package util;

public class StringBox {
	
	public StringBox() {
		str = null;
	}
	
	public StringBox(String str) {
		this.str = str;
	}
	
	public String getString() {
		return str;
	}
	
	public void setString(String newStr) {
		str = newStr;
	}
	
	public String toString() {
		return str;
	}
	
	private String str;
}
