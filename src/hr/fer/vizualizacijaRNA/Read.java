package hr.fer.vizualizacijaRNA;

import java.awt.Dimension;
import java.util.ArrayList;

public class Read implements Comparable<Read> {

	private String name;
	private String cigar;
	private int posStart, vPos;
	private int length = 0;
	private boolean reverseComplement;
	private boolean highlighted = false;
	private boolean findHighlighted = false;
	private ArrayList<Gap> gapList = new ArrayList<>();
	
	public Read(String name, String cigar, int posStart, int readLength, boolean reverseComplement){
		this.name = name;
		this.cigar = cigar;
		this.posStart = posStart;
		this.reverseComplement = reverseComplement;
		
		//računa duljinu očitanja iz CIGAR-a
		if(!cigar.equals("*")){
			String regex = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";
			String[] cigarData = cigar.split(regex);

			for(int i = 0; i < cigarData.length; i+= 2){
				int operationLength = Integer.parseInt(cigarData[i]);
				if(cigarData[i + 1].equals("N")){
					gapList.add(new Gap(length, operationLength));
					length += operationLength;
				}
				else if(!cigarData[i + 1].equals("P") && !cigarData[i + 1].equals("I")){
					length += operationLength;
				}
			}
		}
		else{
			length = readLength;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosStart() {
		return posStart;
	}

	public void setPosStart(int posStart) {
		this.posStart = posStart;
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
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isReverseComplement() {
		return reverseComplement;
	}

	public void setReverseComplement(boolean reverseComplement) {
		this.reverseComplement = reverseComplement;
	}
	

	public String getCigar() {
		return cigar;
	}

	public void setCigar(String cigar) {
		this.cigar = cigar;
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

	@Override
	public int compareTo(Read r) {
		return this.posStart - r.posStart;
	}
	
	public int positionOnScreen(int hPos, Dimension d, double zoomMultiplier){
		if(posStart + length < hPos)
			return -1;
		else if(posStart > hPos + zoomMultiplier * d.getWidth())
			return 1;
		else
			return 0;
	}
	
}
