package hr.fer.vizualizacijaRNA;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class RulerPanel extends JPanel{

	private Dimension dimension;
	private Display display;
	private int zoom, hPos;
	private String currentSequence = "";
	private JComboBox<String> chromosomeSelect;
	private JPanel buttonPanel;
	private JButton zoomPlus;
	private JButton zoomMinus;
	private JButton navigation;
	private JLabel bp;
	
	public RulerPanel(final Display display){
		this.display = display;
		zoom = 0;
		hPos = 0;
		
		zoomPlus = new JButton("Zoom+");
		zoomPlus.setEnabled(false);
		zoomMinus = new JButton("Zoom-");
		zoomMinus.setEnabled(false);
		navigation = new JButton("Jump to");
		navigation.setEnabled(false);
		
		buttonPanel = new JPanel();
		chromosomeSelect = new JComboBox<String>();
		chromosomeSelect.setEnabled(false);
		bp = new JLabel("");
		
		zoomPlus.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				zoom += 1;
				VisualizationControl.zoomChanged(zoom);
			}
		});
		
		zoomMinus.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				zoom -= 1;
				VisualizationControl.zoomChanged(zoom);
			}
		});
		
		chromosomeSelect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String s = (String)chromosomeSelect.getSelectedItem();
				if(!s.equals(currentSequence)){
					VisualizationControl.sequenceChanged(s);
				}
				
			}
		});
		
		navigation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int sequenceLength = display.getSequence().getCurrentSequenceLength();
				String message = "Select position to jump to(0 - " + sequenceLength + "):";
				String input = "";
				int pos;
				try{
					input = JOptionPane.showInputDialog(message);
					pos = Integer.parseInt(input);
				}
				catch(Exception e){
					if(input == null || input.length() == 0)
						return;
					JOptionPane.showMessageDialog(null, "Input not a whole number.", "InfoBox: " + "", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				
				if(pos < 0 || pos > sequenceLength){
					JOptionPane.showMessageDialog(null, "Wrong input parameters.", "InfoBox: " + "", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				VisualizationControl.positionChanged(pos, VisualizationControl.getReadvPos());
			}
		});
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		buttonPanel.add(chromosomeSelect);
		buttonPanel.add(zoomMinus);
		buttonPanel.add(zoomPlus);
		buttonPanel.add(navigation);
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		bp.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(buttonPanel);
		this.add(bp);
		
		dimension = new Dimension();
		dimension.width = display.getDim().width;
		dimension.height = 100;
		buttonPanel.setMaximumSize(new Dimension(dimension.width, 40));
		this.setPreferredSize(dimension);
		
		repaint();
	}
	
	public void zoomChanged(int zoom){
		this.zoom = zoom;
		
		if(zoom == 3){
			zoomPlus.setEnabled(false);
		}	
		else if(zoom == -5){
			zoomMinus.setEnabled(false);
		}
		else{
			zoomPlus.setEnabled(true);
			zoomMinus.setEnabled(true);
		}
		
		if(zoom == 0){
			bp.setText("Screen size: " + (int)dimension.width + " bp");
		}
		else{
			bp.setText("Screen size: " + (int)(dimension.width * getZoomMultiplier()) + " bp");
		}
		
		repaint();
	}
	
	public void positionChanged(int hPos){
		this.hPos = hPos;
		
		repaint();
	}
	
	public void sequenceChanged(String seq){
		currentSequence = seq;
		positionChanged(0);
		zoomChanged(0);
	}
	
	//vraća se na stanje prije učitavanja FASTA datoteke
	public void reset(){
		zoom = 0;
		hPos = 0;
		
		zoomPlus.setEnabled(false);
		zoomMinus.setEnabled(false);
		navigation.setEnabled(false);
		bp.setText("");
		
		currentSequence = "";
		chromosomeSelect.setModel(new JComboBox<String>().getModel());
	}
	
	public int getZoom(){
		return zoom;
	}
	
	public void enableRuler() {
		zoomPlus.setEnabled(true);
		zoomMinus.setEnabled(true);
		navigation.setEnabled(true);
		bp.setText("Screen size: " + dimension.width * getZoomMultiplier() + " bp");
	}

	public double getZoomMultiplier(){
		if(zoom == 0)
			return 1;
		else if(zoom > 0)
			return 1.0 / (5 * zoom);
		else
			return 1.0 * 5 * (-zoom);
			
	}
	
	//poziva se pri učitavanju FASTA datoteke
	public void loadSequence(List<SequencePosition> seqList){
		if(seqList.size() == 1){
			chromosomeSelect.setEnabled(false);
		}
		else{
			for(SequencePosition i : seqList){
				chromosomeSelect.addItem(i.getName());
			}
			chromosomeSelect.setEnabled(true);
		}
	}
	
	//iscrtava ravnalo
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(VisualizationControl.fastaLoaded){
			int lineHeight = dimension.height - 10;
			g.fillRect(0, lineHeight, dimension.width, 1);
			for(int i = 0; i <= dimension.width/100 + 1; i++){
				String str = "" + (int)(hPos + (i * getZoomMultiplier()) * 100) + " bp";
				int linePos = i * 100;
				int strPos = g.getFontMetrics().stringWidth(str);
				
				g.setFont(new Font("Arial Black", Font.BOLD, 15));
				g.fillRect(linePos, lineHeight - 15, 1, 15);
				g.drawString(str, linePos - strPos / 2, 80);
			}
		}	
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
	
	
}