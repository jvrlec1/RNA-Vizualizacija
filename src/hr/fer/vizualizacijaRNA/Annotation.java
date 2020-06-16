package hr.fer.vizualizacijaRNA;

import java.awt.Dimension;
import java.util.ArrayList;

public class Annotation implements Comparable<Annotation>{
	
	private String name;
	private int start, end, vPos;
	private char strand;
	private int thickStart, thickEnd;
	private int[] rgb;
	private ArrayList<Gap> gapList = new ArrayList<>();
	private boolean highlighted = false;
	private boolean findHighlighted = false;
	

	public Annotation(String name, int start, int end, char strand, int thickStart, int thickEnd, int[] rgb, ArrayList<Gap> gapList) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.thickStart = thickStart;
		this.thickEnd = thickEnd;
		this.rgb = rgb;
		this.gapList = gapList;
	}
	
	//vraća -1 ako je anotacija lijevo izvan ekrana, 0 ako je barem dio anotacije unutar ekrana, 1 ako je desno izvan ekrana
	public int positionOnScreen(int hPos, Dimension d, double zoomMultiplier){
		if(end < hPos)
			return -1;
		else if(start > hPos + zoomMultiplier * d.getWidth())
			return 1;
		else
			return 0;
	}
	
	@Override
	public int compareTo(Annotation a) {
		
		return start - a.getStart();
	}
	
	public int getLength(){
		return end - start;
	}


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getEnd() {
		return end;
	}


	public void setEnd(int end) {
		this.end = end;
	}


	public int getThickStart() {
		return thickStart;
	}


	public void setThickStart(int thickStart) {
		this.thickStart = thickStart;
	}


	public int getThickEnd() {
		return thickEnd;
	}


	public void setThickEnd(int thickEnd) {
		this.thickEnd = thickEnd;
	}


	public int[] getRgb() {
		return rgb;
	}


	public void setRgb(int[] rgb) {
		this.rgb = rgb;
	}

	public char getStrand() {
		return strand;
	}

	public void setStrand(char strand) {
		this.strand = strand;
	}

	public ArrayList<Gap> getGapList() {
		return gapList;
	}

	public void setGapList(ArrayList<Gap> gapList) {
		this.gapList = gapList;
	}

	public int getvPos() {
		return vPos;
	}

	public void setvPos(int vPos) {
		this.vPos = vPos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public boolean isFindHighlighted() {
		return findHighlighted;
	}

	public void setFindHighlighted(boolean findHighlighted) {
		this.findHighlighted = findHighlighted;
	}
	
}
