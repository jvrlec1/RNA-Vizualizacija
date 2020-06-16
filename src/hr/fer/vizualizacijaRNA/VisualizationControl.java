package hr.fer.vizualizacijaRNA;

import java.awt.Dimension;

public class VisualizationControl {

	static int hPos = 0;
	static int readvPos = 0;
	static int annotaionvPos = 0;
	static String currentSequence = "";
	static boolean fastaLoaded = false;
	static boolean readsLoaded = false;
	static boolean annotationsLoaded = false;
	static Display display;
	static AnnotationPanel annotations;
	static ReadPanel reads;
	static RulerPanel zoomLevel;
	static ReferencePanel sequence;
	
	public static void setPanels(AnnotationPanel annotations, ReadPanel reads, RulerPanel zoomLevel, ReferencePanel sequence){
		VisualizationControl.annotations = annotations;
		VisualizationControl.reads = reads;
		VisualizationControl.zoomLevel = zoomLevel;
		VisualizationControl.sequence = sequence;
	}
	
	public static void setDisplay(Display display){
		VisualizationControl.display = display;
	}
	
	public static void repaintAll(){
		zoomLevel.repaint();
		sequence.repaint();
		reads.repaint();
		annotations.repaint();
	}
	
	public static void zoomChanged(int zoom){
		zoomLevel.zoomChanged(zoom);
		sequence.zoomChanged(zoom);
		reads.zoomChanged(zoom);
		annotations.zoomChanged(zoom);
	}
	
	public static void positionChanged(int hPos, int vPos){
		int hPosMax = sequence.getCurrentSequenceLength() - (int)(display.getDim().width * zoomLevel.getZoomMultiplier());
		if(hPos > hPosMax)
			VisualizationControl.hPos = hPosMax;
		else
			VisualizationControl.hPos = hPos;
		
		VisualizationControl.readvPos = vPos;
		
		zoomLevel.positionChanged(VisualizationControl.hPos);
		sequence.positionChanged(VisualizationControl.hPos);
		reads.positionChanged(VisualizationControl.hPos, vPos);
		annotations.positionChanged(VisualizationControl.hPos);
	}
	
	public static void sequenceChanged(String seq){
		setCurrentSequence(seq);
		hPos = 0;
		readvPos = 0;
		
		zoomLevel.sequenceChanged(seq);
		sequence.sequenceChanged(seq);
		reads.sequenceChanged(seq);
		annotations.sequenceChanged(seq);
	}
	
	public static void reset(){
		hPos = 0;
		readvPos = 0;
		annotaionvPos = 0;
		setFastaLoaded(false);
		setReadsLoaded(false);
		setAnnotationsLoaded(false);
		
		zoomLevel.reset();
		sequence.reset();
		reads.reset();
		annotations.reset();
	}
	
	public static void updateFrameSize(){
		sequence.setDimension(new Dimension(display.getDim().width, sequence.getDimension().height));
		reads.setDimension(new Dimension(display.getDim().width, reads.getDimension().height));
		annotations.setDimension(new Dimension(display.getDim().width, annotations.getDimension().height));
		zoomLevel.setDimension(new Dimension(display.getDim().width, zoomLevel.getDimension().height));
		zoomLevel.zoomChanged(zoomLevel.getZoom());
		repaintAll();
	}
	
	public static int gethPos() {
		return hPos;
	}
	public static void sethPos(int hPos) {
		VisualizationControl.hPos = hPos;
	}
	public static int getReadvPos() {
		return readvPos;
	}
	public static void setReadvPos(int readvPos) {
		VisualizationControl.readvPos = readvPos;
	}
	public static int getAnnotaionvPos() {
		return annotaionvPos;
	}
	public static void setAnnotaionvPos(int annotaionvPos) {
		VisualizationControl.annotaionvPos = annotaionvPos;
	}
	public static boolean isFastaLoaded() {
		return fastaLoaded;
	}
	public static void setFastaLoaded(boolean fastaLoaded) {
		VisualizationControl.fastaLoaded = fastaLoaded;
		display.getRnaMenuBar().fastaLoaded(fastaLoaded);
		if(fastaLoaded = true)
			zoomLevel.enableRuler();
	}
	public static boolean isReadsLoaded() {
		return readsLoaded;
	}
	public static void setReadsLoaded(boolean readsLoaded) {
		VisualizationControl.readsLoaded = readsLoaded;
		display.getRnaMenuBar().readsLoaded(readsLoaded);
	}
	public static boolean isAnnotationsLoaded() {
		return annotationsLoaded;
	}
	public static void setAnnotationsLoaded(boolean annotationsLoaded) {
		VisualizationControl.annotationsLoaded = annotationsLoaded;
		display.getRnaMenuBar().annotationsLoaded(annotationsLoaded);
	}

	public static String getCurrentSequence() {
		return currentSequence;
	}

	public static void setCurrentSequence(String currentSequence) {
		VisualizationControl.currentSequence = currentSequence;
	}
	
}
