package hr.fer.vizualizacijaRNA;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ReadPanel extends JPanel{

	private static final int GAP_SIZE = 10;
	private static final int HISTOGRAM_OFFSET = 0;
	private static final int READS_OFFSET = HISTOGRAM_OFFSET + 100 + GAP_SIZE;
	
	private Dimension dimension;
	private Display display;
	private Map<String, Histogram> histogramMap;
	private Map<String, ArrayList<Read>> readMap;
	private ArrayList<Read> readsOnScreen = new ArrayList<>();
	private Read selectedRead;
	
	private Scrollbar scrollbar;
	
	public ReadPanel(Display display){
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent event) {
				if(selectedRead == null)
					return;
				if(SwingUtilities.isLeftMouseButton(event))
				{
					String name = "Name: " + selectedRead.getName();
					String cigar ="CIGAR: " + selectedRead.getCigar();
					String str = name + "\n" + cigar;
					StringSelection stringSelection = new StringSelection(str);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);
				}
				else if(SwingUtilities.isRightMouseButton(event)){
					selectedRead.setHighlighted(!selectedRead.isHighlighted());
					repaint();
				}
			}
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent event) {
				int x = event.getX();
				int y = event.getY();
				
				Read r = getReadOnPosition(x, y);
				if(r == null){
					setTooltip("");
					selectedRead = null;
					return;
				}
				else{
					selectedRead = r;
					String name = "Name: " + r.getName();
					String cigar ="CIGAR: " + r.getCigar();
					String str = "<html>" + name + "<br>" + cigar + "</html>";
					setTooltip(str);
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		dimension = new Dimension(display.getDim().width, 20);
		this.display = display;
		setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.black));
		this.setFocusable(true);
		
		repaint();
	}
	
	public void setTooltip(String s){
		this.setToolTipText(s);
	}
	
	//vraća očitanje na poziciji x, y unutar panela, vraća null ako takvo očitanje ne postoji
	public Read getReadOnPosition(int x, int y){
		double zoomMultiplier = display.getZoomLevel().getZoomMultiplier();
		int hPos = VisualizationControl.gethPos();
		int vPos = VisualizationControl.getReadvPos();
		if(readsOnScreen == null || x < READS_OFFSET)
			return null;
		
		for(Read r : readsOnScreen){
			int readStart = (int)((r.getPosStart() - hPos) / zoomMultiplier);
			int readEnd = readStart + (int)(r.getLength() / zoomMultiplier);
			int readvPos = READS_OFFSET + 30 * r.getvPos() - 30 * vPos;
			if(x > readStart && x < readEnd && y > readvPos && y < (readvPos + 20)){
				return r;
			}
		}
		return null;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		drawReads(g);
		drawHistogram(g);
	}

	private void drawHistogram(Graphics g){
		if(histogramMap == null)
			return;
		
		if(getCurrentHistogram() == null)
			return;
		
		int hPos = VisualizationControl.gethPos();
		int amplitude = getCurrentHistogram().getAmplitude();
		int histogramData[] = getCurrentHistogram().getData();
		String currentSequence = "chrI";
		
		int zoom = display.getZoomLevel().getZoom();
		
		if(!(currentSequence == null)){
			if(zoom >= 0){
				int recWidth;
				if(zoom == 0)
					recWidth = 1;
				else if(zoom == 1)
					recWidth = 5;
				else if(zoom == 2)
					recWidth = 10;
				else
					recWidth = 20;
				
				for(int i = 0; i < dimension.width/recWidth; i++){
					int l = (int)((double)histogramData[i + hPos] / amplitude * 100);
					g.setColor(Color.black);
					g.fillRect(i*recWidth, 100 - l + HISTOGRAM_OFFSET, recWidth, l);
				}
			}
			else if(zoom < 0){
				int n = (-zoom) * 5;
				for(int i = 0; i < dimension.width; i++){
					double avg = 0;
					for(int j = 0; j < n; j++){
						int pos = hPos + i * n + j;
						avg += histogramData[pos];
					}
					
					avg /= n;
					int l = (int)(avg / amplitude * 100);
					g.setColor(Color.black);
					g.fillRect(i, 100 - l + HISTOGRAM_OFFSET, 1, l);
				}
			}
			
		}
	}
	
	public void drawReads(Graphics g){
		readsOnScreen.clear();
		if(readMap == null) return;
		
		if(getCurrentReadList() == null) return;
		
		double zoomMultiplier = display.getZoomLevel().getZoomMultiplier();
		int hPos = VisualizationControl.gethPos();
		int vPos = VisualizationControl.getReadvPos();
		for(Read r : getCurrentReadList()){
			int positionOnScreen = r.positionOnScreen(hPos, dimension, zoomMultiplier);
			if(positionOnScreen == -1){
				continue;
			}
			else if(positionOnScreen == 1){
				break;
			}
			else{
				readsOnScreen.add(r);
				int pos = READS_OFFSET + 30 * r.getvPos() - 30 * vPos;
				if(pos < READS_OFFSET) continue;
				int readStart = (int)((r.getPosStart() - hPos) / zoomMultiplier);
				int rectLength = 0, rectStart = readStart, position = 0;
				//iscrtava očitanje
				for(Gap gap : r.getGapList()){
					rectStart = readStart + (int)(position / zoomMultiplier);
					rectLength = (int)((gap.getPosition() - position) / zoomMultiplier);
					g.setColor(ColorChooser.getReadColor(r.isReverseComplement(), false));
					g.fillRect(rectStart, pos, rectLength , 20);
					
					rectStart += rectLength;
					rectLength = (int)(gap.getLength() / zoomMultiplier);
					g.setColor(ColorChooser.getReadColor(r.isReverseComplement(), true));
					g.fillRect(rectStart, pos, rectLength, 20);
					position = gap.getPosition() + gap.getLength(); 
				}
				g.setColor(ColorChooser.getReadColor(r.isReverseComplement(), false));
				rectStart += rectLength;
				rectLength = (int)((r.getLength() - position) / zoomMultiplier);
				g.fillRect(rectStart, pos, rectLength , 20);
				
				//iscrtava obrub oko očitanja ako je potrebno
				if(r.isFindHighlighted()){
					rectStart = readStart;
					rectLength = (int)(r.getLength() / zoomMultiplier);
					drawHighlight(g, rectStart, rectLength, pos, Color.GREEN);
				}
				else if(r.isHighlighted()){
					rectStart = readStart;
					rectLength = (int)(r.getLength() / zoomMultiplier);
					drawHighlight(g, rectStart, rectLength, pos, Color.BLACK);
				}
			}
		}
		
	}
	
	//služi za crtanje obruba oko očitanja
	public void drawHighlight(Graphics g, int rectStart, int rectLength, int pos, Color color){
		g.setColor(color);
		g.fillRect(rectStart, pos, rectLength, 4);
		g.fillRect(rectStart, pos + 16, rectLength, 4);
		g.fillRect(rectStart, pos, 4, 20);
		g.fillRect(rectStart + rectLength - 4, pos, 4, 20);
	}
	
	public void createScrollbar(){
		if(getCurrentHistogram() == null)
			return;
		
		scrollbar = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 1, getCurrentHistogram().getAmplitude());
		scrollbar.setFocusable(false);
		scrollbar.addAdjustmentListener(new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int hPos = VisualizationControl.gethPos();
				int position = e.getValue() - 1;
				VisualizationControl.positionChanged(hPos, position);
			}
		});
		
		this.add(scrollbar, BorderLayout.EAST);
		revalidate();
		repaint();
	}
	
	public void zoomChanged(int zoom){
		repaint();
	}
	
	public void positionChanged(int hPos, int vPos){
		if(scrollbar != null)
			scrollbar.setValue(vPos);
		
		repaint();
	}
	
	public void sequenceChanged(String seq){
		if(scrollbar != null){
			this.remove(scrollbar);
			createScrollbar();
		}
	}
	
	//vraćanje na stanje prije učitavanja BED datoteke
	public void reset(){
		VisualizationControl.readsLoaded = false;
		readMap = null;
		histogramMap = null;
		if(scrollbar != null)
			this.remove(scrollbar);
	}

	public void setReadMap(Map<String, ArrayList<Read>> map){
		readMap = map;
	}
	
	public void setHistogramMap(Map<String, Histogram> map){
		histogramMap = map;
	}
	
	public ArrayList<Read> getCurrentReadList(){
		return readMap.get(VisualizationControl.getCurrentSequence());
	}
	
	public Histogram getCurrentHistogram(){
		return histogramMap.get(VisualizationControl.getCurrentSequence());
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
	
	
}

