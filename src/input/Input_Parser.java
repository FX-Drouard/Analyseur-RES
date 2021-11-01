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
	
	public static String[] ipHData(String[] in) {
		String[] res= new String[20];
		int tmp=0;
		for (int i=14;i<34;i++) {
			res[tmp]=in[i];
			tmp++;
		}
		return res;
	}
	
	public static String ipHToString(String[] in) throws RuntimeException{
		if (in.length!=20) {throw new RuntimeException("Appel erroné ipHToString");}
		StringBuilder res= new StringBuilder();
		String tmp=in[0];
		//Debut analyse Version
		if (String.valueOf(tmp.charAt(0)).equals("4")) {
			res.append("Version: 4 (0x"+tmp.charAt(0)+")\n");
		}else if (String.valueOf(tmp.charAt(0)).equals("6")) {
			throw new RuntimeException("IPV6 non pris en charge par le projet (0x"+tmp.charAt(0)+") \n");
		}
		else {throw new RuntimeException("Attention la trame est corrompu/invalide: "+String.valueOf(tmp.charAt(0)));}
		//Fin Analyse Version et Debut Analyse Taille
		int tailleH = Integer.parseInt(String.valueOf(tmp.charAt(1)),16);
		if (tailleH==5) {
			res.append("Taille de l'entete (aucune option specifiée): "+tailleH*4+" (0x"+String.valueOf(tmp.charAt(1))+")\n");
		}else if (tailleH>5){
			res.append("\n Taille de l'entete avec option: "+tailleH*4+" \n");
		}else {throw new RuntimeException("Taille de l'entete totalement invalide!");}
		//Fin Analyse Taille et Debut Analyse TOS
		res.append("TOS: Non Utilisé dans notre cadre: 0x"+in[1]+"\n");
		//Fin Analyse TOS et Debut Analyse Taille total
		tmp ="";
		for (int i=2;i<4;i++) {
			tmp+=in[i];
		}
		res.append("Taille Total du packet IP: "+Integer.parseInt(tmp,16)+" octet (0x"+tmp+")\n");
		//Fin Analyse Taille Total et Debut Analyse Fragmentation
		tmp ="";
		for (int i=4;i<6;i++) {
			tmp+=in[i];
		}
		res.append("Identifiant: 0x"+tmp+" ("+Integer.parseInt(tmp,16)+") \n");
		//Fin analyse Identifier Debut Analyse Offset+Flags
		tmp ="";
		for (int i=6;i<8;i++) {
			tmp+=in[i];
		}
		String bin="";
		bin=Integer.toBinaryString(Integer.parseInt(tmp,16));
		if (Integer.toBinaryString(Integer.parseInt(tmp,16)).length()<16) {
			for (int i =Integer.toBinaryString(Integer.parseInt(tmp,16)).length();i<16;i++) {
				bin="0"+bin;
			}
		}
		String flags="";
		for (int i=0;i<3;i++) {
			flags+=bin.charAt(i);
		}
		res.append("Flags\n    Champ Réservé: "+flags.charAt(0)+"\n    Champ DF: "+flags.charAt(1)+"\n    Champ MF: "+flags.charAt(2)+"\n");
		int offset;
		{String off="";
		for (int i=3;i<bin.length();i++) {
			off+=bin.charAt(i);
		}
		offset=Integer.parseInt(off,2);}
		if (offset==0) {res.append("Offset : 0, Paquet non Fragmenté\n");}else {res.append("Offset: "+offset+" Paquet Fragmenté\n");}
		//Fin analyse Offset+flags Debut analyse TTL
		tmp ="";
		for (int i=8;i<9;i++) {
			tmp+=in[i];
		}
		res.append("TTL (Time to live): "+Integer.parseInt(tmp,16)+"| max 255 \n");
		//Fin Analyse TTL Debut Analyse Protocol
		tmp ="";
		for (int i=9;i<10;i++) {
			tmp+=in[i];
		}
		if (Integer.parseInt(tmp,16)==1) {
			res.append("Protocol : 1 (ICMP) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==6) {
			res.append("Protocol : 6 (TCP) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==17) {
			res.append("Protocol : 17 (UDP) (0x"+tmp+")\n");
		}else {throw new RuntimeException("Protocol: "+Integer.parseInt(tmp,16)+" pas au Programme");}
		//Fin analyse protocol Debut Analyse Checksum
		tmp ="";
		for (int i=10;i<12;i++) {
			tmp+=in[i];
		}
		res.append("Checksum (Non verifiable): 0x"+tmp+"\n");
		//Fin analyse protocole Debut analyse Dest/Source
		tmp ="";
		StringBuilder ip=new StringBuilder();
		//source
		for (int i=12;i<16;i++) {
			tmp=in[i];
			ip.append(Integer.parseInt(tmp,16)+".");
		}
		ip.delete(ip.length()-1, ip.length());
		res.append("Adresse IP Source: "+ip.toString()+"\n");
		tmp ="";
		ip=new StringBuilder();
		//Destination
		for (int i=16;i<20;i++) {
			tmp=in[i];
			ip.append(Integer.parseInt(tmp,16)+".");
		}
		ip.delete(ip.length()-1, ip.length());
		res.append("Adresse IP Destination: "+ip.toString()+"\n");
		//Fin analyse IP Header!
		return res.toString();
	}
	
}
