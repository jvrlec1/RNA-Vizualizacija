package hr.fer.vizualizacijaRNA;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchButtonListener implements ActionListener {
	
	private int currentMatch;
	private int numberOfMatches;
	private Object[] dialogObjects;

	public SearchButtonListener(int numberOfMatches, Object[] dialogObjects){
		super();
		currentMatch = 1;
		this.numberOfMatches = numberOfMatches;
		this.dialogObjects = dialogObjects;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public int getCurrentMatch() {
		return currentMatch;
	}

	public void setCurrentMatch(int currentMatch) {
		this.currentMatch = currentMatch;
	}

	public int getNumberOfMatches() {
		return numberOfMatches;
	}

	public void setNumberOfMatches(int numberOfMatches) {
		this.numberOfMatches = numberOfMatches;
	}
	
	public void increment(){
		currentMatch++;
	}

	public String getMessage() {
		return dialogObjects[0].toString();
	}

	public void setMessage(String message) {
		dialogObjects[0] = message;
	}
	

}
