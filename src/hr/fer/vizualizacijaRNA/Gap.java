package hr.fer.vizualizacijaRNA;

public class Gap implements Comparable<Gap> {

	private int position;
	private int length;
	
	public Gap(int position, int length){
		this.position = position;
		this.length = length;
	}

	@Override
	public int compareTo(Gap gap) {
		return this.position - gap.position;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	
}
