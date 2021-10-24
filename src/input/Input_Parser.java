package input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



public class Input_Parser {
	private Input_Parser(String fileName) {}
	public static String parse(String fileName) throws IOException {//Lis le fichier
		BufferedReader br = null;
		String res="";
		try{
			br = new BufferedReader(new FileReader(fileName));
			//String line;
			//while((line=br.readLine())!=null) {
				//String[] buffer=line.split(" ");
			//}
			res=br.readLine();//on suppose que la trame se lit en 1 ligne
			if (res==null) {throw new RuntimeException("Erreur fichier vide!");}
			
		}catch (IOException io) {
			System.out.println("Erreur sur la lecture du fichier\n");
			io.printStackTrace();
		
		}finally {
			if(br!=null) {
				br.close();
			}
		}
		return res.toLowerCase();
	}
	public static String[] split(String in) {
		return in.split(" ");
	}
	public static String[] ethernetData(String[] in) {
		String[] res= new String[14];
		for (int i=0;i<14;i++) {
			res[i]=in[i];
		}
		return res;
	}
	
	public static String ethernetToString(String[] in) throws RuntimeException{
		if (in.length!=14) {throw new RuntimeException("Appel erroné ethernetToString");}
		StringBuilder res= new StringBuilder();
		res.append("Destination: ");
		for (int i=0;i<6;i++) {
			res.append(in[i]);
			res.append(':');
		}
		res.delete(res.length()-1, res.length());
		res.append("\nSource: ");
		for (int i=6;i<12;i++) {
			res.append(in[i]);
			res.append(':');
		}
		res.delete(res.length()-1, res.length());
		res.append("\nEthernet Type: ");
		String temp="";
		for (int i=12;i<14;i++) {
			temp+=in[i];
		}
		if (temp.equals("0800")) {
			res.append("IPV4 (0x"+temp+")\n");
		}else if (temp.equals("86DD")) {res.append("IPV6 (0x"+temp+")\n");}
		else {res.append("Autre (0x"+temp+")\n");}
		return res.toString();
	}
}
