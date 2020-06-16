package hr.fer.vizualizacijaRNA;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.swing.JPanel;

public class ReferencePanel extends JPanel {
	
	private Display display;
	private Dimension dimension;
	private Map<String, String> sequences;
	private int hPos = 0;
	private int zoom = 0;

	public ReferencePanel(Display display) {
		
		this.display = display;
		dimension = new Dimension(display.getDim().width, 20);
		this.setPreferredSize(dimension);
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(!(sequences == null)){
			String currentSequence = sequences.get(VisualizationControl.getCurrentSequence());
			if(zoom == 0 || zoom == 1){
				int recWidth = 1 + 4*zoom;
				for(int i = 0; i < dimension.width/recWidth; i++){
					g.setColor(ColorChooser.getNucleotideColor(currentSequence.charAt(i + hPos)));
					g.fillRect(i*recWidth, dimension.height - 20, recWidth, 20);
				}
			}
			else if(zoom < 0){
				int n = (-zoom) * 5;
				for(int i = 0; i < dimension.width; i++){
					g.setColor(ColorChooser.getMultipleNucleotideColor(hPos, n, i, currentSequence));
					g.fillRect(i, dimension.height - 20, 1, 20);
				}
			}
			else{
				int fontSize = (zoom - 1) * 10;
				
				g.setFont(new Font("Arial Black", Font.BOLD, fontSize));
				for(int i = 0; i < dimension.width / fontSize; i++){
					g.setColor(ColorChooser.getNucleotideColor(currentSequence.charAt(i + hPos)));
					g.drawString(currentSequence.substring(i + hPos, i + hPos + 1), i * fontSize, fontSize);
					
				}
			}
			
		}
		
	}

	public void zoomChanged(int zoom){
		this.zoom = zoom;
		
		repaint();
	}
	
	public void positionChanged(int hPos){
		this.hPos = hPos;
		
		repaint();
	}
	
	public void sequenceChanged(String seq){
		zoom = 0;
		hPos = 0;
		
		repaint();
	}
	
	public void reset(){
		VisualizationControl.fastaLoaded = false;
		zoom = 0;
		hPos = 0;
		
		sequences = null;
	}

	public int getPos() {
		return hPos;
	}

	public void setPos(int pos) {
		this.hPos = pos;
	}
	
	public void setSequenceMap(Map<String, String> map){
		sequences = map;
	}
	
	public int getCurrentSequenceLength(){
		return sequences.get(VisualizationControl.getCurrentSequence()).length();
	}
	
	public int getSequenceLength(String sequenceName){
		return sequences.get(sequenceName).length();
	}
	
	public ArrayList<String> getSequenceNames(){
		ArrayList<String> names = new ArrayList<>();
		for(Map.Entry<String, String> entry : sequences.entrySet()){
			names.add(entry.getKey());
		}
		
		return names;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
	
	
}
