package hr.fer.vizualizacijaRNA;

public class SequencePosition {

	private String name;
	private int position;
	
	public SequencePosition(String s, int n){
		name = s;
		position = n;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public String toString(){
		return "Name:" + name + ", position:" + position;
	}
	
}
