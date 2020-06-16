package hr.fer.vizualizacijaRNA;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Display extends JFrame implements AWTEventListener{
	
	private Dimension dim;
	private RulerPanel zoomLevel;
	private ReadPanel reads;
	private ReferencePanel sequence;
	private AnnotationPanel annotations;
	private JPanel centerPanel;
	private RnaMenuBar menuBar;
	
	public Display(){
		setLayout(new BorderLayout());
		
		this.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
		
		menuBar = new RnaMenuBar(this);
		dim = new Dimension(800, 600);
		this.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentResized(ComponentEvent event) {
				dim = event.getComponent().getBounds().getSize();
				VisualizationControl.updateFrameSize();
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		setMinimumSize(dim);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("RNA Vizualizacija");
		setJMenuBar(menuBar);
		pack();
		setVisible(true);
		
		reads = new ReadPanel(this);
		zoomLevel = new RulerPanel(this);
		sequence = new ReferencePanel(this);
		annotations = new AnnotationPanel(this);
		VisualizationControl.setDisplay(this);
		VisualizationControl.setPanels(annotations, reads, zoomLevel, sequence);
		centerPanel = new JPanel();
		
		this.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(reads, BorderLayout.CENTER);
		centerPanel.add(annotations, BorderLayout.NORTH);
		
		this.add(zoomLevel, BorderLayout.NORTH);
		this.add(sequence, BorderLayout.SOUTH);
	}
	
	//pomicanje po sekvenci i očitanjima pritiskom strelica na tipkovnici
	@Override
	public void eventDispatched(AWTEvent e) {
		if(e instanceof KeyEvent){
			KeyEvent key = (KeyEvent)e;
			if(key.getID() == KeyEvent.KEY_PRESSED){
				int keyPressed = key.getKeyCode();
				
				int hPos = VisualizationControl.gethPos();
				int vPos = VisualizationControl.getReadvPos();
				int hPosChange = (int)(zoomLevel.getZoomMultiplier() * 200);
				int vPosChange = 1;
				
				if(keyPressed == KeyEvent.VK_LEFT){
					if(hPos - hPosChange < 0)
						hPos = 0;
					else
						hPos -= hPosChange;
				}
				else if(keyPressed == KeyEvent.VK_RIGHT){
					hPos += hPosChange;
				}
				else if(keyPressed == KeyEvent.VK_UP){
					if(vPos - vPosChange < 0)
						vPos = 0;
					else
						vPos -= vPosChange;
				}
				else if(keyPressed == KeyEvent.VK_DOWN){
					vPos += vPosChange;
				}
				VisualizationControl.positionChanged(hPos, vPos);
				key.consume();
			}
		}
		
	}


	public RulerPanel getZoomLevel() {
		return zoomLevel;
	}


	public ReadPanel getReads() {
		return reads;
	}


	public ReferencePanel getSequence() {
		return sequence;
	}


	public Dimension getDim() {
		return dim;
	}


	public AnnotationPanel getAnnotations() {
		return annotations;
	}

	
	public RnaMenuBar getRnaMenuBar() {
		return menuBar;
	}


	
}
