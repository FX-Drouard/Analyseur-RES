package input_Test;

import input.Input_Parser;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import input.Lanceur;

public class Input_Parser_Test {
	
	@Test
	public void testEthernet() throws IOException {
		Lanceur la = new Lanceur("data/exemple7.txt");
		assertTrue(la.ethernetToString().equals("Destination: ff:ff:ff:ff:ff:ff\r\nSource: 00:08:74:4f:36:23\r\nEthernet Type: IPV4 (0x0800)"));
	}
	
	/*public static void main(String args[]) throws IOException{
		String path = "data/exemple4.txt";
		Lanceur la=new Lanceur(path);
		String [][] temp=Input_Parser.parse(path);
		System.out.println("\n");
		System.out.println(temp.length);
		System.out.println("\n");
		System.out.println(la.brutToString());
		//System.out.println(la.brutToString());
		String[] tmp=Input_Parser.split(res);
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
		System.out.println(Input_Parser.ipHToString(ipH));
	}*/
	
}
