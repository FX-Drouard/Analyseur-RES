package input_Test;

import input.Input_Parser;
import java.io.IOException;
import input.Lanceur;

public class Input_Parser_Test {
	private static Lanceur la;
	private static int choix=0;
	
	public static void main(String args[]) throws IOException{
		String path = "data/exemple5.txt";
		la=new Lanceur(path);
		String [][] temp=Input_Parser.parse(path);
		System.out.println("\n");
		System.out.println(temp.length);
		//System.out.println(la.brutToString());
		/*String[] tmp=Input_Parser.split(res);
		String[] ethernet=Input_Parser.ethernetData(tmp);
		System.out.println("\n\nTrame Analysé:");
		System.out.println("\n\nEthernet:");
		System.out.println(Input_Parser.ethernetToString(ethernet));
		String[] ipH=Input_Parser.ipHData(tmp);
		System.out.println("\nIP Header:");
		System.out.println(Input_Parser.ipHToString(ipH));
		
		System.out.println("\n\nTRAME 2:\n");
		res=Input_Parser.parse("data/exemple2.txt");
		System.out.println("Trame brut "+res);
		tmp=Input_Parser.split(res);
		ethernet=Input_Parser.ethernetData(tmp);
		System.out.println("\n\nTrame Analysé:");
		System.out.println("\n\nEthernet:");
		System.out.println(Input_Parser.ethernetToString(ethernet));
		ipH=Input_Parser.ipHData(tmp);
		System.out.println("\nIP Header:");
		System.out.println(Input_Parser.ipHToString(ipH));*/
	}
	
}
