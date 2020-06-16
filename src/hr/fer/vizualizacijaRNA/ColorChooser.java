package hr.fer.vizualizacijaRNA;

import java.awt.Color;
import java.util.Arrays;

public class ColorChooser {

	public static Color getNucleotideColor(char nucleotide){
		Color c;
		switch(Character.toUpperCase(nucleotide)){
		case 'A':
			c = Color.blue;
			break;
		case 'C':
			c = Color.red;
			break;
		case 'G':
			c = Color.green;
			break;
		case 'T':
			c = Color.yellow;
			break;
		case 'U':
			c = Color.orange;
			break;
		case '-':
			c = Color.LIGHT_GRAY;
		default:
			c = Color.black;
		}
		
		return c;
	}
	
	
	public static Color getReadColor(boolean reverseComplement, boolean isGap){
		if(isGap)
			return Color.GRAY;
		else if(reverseComplement)
			return Color.BLUE;
		else
			return Color.RED;

	}
	
	public static Color getAnnotationColor(char strand, boolean isGap){
		if(isGap)
			return Color.GRAY;
		else if(strand == '+')
			return Color.RED;
		else if(strand == '-')
			return Color.BLUE;
		else
			return Color.BLACK;
	}
	
	//vraća boju za iscrtavanje više nukleotida unutar širine od jednog piksela
	public static Color getMultipleNucleotideColor(int hPos, int n, int i, String currentSequence){
		int count[] = new int[5];
		for(int j = 0; j < n; j++){
			int pos = hPos + i * n + j;
			switch(Character.toUpperCase(currentSequence.charAt(pos))){
			case 'A':
				count[0]++;
				break;
			case 'C':
				count[1]++;
				break;
			case 'G':
				count[2]++;
				break;
			case 'T':
				count[3]++;
				break;
			default:
				count[4]++;
			}
		}
		int max = 0;
		for(int j = 1 ; j < 5; j++){
			if(count[j] > count[max])
				max = j;
		}
		
		if(count[max] > Arrays.stream(count).sum() / 2){
			switch(max){
			case 0:
				return Color.blue;
			case 1:
				return Color.red;
			case 2:
				return Color.green;
			case 3:
				return Color.yellow;
			default:
				return Color.black;
			}
		}
		else{
			return Color.gray;
		}
		
	}
}
