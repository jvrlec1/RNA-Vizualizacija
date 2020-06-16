package hr.fer.vizualizacijaRNA;


import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingWorker;

public class Histogram {

	private int amplitude;
	private int length;
	private int[] data;
	
	public Histogram(SwingWorker<Void, Void> worker, int length, ArrayList<Read> readList){
		this.length = length;
		data = new int[length];
		for(Read r : readList){	
			try{
			if(worker.isCancelled())
				return;

			int currentPostion = r.getPosStart();
			for(Gap gap : r.getGapList()){
				for(int i = currentPostion; i < r.getPosStart() + gap.getPosition(); i++){
					data[i]++;
				}
				currentPostion = r.getPosStart() + gap.getPosition() + gap.getLength();
			}	
			for(int i = currentPostion; i < r.getPosStart() + r.getLength(); i++){
				data[i]++;
			}
			}
			catch(Exception e){
				
			}
		}
		
		
		amplitude = Arrays.stream(data).max().getAsInt();
	}
	
	public int getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(int amplitude) {
		this.amplitude = amplitude;
	}

	public int[] getData() {
		return data;
	}

	public void setData(int[] data) {
		this.data = data;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
}
