package hr.fer.vizualizacijaRNA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class FileLoader {

	private static Display display;
	private static File fileFASTA, fileSAM, fileBED;
	private static boolean workingInBackground = false;
	private static List<SequencePosition> seqList = new ArrayList<>();
	private static JDialog dialog;
	
	//potpuni proces učitavanja FASTA datoteke nakon odabira datoteke
	public static void loadFASTA(final File file){
		String[] extensions = {"fasta", "fa", "fna", "ffn", "faa", "frn"};
		if(!isCorrectExtension(file, extensions)){
			JOptionPane.showMessageDialog(null, "Incorrect file type.");
			return;
		}
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				try{
					workingInBackground = true;
					VisualizationControl.reset();
					fileFASTA = file;
					setSequenceList(file);
					Map<String, String> sequences = readFASTA(this);
					if(sequences.equals("CANCELED"))
						return null;
					display.getSequence().setSequenceMap(sequences);
					VisualizationControl.setCurrentSequence(seqList.get(0).getName());
					display.getZoomLevel().loadSequence(seqList);
					VisualizationControl.setFastaLoaded(true);
				}
				catch (Exception e){
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			public void done() {
				workingInBackground = false;
				dialog.dispose();
			}
			
		};
		
		worker.execute();
		
		Object[] options = {"Cancel"};
		JOptionPane optionPane = new JOptionPane("Loading file...", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options);
		dialog = optionPane.createDialog(null, "");
		dialog.setVisible(true);
		if(workingInBackground){
			worker.cancel(true);
		}
			
		
		VisualizationControl.repaintAll();
	}
	
	//čitanje i obrada linija datoteke
	public static Map<String, String> readFASTA(SwingWorker<Void, Void> worker){
		String s;
		Map<String, String> sequencesMap = new TreeMap<String, String>();
		try {
			String currentSequence = "";
			FileReader fr = new FileReader(fileFASTA);
			BufferedReader br = new BufferedReader(fr);
			while ((s = br.readLine()) != null){
				if(worker.isCancelled()){
					br.close();
					sequencesMap.put("Status", "CANCELED");
					return sequencesMap;
				}
				
				if(s.startsWith(">")){
					currentSequence = s.split("\\s+")[0].substring(1);
					continue;
				}
				String sequence;
				if(sequencesMap.get(currentSequence) == null)
					sequence = s.replaceAll("\\s+","");
				else
					sequence = sequencesMap.get(currentSequence) + s.replaceAll("\\s+","");
				
				sequencesMap.put(currentSequence, sequence); 
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sequencesMap;
	}
	
	//potpuni proces učitavanja SAM datoteke nakon odabira datoteke
	public static void loadSAM(final File file){
		String[] extensions = {"sam"};
		if(!isCorrectExtension(file, extensions)){
			JOptionPane.showMessageDialog(null, "Incorrect file type.");
			return;
		}
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				workingInBackground = true;
				display.getReads().reset();
				fileSAM = file;
				readSAM(this);
				VisualizationControl.setReadsLoaded(true);
				VisualizationControl.setReadvPos(0);
				
				return null;
			}
			
			@Override
			public void done() {
				workingInBackground = false;
				dialog.dispose();
			}
			
		};
		
		worker.execute();
		
		Object[] options = {"Cancel"};
		JOptionPane optionPane = new JOptionPane("Loading file...", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options);
		dialog = optionPane.createDialog(null, "");
		dialog.setVisible(true);
		if(workingInBackground){
			worker.cancel(true);
		}
		

		VisualizationControl.repaintAll();
	}
	
	//čitanje i obrada linija datoteke
	private static void readSAM(SwingWorker<Void, Void> worker){
		Map <String, ArrayList<Read>> readMap = new TreeMap<>();
		String s;
		//boolean skipRead = false;
		try {
			Map<String, Histogram> histogramMap = new TreeMap<>();
			FileReader fr = new FileReader(fileSAM);
			BufferedReader br = new BufferedReader(fr);
			while ((s = br.readLine()) != null){
				if(worker.isCancelled()){
					br.close();
					return;
				}
				if(!s.startsWith("@")){
					String[] str = s.split("\\s+");
					if(!matchesSequence(str[2])){
						continue;
					}
					else{
						//provjerava postoji li ocitanje sa istim imenom vec u listi
						/*for(Read r : readList){
							if(r.getName().equals(str[0]) || !str[2].equals(sP.getName())){
								skipRead = true;
								break;
							}
						}
						if(skipRead){
							skipRead = false;
							continue;
						}*/
						if(readMap.get(str[2]) == null)
							readMap.put(str[2], new ArrayList<Read>());
						
						readMap.get(str[2]).add(createRead(str));
					}
				}
			}
			
			for(Map.Entry<String, ArrayList<Read>> entry : readMap.entrySet()){
				ArrayList<Read> list = new ArrayList<>(entry.getValue());
				Collections.sort(list);
				setReadLines(worker, list, entry.getKey());
				readMap.put(entry.getKey(), list);
				Histogram histogram = new Histogram(worker, display.getSequence().getSequenceLength(entry.getKey()), entry.getValue());
				histogramMap.put(entry.getKey(), histogram);
			}
			
			if(worker.isCancelled()){
				fr.close();
				return;
			}
			
			display.getReads().setHistogramMap(histogramMap);
			display.getReads().setReadMap(readMap);
			display.getReads().createScrollbar();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//potpuni proces učitavanja BED datoteke nakon odabira datoteke
	public static void loadBED(File file){
		String[] extensions = {"bed"};
		if(!isCorrectExtension(file, extensions)){
			JOptionPane.showMessageDialog(null, "Incorrect file type.");
			return;
		}
		display.getAnnotations().reset();
		Map<String, ArrayList<Annotation>> annotationMap = new TreeMap<>();
		fileBED = file;
		readBED(annotationMap);
		display.getAnnotations().setAnnotationMap(annotationMap);
		VisualizationControl.setAnnotationsLoaded(true);
		VisualizationControl.setAnnotaionvPos(0);
		display.getAnnotations().createScrollbar();
		VisualizationControl.repaintAll();
	}

	//čitanje i obrada linija datoteke
	public static void readBED(Map<String, ArrayList<Annotation>> annotationMap){
		FileReader fr;
		
		String s;
		try {
			fr = new FileReader(fileBED);
			BufferedReader br = new BufferedReader(fr);
			while ((s = br.readLine()) != null){
				String[] str = s.split("\\s+");
				if(!matchesSequence(str[0]))
					continue;
				
				ArrayList<Gap> gapList = new ArrayList<>();
				String name = str[3];
				int start = Integer.parseInt(str[1]);
				int end = Integer.parseInt(str[2]);
				char strand = '.';
				int thickStart = start, thickEnd = start;
				int[] rgb = new int[3];
				
				if(str.length >= 6){
					strand = str[5].charAt(0);
				}
				
				if(str.length >= 8){
					thickStart = Integer.parseInt(str[6]);
					thickEnd = Integer.parseInt(str[7]);
				}
				if(str.length >= 9){
					if(!str[8].equals("0")){
						String[] elements = str[8].split(",");
						for(int i = 0; i < elements.length; i++){
							rgb[i] = Integer.parseInt(elements[i]);
						}
					}
				}
				
				if(str.length >= 12){
					int blockCount = Integer.parseInt(str[9]);
					if(blockCount != 0 && blockCount != 1){
						String[] blockSizes = str[10].split(",");
						String[] blockStarts = str[11].split(",");
						for(int i = 1; i < blockCount; i++){
							int position = Integer.parseInt(blockStarts[i - 1]) + Integer.parseInt(blockSizes[i - 1]) + Integer.parseInt(str[1]);
							int length = Integer.parseInt(blockStarts[i]) + Integer.parseInt(str[1]) - position;
							Gap gap = new Gap(position, length);
							gapList.add(gap);
						}
					}
				}
				Collections.sort(gapList);
				if(annotationMap.get(str[0]) == null){
					annotationMap.put(str[0], new ArrayList<Annotation>());
				}
					
					
				annotationMap.get(str[0]).add(new Annotation(name, start, end, strand, thickStart, thickEnd, rgb, gapList));
			}
			br.close();
			for(Map.Entry<String, ArrayList<Annotation>> entry : annotationMap.entrySet()){
				ArrayList<Annotation> list = new ArrayList<>(entry.getValue());
				Collections.sort(list);
				setAnnotationLines(list, entry.getKey());
				annotationMap.put(entry.getKey(), list);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//postavlja se lista sljedova iz FASTA datoteke
	private static void setSequenceList(File f1) {
		int linecount = 0;
		FileReader fr;
		try {
			fr = new FileReader(f1);
			BufferedReader br = new BufferedReader(fr);
			String s;
			seqList.clear();
			
			while ((s = br.readLine()) != null)
			{
				linecount++;

				if(s.startsWith(">")){
					String[] str = s.split(" ");
					seqList.add(new SequencePosition(str[0].substring(1), linecount));
					
				}
			}
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//vraća pojedino očitanje stvoreno iz parametara
	private static Read createRead(String[] str){
		String name = str[0];
		String cigar = str[5];
		int readLength = str[9].length();
		int posStart = Integer.parseInt(str[3]);
		
		int flag = Integer.parseInt(str[1]);
		boolean reverseComplement = false;
		int flagBit = (flag >> 4) & 1;
		if(flagBit == 1){
			reverseComplement = true;
		}

		return new Read(name, cigar, posStart, readLength, reverseComplement);
	}
	
	//postavlja vertikalnu poziciju za svako očitanje
	private static void setReadLines(SwingWorker<Void, Void> worker, ArrayList<Read> readList, String sequenceName){
		SortedMap<Integer, Integer> map = new TreeMap<>();
		int nextRead = 0;
		int firstFree = 0;
		int sequenceLength = display.getSequence().getSequenceLength(sequenceName);
		for(int i = 0; i < sequenceLength; i++){
			if(worker.isCancelled()){
				return;
			}
			for(int j = nextRead; j < readList.size(); j++){
				if(readList.get(j).getPosStart() > i){
					break;
				}
				nextRead++;
				readList.get(j).setvPos(firstFree);
				map.put(firstFree, readList.get(j).getPosStart() + readList.get(j).getLength());
				for(int k = firstFree + 1; k <= readList.size() ; k++){
					if(!map.containsKey(k)){
						firstFree = k;
						break;
					}
				}	
			}
			
			Set<Integer> keys = new HashSet<Integer>();
			for(Entry<Integer, Integer> e : map.entrySet()){
				if(e.getValue().equals(i)){
					keys.add(e.getKey());
				}
			}
			for(Integer k : keys){
				if(k < firstFree) firstFree = k;
				map.remove(k);
			}
		}
	}
	
	//postavlja vertikalnu poziciju za svaku anotaciju
	private static void setAnnotationLines(ArrayList<Annotation> annotationList, String sequenceName){
		SortedMap<Integer, Integer> map = new TreeMap<>();
		int nextAnnotation = 0;
		int amplitude = -1;
		int firstFree = 0;
		int sequenceLength = display.getSequence().getSequenceLength(sequenceName);
		for(int i = 0; i < sequenceLength; i++){
			for(int j = nextAnnotation; j < annotationList.size(); j++){
				if(annotationList.get(j).getStart() > i){
					break;
				}
				nextAnnotation++;
				annotationList.get(j).setvPos(firstFree);
				map.put(firstFree, annotationList.get(j).getStart() + annotationList.get(j).getLength());
				for(int k = firstFree + 1; k <= annotationList.size() ; k++){
					if(!map.containsKey(k)){
						firstFree = k;
						break;
					}
				}
				if(amplitude < firstFree)
					amplitude = firstFree;
			}
			
			Set<Integer> keys = new HashSet<Integer>();
			for(Entry<Integer, Integer> e : map.entrySet()){
				if(e.getValue().equals(i)){
					keys.add(e.getKey());
				}
			}
			for(Integer k : keys){
				if(k < firstFree) firstFree = k;
				map.remove(k);
			}
		}
		
		if(display.getAnnotations().getAmplitude() < amplitude)
			display.getAnnotations().setAmplitude(amplitude);
	}

	public static boolean isCorrectExtension(File file, String[] extensions){
		for(String s : extensions){
			if(file.getName().toLowerCase().endsWith("." + s))
				return true;
		}
		
		return false;
	}
	
	//provjerava odgovara li primljeni znakovni niz jednom od sljedova reference
	public static boolean matchesSequence(String s){
		for(String sequenceName : display.getSequence().getSequenceNames()){
			if(s.equals(sequenceName))
				return true;
		}
		
		return false;
	}
	
	public static void setDisplay(Display display) {
		FileLoader.display = display;
	}

	public static List<SequencePosition> getSeqList() {
		return seqList;
	}
	
	
}
