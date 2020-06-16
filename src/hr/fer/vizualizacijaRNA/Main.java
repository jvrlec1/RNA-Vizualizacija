package hr.fer.vizualizacijaRNA;

import javax.swing.SwingUtilities;


public class Main{
	
	public static void main(String[] args) {
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					Display window = new Display();
					
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
