package TestRatPackage;

public class RatName {
	String name;
	public RatName(String name) throws Exception {
		System.out.println("this name is "+name);
		this.name = name;
	}
	@Override
	public String toString() {
		return name == null ? "What the...":"my name is ["+name+"]";
	}
}
