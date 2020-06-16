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

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class AnnotationPanel extends JPanel{

	private Display display;
	private Dimension dimension;
	private Map<String, ArrayList<Annotation>> annotationMap;
	private int hPos = 0, vPos = 0;
	private int amplitude;
	private Scrollbar scrollbar;
	private ArrayList<Annotation> annotationsOnScreen = new ArrayList<>();
	private Annotation selectedAnnotation;
	
	public AnnotationPanel(Display display){
		this.display = display;
		dimension = new Dimension(display.getDim());
		dimension.height = 50;
		
		setLayout(new BorderLayout());
		this.setPreferredSize(dimension);
		
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
				if(selectedAnnotation == null)
					return;
				if(SwingUtilities.isLeftMouseButton(event))
				{
					String name = "Name: " + selectedAnnotation.getName();
					StringSelection stringSelection = new StringSelection(name);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);
				}
				else if(SwingUtilities.isRightMouseButton(event)){
					selectedAnnotation.setHighlighted(!selectedAnnotation.isHighlighted());
					repaint();
				}
			}
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent event) {
				int x = event.getX();
				int y = event.getY();
				
				Annotation a = getAnnotationOnPosition(x, y);
				if(a == null){
					setTooltip("");
					selectedAnnotation = null;
					return;
				}
				else{
					selectedAnnotation = a;
					String name = "Name: " + a.getName();
					setTooltip(name);
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void setTooltip(String s){
		this.setToolTipText(s);
	}
	
	//vraća anotaciju na poziciji x, y unutar panela, vraća null ako takva anotacija ne postoji
	public Annotation getAnnotationOnPosition(int x, int y){
		double zoomMultiplier = display.getZoomLevel().getZoomMultiplier();
		int hPos = VisualizationControl.gethPos();
		int vPos = VisualizationControl.getAnnotaionvPos();
		if(annotationsOnScreen == null)
			return null;
		
		for(Annotation a : annotationsOnScreen){
			int annotationStart = (int)((a.getStart() - hPos) / zoomMultiplier);
			int annotationEnd = annotationStart + (int)(a.getLength() / zoomMultiplier);
			int annotationvPos = 30 * a.getvPos() - 30 * vPos;
			if(x > annotationStart && x < annotationEnd && y > annotationvPos && y < (annotationvPos + 20)){
				return a;
			}
		}
		return null;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		drawAnnotations(g);
	}
	
	private void drawAnnotations(Graphics g) {
		annotationsOnScreen.clear();
		if(annotationMap == null)
			return;
		
		if(getCurrentAnnotationList() == null)
			return;
		
		for(Annotation a : getCurrentAnnotationList()){
			double zoomMultiplier = display.getZoomLevel().getZoomMultiplier();
			int positionOnScreen = a.positionOnScreen(hPos, dimension, zoomMultiplier);
			
			if(positionOnScreen == -1){
				continue;
			}
			else if(positionOnScreen == 1){
				break;
			}
			annotationsOnScreen.add(a);
			int pos = 30 * a.getvPos() - 30 * vPos;
			int rectStart = (int)((a.getStart() - hPos) / zoomMultiplier);
			int rectLength = (int)((a.getEnd() - a.getStart()) / zoomMultiplier);
			//iscrtava anotaciju
			g.setColor(ColorChooser.getAnnotationColor(a.getStrand(), false));
			g.fillRect(rectStart, pos + 5, rectLength , 10);
			
			rectStart = (int)((a.getThickStart() - hPos) / zoomMultiplier);
			rectLength = (int)((a.getThickEnd() - a.getThickStart()) / zoomMultiplier);
			g.fillRect(rectStart, pos, rectLength , 20);
			
			//iscrtava praznine(introne)
			g.setColor(ColorChooser.getAnnotationColor(a.getStrand(), true));
			for(Gap gap : a.getGapList()){
				rectStart = (int)((gap.getPosition() - hPos) / zoomMultiplier);
				rectLength = (int)(gap.getLength() / zoomMultiplier);
				g.fillRect(rectStart, pos, rectLength, 20);
			}
			
			//iscrtava obrub ako je potrebno
			if(a.isFindHighlighted()){
				rectStart = (int)((a.getStart() - hPos) / zoomMultiplier);
				rectLength = (int)((a.getEnd() - a.getStart()) / zoomMultiplier);
				drawHighlight(g, rectStart, rectLength, pos, Color.GREEN);
			}
			else if(a.isHighlighted()){
				rectStart = (int)((a.getStart() - hPos) / zoomMultiplier);
				rectLength = (int)((a.getEnd() - a.getStart()) / zoomMultiplier);
				drawHighlight(g, rectStart, rectLength, pos, Color.BLACK);
			}
			
		}
	}
	
	//služi za crtanje obruba oko anotacije
	public void drawHighlight(Graphics g, int rectStart, int rectLength, int pos, Color color){
		g.setColor(color);
		g.fillRect(rectStart, pos, rectLength, 4);
		g.fillRect(rectStart, pos + 16, rectLength, 4);
		g.fillRect(rectStart, pos, 4, 20);
		g.fillRect(rectStart + rectLength - 4, pos, 4, 20);
	}
	
	public void positionChanged(int hPos){
		this.hPos = hPos;
		repaint();
	}
	
	public void zoomChanged(int zoom){
		repaint();
	}
	
	public void sequenceChanged(String seq){
		hPos = VisualizationControl.gethPos();
		vPos = 0;
		
		if(scrollbar != null){
			this.remove(scrollbar);
			createScrollbar();
		}
		
		repaint();
	}
	
	//vraćanje na stanje prije učitavanja BED datoteke
	public void reset(){
		VisualizationControl.annotationsLoaded = false;
		vPos = 0;
		amplitude = 0;
		annotationMap = null;
		if(scrollbar != null)
			this.remove(scrollbar);
	}
	
	public void createScrollbar(){
		scrollbar = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 1, amplitude);
		scrollbar.setFocusable(false);
		scrollbar.addAdjustmentListener(new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int position = e.getValue();
				vPos = position - 1;
				repaint();
			}
		});
		
		this.add(scrollbar, BorderLayout.EAST);
		revalidate();
		repaint();
	}

	public Dimension getDim() {
		return dimension;
	}

	public void setDim(Dimension dim) {
		this.dimension = dim;
	}
	
	public void setAnnotationMap(Map<String, ArrayList<Annotation>> annotationMap){
		this.annotationMap = annotationMap;
	}
	
	public ArrayList<Annotation> getCurrentAnnotationList(){
		return annotationMap.get(VisualizationControl.getCurrentSequence());
	}

	public int getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(int amplitude) {
		this.amplitude = amplitude;
	}

	public int gethPos() {
		return hPos;
	}

	public void sethPos(int hPos) {
		this.hPos = hPos;
	}

	public int getvPos() {
		return vPos;
	}

	public void setvPos(int vPos) {
		this.vPos = vPos;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
	
	
}
