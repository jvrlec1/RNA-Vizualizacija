package hr.fer.vizualizacijaRNA;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class RnaMenuBar extends JMenuBar{
	
	private final Display display;
	private JMenuItem menuFASTA;
	private JMenuItem menuSAM;
	private JMenuItem menuAnnotation;
	private JMenuItem menuFindRead;
	private JMenuItem menuFindAnnotation;
	
	private Read previousRead;
	private Annotation previousAnnotation;

	public RnaMenuBar(Display d){
		display = d;
		FileLoader.setDisplay(d);
		
		JMenu file = new JMenu("File");
		JMenu view = new JMenu("View");
		
		menuFASTA = new JMenuItem("Load FASTA File");
		openFASTA(menuFASTA);
		
		menuSAM = new JMenuItem("Load SAM File");
		menuSAM.setEnabled(false);
		openSAM(menuSAM);
		
		menuAnnotation = new JMenuItem("Load annotation file");
		menuAnnotation.setEnabled(false);
		openBED(menuAnnotation);
		
		
		file.add(menuFASTA);
		file.add(menuSAM);
		file.add(menuAnnotation);
		
		menuFindRead = new JMenuItem("Find read");
		menuFindRead.setEnabled(false);
		findRead(menuFindRead);
		
		menuFindAnnotation = new JMenuItem("Find annotation");
		menuFindAnnotation.setEnabled(false);
		findAnnotation(menuFindAnnotation);
		
		view.add(menuFindRead);
		view.add(menuFindAnnotation);
		
		this.add(file);
		this.add(view);
		
	}
	
	private void openFASTA(final JMenuItem item){
		item.addActionListener(new ActionListener() {
			
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			
			FileFilter ff = new FileNameExtensionFilter("All FASTA files", "fasta", "fa", "fna", "ffn", "faa", "frn");
			fileChooser.addChoosableFileFilter(ff);
			fileChooser.setFileFilter(ff);
			
			int result = fileChooser.showOpenDialog(item);
			if (result == JFileChooser.APPROVE_OPTION) {
				 File selectedFile = fileChooser.getSelectedFile();
				 FileLoader.loadFASTA(selectedFile);
			}
		}
	});
	}
	
	private void openSAM(final JMenuItem item){
		item.addActionListener(new ActionListener() {
			
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			
			FileFilter ff = new FileNameExtensionFilter("All SAM files", "sam");
			fileChooser.addChoosableFileFilter(ff);
			fileChooser.setFileFilter(ff);
			
			int result = fileChooser.showOpenDialog(item);
			if (result == JFileChooser.APPROVE_OPTION) {
				 File selectedFile = fileChooser.getSelectedFile();
				 FileLoader.loadSAM(selectedFile);
			}
		}
	});
	}
	
	private void openBED(final JMenuItem item){
		item.addActionListener(new ActionListener() {
			
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			
			FileFilter ff = new FileNameExtensionFilter("All annotation files", "bed");
			fileChooser.addChoosableFileFilter(ff);
			fileChooser.setFileFilter(ff);
			
			int result = fileChooser.showOpenDialog(item);
			if (result == JFileChooser.APPROVE_OPTION) {
				 File selectedFile = fileChooser.getSelectedFile();
				 FileLoader.loadBED(selectedFile);
			}
		}
	});
	}
	
	//popup prozori i sama pretraga anotacija
	private void findAnnotation(final JMenuItem item){
		item.addActionListener(new ActionListener() {
			
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String filter = JOptionPane.showInputDialog(null, "Enter annotation name:", "", JOptionPane.PLAIN_MESSAGE, null, null, "").toString();
			if(filter == null)
				return;
			else if(filter.equals("")){
				JOptionPane.showMessageDialog(null, "Didn't enter a name");
				return;
			}
			
			final ArrayList<Annotation> filteredAnnotations = new ArrayList<>();
			for(Annotation a : display.getAnnotations().getCurrentAnnotationList()){
				if(a.getName().toLowerCase().contains(filter.toLowerCase())){
					filteredAnnotations.add(a);
				}
			}
			if(filteredAnnotations.isEmpty()){
				JOptionPane.showMessageDialog(null, "No annotations found");
				return;
			}
			int numberOfMatches = filteredAnnotations.size();
			String message = "Found " + numberOfMatches + " matches.";
			JButton next = new JButton("Find next");
			Object[] dialogObjects = new Object[]{message, next};
			
			next.addActionListener(new SearchButtonListener(numberOfMatches, dialogObjects) {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(getCurrentMatch() + 1 > getNumberOfMatches())
						setCurrentMatch(1);
					else
						increment();
					
					Annotation matchedAnnotation = filteredAnnotations.get(getCurrentMatch() - 1);
					matchedAnnotation.setFindHighlighted(true);
					previousAnnotation.setFindHighlighted(false);
					display.getAnnotations().setvPos(matchedAnnotation.getvPos());
					VisualizationControl.positionChanged(matchedAnnotation.getStart(), VisualizationControl.getReadvPos());
					previousAnnotation = matchedAnnotation;
					
				}
			});
			
			filteredAnnotations.get(0).setFindHighlighted(true);
			previousAnnotation = filteredAnnotations.get(0);
			display.getAnnotations().setvPos(filteredAnnotations.get(0).getvPos());
			VisualizationControl.positionChanged(filteredAnnotations.get(0).getStart(), VisualizationControl.getReadvPos());
			
			JOptionPane.showMessageDialog(null, dialogObjects);
			previousAnnotation.setFindHighlighted(false);
			display.getAnnotations().repaint();
		}
	});
	}
	
	//popup prozori i sama pretraga očitanja
	private void findRead(final JMenuItem item){
		item.addActionListener(new ActionListener() {
			
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String filter = JOptionPane.showInputDialog(null, "Enter read name:", "", JOptionPane.PLAIN_MESSAGE, null, null, "").toString();
			if(filter == null)
				return;
			else if(filter.equals("")){
				JOptionPane.showMessageDialog(null, "Didn't enter a name");
				return;
			}
			
			final ArrayList<Read> filteredReads = new ArrayList<>();
			for(Read r : display.getReads().getCurrentReadList()){
				if(r.getName().toLowerCase().contains(filter.toLowerCase())){
					filteredReads.add(r);
				}
			}
			if(filteredReads.isEmpty()){
				JOptionPane.showMessageDialog(null, "No reads found");
				return;
			}
			int numberOfMatches = filteredReads.size();
			String message = "Found " + numberOfMatches + " matches.";
			JButton next = new JButton("Find next");
			Object[] dialogObjects = new Object[]{message, next};
			
			next.addActionListener(new SearchButtonListener(numberOfMatches, dialogObjects) {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(getCurrentMatch() + 1 > getNumberOfMatches())
						setCurrentMatch(1);
					else
						increment();
					
					String message = "Found " + getNumberOfMatches() + " matches.";
					setMessage(message);
					
					Read matchedRead = filteredReads.get(getCurrentMatch() - 1);
					matchedRead.setFindHighlighted(true);
					VisualizationControl.positionChanged(matchedRead.getPosStart(), matchedRead.getvPos());
					previousRead.setFindHighlighted(false);
					previousRead = matchedRead;
				}
			});
			
			filteredReads.get(0).setFindHighlighted(true);
			VisualizationControl.positionChanged(filteredReads.get(0).getPosStart(), filteredReads.get(0).getvPos());
			previousRead = filteredReads.get(0);
			
			
			JOptionPane.showMessageDialog(null, dialogObjects);
			previousRead.setFindHighlighted(false);
			display.getReads().repaint();
		}
	});
	}
	
	public void fastaLoaded(boolean isLoaded){
		menuSAM.setEnabled(isLoaded);
		menuAnnotation.setEnabled(isLoaded);
	}
	
	public void readsLoaded(boolean isLoaded){
		menuFindRead.setEnabled(isLoaded);
	}
	
	public void annotationsLoaded(boolean isLoaded){
		menuFindAnnotation.setEnabled(isLoaded);
	}
}
