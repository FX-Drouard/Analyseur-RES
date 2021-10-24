package input_Test;

import input.Input_Parser;
import java.io.IOException;

public class Input_Parser_Test {
	
	public static void main(String args[]) throws IOException{
		String res=Input_Parser.parse("data/exemple.txt");
		System.out.println("Trame brut"+res);
		String[] tmp=Input_Parser.split(res);
		String[] ethernet=Input_Parser.ethernetData(tmp);
		System.out.println("\n\nTram Analysée:");
		System.out.println(Input_Parser.ethernetToString(ethernet));
		
	}
	
}
