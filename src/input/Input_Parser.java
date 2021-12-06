package input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Input_Parser {
	private Input_Parser(String fileName) {}
	public static String[][] parse(String fileName) throws IOException,FileNotFoundException, RuntimeException {//Lis le fichier
		BufferedReader br = null;
		StringBuilder res=new StringBuilder();
		StringBuilder sb= new StringBuilder();
		List<String> ls= new ArrayList<>();
		try{
			br = new BufferedReader(new FileReader(fileName));
			String line;
			int cptOc=0;
			int numL=0;
			
			while((line=br.readLine())!=null) {
				numL+=1;
				
				String[] tmps=line.split(" ");
				
				//Debut de trame
				if(tmps[0].equals("0000")) {
					cptOc=0;
					
					//Split manuel (dans le cas ou il n'y a pas de donnees a la fin
					if(numL>1) {
						ls.add(sb.toString());
						sb=new StringBuilder();
					}
					
				//Nouvelle ligne, on vÃ©rifie que cptOc est bien Ã©gal au offset sinon la ligne d'avant est fausse
				}else if(tmps[0].length()==4) {
					if(isHex(tmps[0])) {
						if(Integer.parseInt(tmps[0],16)==cptOc) {
						}else {
							throw new RuntimeException("Offset Invalide ou Ligne incompléte Ã  la ligne : "+(numL-1));
						}
					}
				}else {
					continue;
				}
				//Sinon on ignore (offset invalide)
				
				//On compte les octets de la trame
				for (int i =1;i<tmps.length;i++) {
					if(tmps[i].length()==2 && isHex(tmps[i])) {
						cptOc+=1;
					}
					
					//Partie du split manuel
					sb.append(tmps[i].toLowerCase()+" ");					
				}
				
				//Partie split manuel aussi
				sb.deleteCharAt(sb.length()-1);
				//Il faut tester la validitÃ© de la ligne (non fait)
				
				//String[] buffer=line.split(" ");
				res.append(line);
			}
			//res=br.readLine();//on suppose que la trame se lit en 1 ligne
			//if (res==null) {throw new RuntimeException("Erreur fichier vide!");}
			
		}catch (FileNotFoundException e){
			throw e;
		}catch (IOException io) {
			System.out.println("Erreur sur la lecture du fichier\n");
			io.printStackTrace();
		
		}finally {
			if(br!=null) {
				br.close();
			}
		}
		
		//On n'oublie pas d'ajouter la derniere trame s'il y en a une pour le split manuel
		if(sb.length()>0 ) {
			ls.add(sb.toString());
		}
		String[] use= new String[ls.size()];
		for(int i=0;i<use.length;i++) {
			use[i]=ls.get(i);
		}
		String[][] allres=filterCall(use);
		return allres;
	}
	
	/*
	//Cette mÃ©thode ne marche pas si une trame est suivi de 2 00 en fin de ligne et commence par 00 sur la ligne suivante
	public static String[] splitOffSet(String in) {
		String[] tmp;
		String[] res;
		tmp=in.split("0000");//attention pb sur exemple4
		if (tmp.length==1) {
			return tmp;
		}
		res=new String[tmp.length-1];
		for (int i =1;i<tmp.length;i++) {
			res[i-1]=tmp[i];
		}
		return res;
	}*/
	
	public static String[] backToSpace(String[] in) {
		String[] res=in;
		String tmp;
		//On parcourt chaque case du tableau
		for(int i=0;i<in.length;i++) {
			tmp=in[i];
			StringBuilder news= new StringBuilder();
			//On parcourt chaque chaine et change les retours a ligne en espace
			for(int j=0;j<tmp.length();j++) {
				if(tmp.charAt(j)=='\n') {
					news.append(' ');
				}else {
					news.append(tmp.charAt(j));
				}
			}
			res[i]=news.toString();
		}
		return res;
	}
	
	public static String[] split(String in) {
		return in.split(" ");
	}
	
	public static String[] filterPacket(String[] in) {
		String[] res=in;
		String tmp;
		for(int i=0;i<in.length;i++) {
			tmp=in[i];
			String[] tsplit= tmp.split(" ");
			StringBuilder news= new StringBuilder();
			for(int j=0; j<tsplit.length-1;j++) {
				if(tsplit[j]!="" && tsplit[j].length()==2 && isHex(tsplit[j])) {
					news.append(tsplit[j]+" ");
				}
			}
			if(tsplit[tsplit.length-1]!="" && tsplit[tsplit.length-1].length()==2) {
				news.append(tsplit[tsplit.length-1]);
			}
			res[i]=news.toString();
		}
		return res;
	}
	
	public static String[][] filterAllSpace(String[] in) {
		String[][] res= new String[in.length][];
		String tmp;
		for(int i=0;i<in.length;i++) {
			tmp=in[i];
			res[i]=tmp.split(" ");
		}
		return res;
		
	}
	
	public static boolean isHex(String s) {
		try {
			Long.parseLong(s,16);
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
	public static String[][] filterCall(String[] in){
		//String[]tmp=splitOffSet(in);
		String[]tmp=in;
		String[]restmp;
		String[][]res;
		restmp=backToSpace(tmp);
		tmp=filterPacket(restmp);
		res=filterAllSpace(tmp);
		return res;
		
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
	
	public static int getIpOptTaille(String[] in) {
		if (in.length!=20) {throw new RuntimeException("Appel erroné getIpOptTaille");}
		String tmp=in[0];
		int tailleH = Integer.parseInt(String.valueOf(tmp.charAt(1)),16);
		return tailleH*4-20;
	}
	
	public static String[] ipOptData(String[] in, int taille) {
		String[] res= new String[taille];
		int tmp=0;
		for (int i=34;i<34+taille;i++) {
			res[tmp]=in[i];
			tmp++;
		}
		return res;
	}
	
	public static String ipOptToString(String[] in,int taille) {
		if (in.length!=taille) {throw new RuntimeException("Appel erroné ipOptToString");}
		String[] tab= {"End of Options List","No Operation","Security","Loose Source Route","Time Stamp","Extended Security","Commercial Security","Record Route","Stream ID","Strict Source Route","Experimental Measurement","MTU Probe","MTU Reply","Experimental Flow Control","Experimental Access Control","ENCODE","IMI Traffic Descriptor","Extended Internet Protocol","Traceroute","Address Extension","Router Alert","Selective Directed Broadcast","Unassigned (Released 18 October 2005)","Dynamic Packet State","Upstream Multicast Pkt.","Quick-Start","Unassigned","Unassigned","Unassigned","Unassigned","RFC3692-style Experiment"};
		int i=0;
		StringBuilder res=new StringBuilder().append("\nOption IP List:\n");
		while (true) {
			if (i==in.length) {
				break;
			}
			String hex=in[i];
			String bin=Input_Parser.hexToBin(hex);
			int type=Integer.parseInt(bin.substring(3),2);
			int taillopt=Integer.parseInt(in[i+1],16);
			i+=taillopt;
			if (type==0) {
				res.append("    IP Option: "+tab[type]+" ("+taillopt+" bytes)\n");
				return res.toString();
			}
			res.append("    IP Option: "+tab[type]+" ("+taillopt+" bytes)\n");
		}
		return res.toString();
		
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
		}else if (Integer.parseInt(tmp,16)==2) {
			res.append("Protocol : 2 (IGMP) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==3) {
			res.append("Protocol : 3 (GGP) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==4) {
			res.append("Protocol : 4 (IP-in-IP) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==5) {
			res.append("Protocol : 5 (ST) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==7) {
			res.append("Protocol : 7 (CBT) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==8) {
			res.append("Protocol : 8 (EGP) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==9) {
			res.append("Protocol : 9 (IGP) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==10) {
			res.append("Protocol : 10 (BBN-RCC-MON) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==11) {
			res.append("Protocol : 11 (NVP-II) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==12) {
			res.append("Protocol : 12 (PUP) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==13) {
			res.append("Protocol : 13 (ARGUS) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==14) {
			res.append("Protocol : 14 (EMCON) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==15) {
			res.append("Protocol : 15 (XNET) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==16) {
			res.append("Protocol : 16 (CHAOS) (0x"+tmp+")\n");
		}else if (Integer.parseInt(tmp,16)==0) {
			res.append("Protocol : 0 (HOPOPT) (0x"+tmp+")\n");
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
		if (ip.toString().equals("255.255.255.255")) {
			res.append("Adresse IP Destination: "+ip.toString()+" (Broadcast)\n");
		}else {
			res.append("Adresse IP Destination: "+ip.toString()+"\n");}
		//Fin analyse IP Header!
		return res.toString();
	}
	
	
	public static String protocolenameToString(String[] in ) {
		if (in.length!=20) {throw new RuntimeException("Appel erroné ipHToString");}
		String tmp ="";
		for (int i=9;i<10;i++) {
			tmp+=in[i];
		}
		if (Integer.parseInt(tmp,16)==1) {
			return "ICMP";
		}else if (Integer.parseInt(tmp,16)==6) {
			return "TCP";
		}else if (Integer.parseInt(tmp,16)==17) {
			return "UDP";
		}else if (Integer.parseInt(tmp,16)==2) {
			return "IGMP";
		}else if (Integer.parseInt(tmp,16)==3) {
			return "GGP";
		}else if (Integer.parseInt(tmp,16)==4) {
			return "IP-in-IP";
		}else if (Integer.parseInt(tmp,16)==5) {
			return "ST";
		}else if (Integer.parseInt(tmp,16)==7) {
			return "CBT";
		}else if (Integer.parseInt(tmp,16)==8) {
			return "EGP";
		}else if (Integer.parseInt(tmp,16)==9) {
			return "IGP";
		}else if (Integer.parseInt(tmp,16)==10) {
			return "BBN-RCC-MON";
		}else if (Integer.parseInt(tmp,16)==11) {
			return "NVP-II";
		}else if (Integer.parseInt(tmp,16)==12) {
			return "PUP";
		}else if (Integer.parseInt(tmp,16)==13) {
			return "ARGUS";
		}else if (Integer.parseInt(tmp,16)==14) {
			return "EMCON";
		}else if (Integer.parseInt(tmp,16)==15) {
			return "XNET";
		}else if (Integer.parseInt(tmp,16)==16) {
			return "CHAOS";
		}else if (Integer.parseInt(tmp,16)==0) {
			return "HOPOPT";
		}else {return "Protocole Inconue";}
	}
	
	public static String[] protocoleHData(String [] in, int start ) {
		String[] res= new String[8];
		int tmp=0;
		for (int i=start;i<start+8;i++) {
			res[tmp]=in[i];
			tmp++;
		}
		return res;
	}
	
	public static int protocoleHStartByIP(String [] in) {
		if (in.length!=20) {throw new RuntimeException("Appel erroné protocoleStartByIP");}
		String tmp = in[0];
		int tailleH = Integer.parseInt(String.valueOf(tmp.charAt(1)),16);
		return 4*tailleH+14;
	}
	
	public static String protocoleHToString(String[] in) {
		if (in.length!=8) {throw new RuntimeException("Appel erroné protocoleToString");}
		StringBuilder res= new StringBuilder();
		String tmp =in[0];
		tmp=tmp+in[1];
		{int port=Integer.parseInt(tmp,16);
		res.append("Port Source: "+port+" (0x"+tmp+")\n");
		tmp = in[2];
		tmp =tmp+in[3];
		port=Integer.parseInt(tmp,16);
		res.append("Port Destination: "+port+" (0x"+tmp+")\n");}
		tmp = in[4];
		tmp =tmp+in[5];
		res.append("Longueur: "+Integer.parseInt(tmp,16)+" (0x"+tmp+")\n");
		tmp = in[6];
		tmp =tmp+in[7];
		res.append("Checksum (non verifiable): "+tmp+" (0x"+tmp+")\n");
		return res.toString();
	}
	
	public static int udpLong(String [] in) {
		if (in.length!=8) {throw new RuntimeException("Appel erroné udpLong");}
		String tmp = in[4]+in[5];
		return Integer.parseInt(tmp,16);
	}
	
	public static String dNameToString(String[] in){
		if (in.length!=8) {throw new RuntimeException("Appel erroné protocoleToString");}
		String tmp =in[0];
		tmp=tmp+in[1];
		if ((Integer.parseInt(tmp,16)==67)||(Integer.parseInt(tmp,16)==68)) {
			return "DHCP";
		}
		else if (Integer.parseInt(tmp,16)==53) {
			return "DNS";
		}else {
			tmp =in[2];
			tmp=tmp+in[3];
			if ((Integer.parseInt(tmp,16)==67)||(Integer.parseInt(tmp,16)==68)) {
				return "DHCP";
			}
			else if (Integer.parseInt(tmp,16)==53) {
				return "DNS";
		}else { return "DNS/DHCP non présent";}}
	}
	
	public static int dStartByIP(String [] in) {
		if (in.length!=20) {throw new RuntimeException("Appel erroné dStartByIP");}
		String tmp = in[0];
		int tailleH = Integer.parseInt(String.valueOf(tmp.charAt(1)),16);
		return 4*tailleH+14+8;
	}
	
	public static String [] dhcpData(String[] in, int start) {
		String[] res=new String[Input_Parser.udpLong(Input_Parser.protocoleHData(in, Input_Parser.protocoleHStartByIP(Input_Parser.ipHData(in))))];
		int tmp=0;
		for (int i=start;i<in.length;i++) {
			res[tmp]=in[i];
			tmp++;
		}
		return res;
	}
	
	public static String dhcpToString(String[] in, int taille) {
		if (in.length!=taille) {throw new RuntimeException("Appel erroné dhcpToString");}
		StringBuilder res= new StringBuilder();
		//StringBuilder res2= new StringBuilder();res2.append("DHCP Message Type: Unknown\n");
		String tmp=in[0];
		if (tmp.equals("01")) {
			res.append("Message Type: Boot Request (0x01)\n");
		}else if (tmp.equals("02")) {
			res.append("Message Type: Boot Reply (0x02)\n");
		}else {
			res.append("Message Type: Unknown (0x"+tmp+")\n");
		}
		tmp=in[1];
		res.append("Hardware Type: "+Input_DHCP.getHardware(Integer.parseInt(tmp,16))+"\n");
		tmp=in[2];
		res.append("Hardware address length: "+Integer.parseInt(tmp,16)+"\n");
		tmp=in[3];
		res.append("HOPS: "+Integer.parseInt(tmp,16)+"\n");
		tmp=in[4]+in[5]+in[6]+in[7];
		res.append("Transaction ID: 0x"+tmp+"\n");
		tmp=in[8]+in[9];
		res.append("Second elapsed: "+Integer.parseInt(tmp,16)+"\n");
		tmp=in[10]+in[11];
		if (tmp.equals("0000")) {
			res.append("BootP flags: 0x0000 (Unicast)\n    0... .... .... .... : Unicast\n    .000 0000 0000 0000: Reserved Flags: 0x0000\n");
		}else if (tmp.equals("8000")) {
			res.append("BootP flags: 0x8000 (Broadcast)\n    1... .... .... .... : Broadcast\n    .000 0000 0000 0000: Reserved Flags: 0x8000\n");
		}else {
			res.append("Bootp Flags: Incorrect\n");
		}
		res.append("Client IP address: "+Integer.parseInt(in[12],16)+"."+Integer.parseInt(in[13],16)+"."+Integer.parseInt(in[14],16)+"."+Integer.parseInt(in[15],16)+"\n");
		res.append("Your (Client) IP address: "+Integer.parseInt(in[16],16)+"."+Integer.parseInt(in[17],16)+"."+Integer.parseInt(in[18],16)+"."+Integer.parseInt(in[19],16)+"\n");
		res.append("Next server IP address: "+Integer.parseInt(in[20],16)+"."+Integer.parseInt(in[21],16)+"."+Integer.parseInt(in[22],16)+"."+Integer.parseInt(in[23],16)+"\n");
		res.append("Relay agent IP address (Gateway): "+Integer.parseInt(in[24],16)+"."+Integer.parseInt(in[25],16)+"."+Integer.parseInt(in[26],16)+"."+Integer.parseInt(in[27],16)+"\n");
		res.append("Client Mac Adress: "+in[28]+":"+in[29]+":"+in[30]+":"+in[31]+":"+in[32]+":"+in[33]+"\n");
		String ttmp="";
		res.append("Padding: 00000000000000000000\n");
		
		boolean isnZ=true;
		for (int i=44;i<108;i++) {
			ttmp=ttmp+Input_DHCP.hexToAscii(in[i]);
			if(!(in[i].equals("00"))) {
				isnZ=false;
			}
		}
		if (isnZ) {
			res.append("Server host name: not given"+"\n");
		}else {
			res.append("Server Host Name: "+ttmp+"\n");
		}
		
		isnZ=true;
		ttmp="";
		for (int i=109;i<236;i++) {
			if(!(in[i].equals("00"))) {
				isnZ=false;
			}
			ttmp=ttmp+Input_DHCP.hexToAscii(in[i]);
		}
		if (isnZ) {
			res.append("Boot file name: not given"+"\n");
		}else {
			res.append("Boot File Name: "+ttmp+"\n");
		}
		tmp=in[236]+in[237]+in[238]+in[239];
		if (!tmp.equals("63825363")) {
			res.append("Magic cookie invalide (0x"+tmp+")\n");
			return res.toString();
		}
		res.append("Magic cookie: DHCP"+"\n");
		res.append("\nDebut des Options:\n\n");
		//return res.toString();
		int lastind=240;
		try {
			boolean fin=true;
			int i=240;
			while(fin) {
				tmp=in[i];
				int index=Integer.parseInt(tmp,16);
				if(index <=Input_DHCP.getTab().length && index >=0) {
					res.append("DHCP Option: "+Input_DHCP.getOption(Integer.parseInt(tmp,16))+" ("+index+")");
				}
				else {
					res.append("DHCP Option: Unknown "+index+"\n");
				}
				
				int len=0;
				try{
					len=Integer.parseInt(in[i+1],16);
				}catch (Exception e) {
					len=0;
				}
				//option end 255
				if (tmp.equals("ff")||tmp.equals("FF")) {
					lastind=i+len+1;
					fin=false;
				
				}
				
				if (Integer.parseInt(tmp,16)==53) {
					if (in[i+1+len].equals("01")) {
						res.append(" : DISCOVER\n");
						res.append("    Longueur: "+len+"\n");
					}else if (in[i+1+len].equals("02")) {
						res.append(" : OFFER\n");
						res.append("    Longueur: "+len+"\n");
					}else if (in[i+1+len].equals("03")) {
						res.append(" : REQUEST\n");
						res.append("    Longueur: "+len+"\n");
					}else if (in[i+1+len].equals("04")) {

						res.append(" : DECLINE\n");
						res.append("    Longueur: "+len+"\n");
					}else if (in[i+1+len].equals("05")) {
						res.append(" : ACK\n");
						res.append("    Longueur: "+len+"\n");
					}else if (in[i+1+len].equals("06")) {

						res.append(" : NAK\n");
						res.append("    Longueur: "+len+"\n");
					}else if (in[i+1+len].equals("07")) {
						res.append(" : RELEASE\n");
						res.append("    Longueur: "+len+"\n");
					}else if (in[i+1+len].equals("08")) {

						res.append(" : INFORM\n");
						res.append("    Longueur: "+len+"\n");
					}else {
						res.append(" : UNKNOWN\n");
						res.append("    Longueur: "+len+"\n");
					}
				}else if (Integer.parseInt(tmp,16)==51){
					res.append("\n    Longueur: "+len+"\n");
					String val=in[i+2]+in[i+3]+in[i+4]+in[i+5];
					int sec= Integer.parseInt(val, 16);
					res.append("    IP Address Lease Time: ("+sec+"s) "+Input_DHCP.secToDays(sec)+" day(s)\n");
				}else if (Integer.parseInt(tmp,16)==55){
					res.append("\n    Longueur: "+len+"\n");
					for (int j=i+1;j<i+1+len;j++) {
						res.append("    Parameter Request List Item: ("+Integer.parseInt(in[j],16)+") "+Input_DHCP.getOption(Integer.parseInt(in[j],16))+"\n");
					}
				}else if (Integer.parseInt(tmp,16)==1){
					res.append("\n    Longueur: "+len+"\n");
					res.append("    Subnet Mask: "+Integer.parseInt(in[i+2],16)+"."+Integer.parseInt(in[i+3],16)+"."+Integer.parseInt(in[i+4],16)+"."+Integer.parseInt(in[i+5],16)+"\n");
				}else if (Integer.parseInt(tmp,16)==54){
					res.append("\n    Longueur: "+len+"\n");
					res.append("    DHCP Server Identifier: "+Integer.parseInt(in[i+2],16)+"."+Integer.parseInt(in[i+3],16)+"."+Integer.parseInt(in[i+4],16)+"."+Integer.parseInt(in[i+5],16)+"\n");
				}else if (Integer.parseInt(tmp,16)==15){
					res.append("\n    Longueur: "+len+"\n");
					StringBuilder txt=new StringBuilder();
					for (int j=i+2;j<i+len+1;j++) {
						txt.append(in[j]);
					}
					res.append("    Domain Name: "+Input_DHCP.hexToAscii(txt.toString())+"\n");
				}else if (Integer.parseInt(tmp,16)==6){
					res.append("\n    Longueur: "+len+"\n");
					int nb=len/4;
					res.append("    Nombre de serveur de Noms: "+nb+"\n");
					for (int j=0;j<nb;j++) {
						res.append("    Serveur de nom: "+Integer.parseInt(in[i+2+(4*j)],16)+"."+Integer.parseInt(in[i+3+(4*j)],16)+"."+Integer.parseInt(in[i+4+(4*j)],16)+"."+Integer.parseInt(in[i+4+(4*j)],16)+"\n");
					}
				}else if (Integer.parseInt(tmp,16)==3){
					res.append("\n    Longueur: "+len+"\n");
					res.append("    Router: "+Integer.parseInt(in[i+2],16)+"."+Integer.parseInt(in[i+3],16)+"."+Integer.parseInt(in[i+4],16)+"."+Integer.parseInt(in[i+5],16)+"\n");
				}else if (Integer.parseInt(tmp,16)==50){
					res.append("\n    Longueur: "+len+"\n");
					res.append("    Requested IP Adress: "+Integer.parseInt(in[i+2],16)+"."+Integer.parseInt(in[i+3],16)+"."+Integer.parseInt(in[i+4],16)+"."+Integer.parseInt(in[i+5],16)+"\n");
				}else if (Integer.parseInt(tmp,16)==12){ 
					res.append("\n    Longueur: "+len+"\n");
					StringBuilder txt =new StringBuilder();
					for (int j=i+2;j<i+len+2;j++) {
						txt.append(Input_DHCP.hexToAscii(in[j]));
					}
					res.append("    Host Name: "+txt.toString()+"\n");
				}else if (Integer.parseInt(tmp,16)==116){
					res.append("\n    Longueur: "+len+"\n");
					if (in[i+2].equals("00")) {
						res.append("    DHCP Auto-Configuration: DoNotAutoConfigure (0)\n");
					}else if (in[i+2].equals("01")){
						res.append("    DHCP Auto-Configuration: AutoConfigure (1)\n");
					}else {
						res.append("    DHCP Auto-Configuration: Unknown ("+Integer.parseInt(in[i+2],16)+")\n");
					}
				}else if (Integer.parseInt(tmp,16)==61){
					res.append("\n    Longueur: "+len+"\n");
					res.append("    Hardware Type: "+Input_DHCP.getHardware(Integer.parseInt(in[i+2], 16))+" (0x"+in[i+2]+")\n");
					StringBuilder txt=new StringBuilder();
					for (int j=i+3;j<i+2+len;j++) {
						txt.append(in[j]);
						if (j!=i+1+len) {
							txt.append(":");
						}
					}
					res.append("    Client Hardware Address: "+txt.toString()+"\n");
				}else if (Integer.parseInt(tmp,16)==60){ 
					res.append("\n    Longueur: "+len+"\n");
					StringBuilder txt =new StringBuilder();
					for (int j=i+2;j<i+len+2;j++) {
						txt.append(Input_DHCP.hexToAscii(in[j]));
					}
					res.append("    Vendor Class Identifier: "+txt.toString()+"\n");
				}else if (Integer.parseInt(tmp,16)==42){
					res.append("\n    Longueur: "+len+"\n");
					res.append("    NTP Server: "+Integer.parseInt(in[i+2],16)+"."+Integer.parseInt(in[i+3],16)+"."+Integer.parseInt(in[i+4],16)+"."+Integer.parseInt(in[i+5],16)+"\n");
				}else {
					res.append("\n    Longueur: "+len+"\n");
				}
				
				
				//Attention il faut incrémenter de 2 pour commencer au bon endroit
				i+=len+2;
			}
		}catch (Exception e){
			return res.toString()+"Paquet DHCP malformé!\n";
		}
		//Padding
		if(lastind>240) {
			StringBuilder pad=new StringBuilder();
			for (int i =lastind;i<taille;i++) {
				if(in[i]==null) {
					break;
				}
				pad.append(in[i]);
			}
			if (pad.length()!=0) {
				res.append(""+"\nPadding : "+pad.toString());
			}else {
				res.append(""+"\nPadding : No Padding");
			}
			
		}
		
		return res.toString();
		
	}
	
	public static String dnsToString(String[] in, int taille) {
		if (in.length!=taille) {throw new RuntimeException("Appel erroné dhcpToString");}
		StringBuilder res=new StringBuilder();
		res.append("Transaction id: 0x"+in[0]+in[1]+"\n");
		//flags
		String tmp=in[2]+in[3];
		res.append("Flags: 0x"+tmp+" ");
		{StringBuilder res2= new StringBuilder();
		boolean resp=false;
		String bin=Input_Parser.hexToBin(tmp);
		if((""+bin.charAt(0)).equals("0")) {
			res2.append("    "+bin.charAt(0)+"... .... .... .... = Response: message is a query\n");
		}else {
			res2.append("    "+bin.charAt(0)+"... .... .... .... = Response: message a response\n");
			resp=true;
		}
		{String op =bin.substring(1, 5); //debut op
		if (op.equals("0000")) {
			res2.append("    ."+op.substring(0,3)+" "+op.charAt(3)+"... .... .... =OPCode: Standard Query ("+Integer.parseInt(op, 2)+")\n");
			res.append("Standard Query ("+Integer.parseInt(op, 2)+") "+"Is Response: "+resp);
		}else if (op.equals("0001")) {
			res2.append("    ."+op.substring(0,3)+" "+op.charAt(3)+"... .... .... =OPCode:  IQuery ("+Integer.parseInt(op, 2)+")\n");
			res.append("IQuery ("+Integer.parseInt(op, 2)+") "+"Is Response: "+resp);
		}else if (op.equals("0010")) {
			res2.append("    ."+op.substring(0,3)+" "+op.charAt(3)+"... .... .... =OPCode: Status ("+Integer.parseInt(op, 2)+")\n");
			res.append("Status ("+Integer.parseInt(op, 2)+") "+"Is Response: "+resp);
		}else if (op.equals("0011")) {
			res2.append("    ."+op.substring(0,3)+" "+op.charAt(3)+"... .... .... =OPCode: Unassigned ("+Integer.parseInt(op, 2)+")\n");
			res.append("Unassigned ("+Integer.parseInt(op, 2)+") "+"Is Response: "+resp);
		}else if (op.equals("0100")) {
			res2.append("    ."+op.substring(0,3)+" "+op.charAt(3)+"... .... .... =OPCode: Notify ("+Integer.parseInt(op, 2)+")\n");
			res.append("Notify ("+Integer.parseInt(op, 2)+") "+"Is Response: "+resp);
		}else if (op.equals("0101")) {
			res2.append("    ."+op.substring(0,3)+" "+op.charAt(3)+"... .... .... =OPCode: Update ("+Integer.parseInt(op, 2)+")\n");
			res.append("Update ("+Integer.parseInt(op, 2)+") "+"Is Response: "+resp);
		}else if (op.equals("0110")) {
			res2.append("    ."+op.substring(0,3)+" "+op.charAt(3)+"... .... .... =OPCode: DNS Stateful Operations ("+Integer.parseInt(op, 2)+")\n");
			res.append("DNS Stateful Operations ("+Integer.parseInt(op, 2)+") "+"Is Response: "+resp);
		}else {
			res2.append("    ."+op.substring(0,3)+" "+op.charAt(3)+"... .... .... =OPCode: Unknown ("+Integer.parseInt(op, 2)+")\n");
			res.append("Unknown ("+Integer.parseInt(op, 2)+") "+"Is Response: "+resp);
		}
		}//fin op
		
		if (resp) {
			String a = ""+bin.charAt(5);
			if (a.equals("0")) {
				res2.append("    .... ."+a+".. .... .... = Authoritative: Server is not an authority for domain\n");
			}else {
				res2.append("    .... ."+a+".. .... .... = Authoritative: Server is an authority for domain\n");
			}
		}
		if ((""+bin.charAt(6)).equals("0")) {
			res2.append("    .... .."+bin.charAt(6)+". .... .... = Truncated: Message is not truncated\n");
		}else {
			res2.append("    .... .."+bin.charAt(6)+". .... .... = Truncated: Message is truncated\n");
		}
		if ((""+bin.charAt(7)).equals("1")) {
			res2.append("    .... ..."+bin.charAt(7)+" .... .... = Recursion Desired: Do query recursively\n");
		}else {
			res2.append("    .... ..."+bin.charAt(7)+" .... .... = Recursion Desired: Don't do query recursively\n");
		}
		if (resp) {
			if ((""+bin.charAt(8)).equals("0")) {
				res2.append("    .... .... "+bin.charAt(8)+"... .... = Recursion Available: Server Can't do recursive queries\n");
			}else {
				res2.append("    .... .... "+bin.charAt(8)+"... .... = Recursion Available: Server can do recursive queries\n");
			}
		}
		if ((""+bin.charAt(9)).equals("1")) {
			res2.append("    .... .... ."+bin.charAt(9)+".. .... = Z: reserved -Incorrect!\n");
		}else {
			res2.append("    .... .... ."+bin.charAt(9)+".. .... = Z: reserved (0)\n");
		}
		if (resp) {
			if ((""+bin.charAt(10)).equals("0")) {
				res2.append("    .... .... .."+bin.charAt(10)+". .... = Answer authenticated: Answer/Authority portion was not authenticated by the server\n");
			}else {
				res2.append("    .... .... .."+bin.charAt(10)+". .... = Answer authenticated: Answer/Authority portion was authenticated by the server\n");
			}
		}
		if ((""+bin.charAt(11)).equals("0")) {
			res2.append("    .... .... ..."+bin.charAt(11)+" .... = Non-authenticated data: Unacceptable!\n");
		}else {
			res2.append("    .... .... ..."+bin.charAt(11)+" .... = Non-authenticated data: Acceptable\n");
		}
		if (resp) {
			String a=bin.substring(12);
			if (a.equals("0000")) {
				res2.append("    .... .... .... "+a+" = Reply code: No error ("+Integer.parseInt(a, 2)+")\n");
				res.append(", no error\n");
			}else if (a.equals("0001")) {
				res2.append("    .... .... .... "+a+" = Reply code: Error ("+Integer.parseInt(a, 2)+")\n");
				res.append(" Error\n");
			}else if (a.equals("0010")) {
				res2.append("    .... .... .... "+a+" = Reply code: Server failed ("+Integer.parseInt(a, 2)+")\n");
				res.append(", Server failed\n");
			}else if (a.equals("0011")) {
				res2.append("    .... .... .... "+a+" = Reply code: No such name ("+Integer.parseInt(a, 2)+")\n");
				res.append(", No such name\n");
			}else if (a.equals("0100")) {
				res2.append("    .... .... .... "+a+" = Reply code: Not Implemented ("+Integer.parseInt(a, 2)+")\n");
				res.append(", Not Implemented\n");
			}else if (a.equals("0101")) {
				res2.append("    .... .... .... "+a+" = Reply code: Refused ("+Integer.parseInt(a, 2)+")\n");
				res.append(", Refused\n");
			}else if (a.equals("0110")) {
				res2.append("    .... .... .... "+a+" = Reply code: Name Exists ("+Integer.parseInt(a, 2)+")\n");
				res.append(", Name Exists\n");
			}else if (a.equals("0111")) {
				res2.append("    .... .... .... "+a+" = Reply code: RRset Exists ("+Integer.parseInt(a, 2)+")\n");
				res.append(", RRset Exists\n");
			}else if (a.equals("1000")) {
				res2.append("    .... .... .... "+a+" = Reply code: RRset does not Exist ("+Integer.parseInt(a, 2)+")\n");
				res.append(", RRset does not Exist\n");
			}else if (a.equals("1001")) {
				res2.append("    .... .... .... "+a+" = Reply code: Not Authoritative ("+Integer.parseInt(a, 2)+")\n");
				res.append(", Not Authoritative\n");
			}else if (a.equals("1010")) {
				res2.append("    .... .... .... "+a+" = Reply code: Name out of zone ("+Integer.parseInt(a, 2)+")\n");
				res.append(", Name out of zone\n");
			}else if (a.equals("1011")) {
				res2.append("    .... .... .... "+a+" = Reply code: DSO-Type not implemented ("+Integer.parseInt(a, 2)+")\n");
				res.append(", DSO-Type not implemented\n");
			}else {
				res2.append("    .... .... .... "+a+" = Reply code: Unknown ("+Integer.parseInt(a, 2)+")\n");
				res.append(", Unknown error\n");
			}
		}else {
			res.append("\n");
		}
		res.append(res2.toString());
		}

		//fin flags
		
		tmp=in[4]+in[5];
		int question=Integer.parseInt(tmp,16);
		res.append("Question: "+question+"\n");
		tmp=in[6]+in[7];
		int rep=Integer.parseInt(tmp,16);
		res.append("Reponse: "+rep+"\n");
		tmp=in[8]+in[9];
		int arr=Integer.parseInt(tmp,16);
		res.append("Authority RRs: "+arr+"\n");
		tmp=in[10]+in[11];
		
		int addrr=Integer.parseInt(tmp,16);
		res.append("Additional RRs: "+addrr+"\n");
		//au dessus les 4 valeurs
		
		
		
		//question
		int i=12;
		tmp = Input_Parser.hexToBin(in[i]);
		if (tmp.substring(0,2).equals("11")) {
			return res.toString()+"\nInvalide dns data\n";
		}
		
		StringBuilder res3=new StringBuilder();
		
		//Dictionnaire contenant les offsets et les mots
		Map<Integer,String> map=new HashMap<Integer,String>();
		
		
		res.append("Queries: \n");
		for (int j=0;j<question;j++) {
			
			int totlength=0;//taille total du name de 1 question
			int nbmots=0;//nb mots de 1 question
			
			
			while (!in[i].equals("00")) {
				boolean isC=false;
				int index=i;
				tmp = Input_Parser.hexToBin(in[i]);
				
				//Compression part
				while (tmp.substring(0,2).equals("11")) {
					int colen=0;
					System.out.println("I passc ");
					isC=true;

					
					//Get the value of offset
					int pointer=Integer.parseInt(tmp.substring(2)+Input_Parser.hexToBin(in[i+1]),2);
					//If it points the same offset the packet is wrong
					if(pointer>=i) {
						return "DNS Invalid Data\n";
					}
					//Get the name until 00
					
					while (!(in[pointer].equals("00"))) {
						
						if((in[pointer].substring(0,2).equals("11"))) {
							int pt=Integer.parseInt(in[pointer].substring(2)+hexToBin(in[pointer+1]),2);
							//Start the recusive method
							res3.append(dnsRec(in,pt,map));
							i+=2;
							tmp= hexToBin(in[i]);
							pointer+=(1+Integer.parseInt(in[pointer],16));
							continue;
							
						}
						
						if(map.containsKey(pointer)) {
							res3.append(map.get(pointer)+".");
							nbmots+=1;
						}else {
							
							return res.toString()+"DNS Invalid";
						}
						colen+=Integer.parseInt(in[pointer],16);
						pointer+=(1+Integer.parseInt(in[pointer],16));
						
						
					}
					
					totlength+=colen;
					i+=2;
					tmp= hexToBin(in[i]);
					
				}
				if(isC) {
					i--;
					break;
				}
				
				//Word length
				int nbb=Integer.parseInt(in[i],16);
				totlength+=nbb;
				i++;
				String ra="";
				int fin=i+nbb;
				
				
				//Get the word
				for (int o=i;o<fin;o++) {
					ra=ra+in[o];
					i++;
				}
				res3.append(Input_DHCP.hexToAscii(ra)+".");
				//Number of words
				nbmots++;
				
				//Stock the full name
				map.put(index, Input_DHCP.hexToAscii(ra));
			}
			if ((res3.charAt(res3.length()-1))=='.') {
				res3.deleteCharAt(res3.length()-1);
			}
			res.append("    Name: "+res3.toString()+"\n");
			res.append("        [Name length: "+totlength+"]\n");
			res.append("        [Label Count: "+nbmots+"]\n");
			nbmots=0;
			
			//We need to increment to the next octet
			i++;
			
			//debut des if else
			int type=Integer.parseInt(in[i]+in[i+1],16);

			//5 types principaux + affinité
			if (type==1) {
				res.append("        Type: A (IPV4) (1)\n");
			}else if (type==28) {
				res.append("        Type: AAAA (IPV6) (28)\n");
			}else if (type==5) {
				res.append("        Type: CNAME (Canonical name) (5)\n");
			}else if (type==2) {
				res.append("        Type: NS (Name Server) (2)\n");
			}else if (type==15) {
				res.append("        Type: MX (Mail Server Name) (15)\n");
			}else if (type==12) {
				res.append("        Type: PTR (domain name PoinTeR) (12)\n");
			}else {
				res.append("        Type non pris en charge\n");
			}
			//Fin des types
			i=i+2;
			int classe=Integer.parseInt(in[i]+in[i+1],16);
			if (classe==0) {
				res.append("        Classe: Reserved (0x0000)\n");
			}else if (classe==1) {
				res.append("        Classe: IN (0x0001)\n");
			}else if (classe==2) {
				res.append("        Classe: Unassigned (0x0002)\n");
			}else if (classe==3) {
				res.append("        Classe: Chaos (CH) (0x0003)\n");
			}else if (classe==4) {
				res.append("        Classe: Hesiod (HS) (0x0004)\n");
			}else if ((classe>=5)&&(classe<=253)) {
				res.append("        Classe: Unassigned (0x"+in[i]+in[i+1]+")\n");
			}else if (classe==254) {
				res.append("        Classe: Qclass None (0x"+in[i]+in[i+1]+")\n");
			}else if (classe==255) {
				res.append("        Classe: Qclass *(Any) (0x"+in[i]+in[i+1]+")\n");
			}else if ((classe>=256)&&(classe<=65279)) {
				res.append("        Classe: Unassigned (0x"+in[i]+in[i+1]+")\n");
			}else if ((classe>=65280)&&(classe<=65534)) {
				res.append("        Classe: Reserved for private Use (0x"+in[i]+in[i+1]+")\n");
			}else {
				res.append("        Classe: Reserved (0x"+in[i]+in[i+1]+")\n");
			}
			//Fin des classes
			i+=2;
		}
		
		
		
		res3=new StringBuilder();
		
		
		//Answer Part
		if(rep >0) res.append("Answer: \n");
		for (int j=0;j<rep;j++) {
			
			int totlength=0;//taille total du name de 1 question
			int nbmots=0;//nb mots de 1 question
			
			while (!in[i].equals("00")) {
				boolean isC=false;
				tmp = Input_Parser.hexToBin(in[i]);
				
				
				//Compression part
				while (tmp.substring(0,2).equals("11")) {
					int colen=0;
					isC=true;
					
					//Get the value of offset
					
					int pointer=Integer.parseInt(tmp.substring(2)+Input_Parser.hexToBin(in[i+1]),2);
					
					if(pointer>=i) {
						return "DNS Invalid Data\n";
					}
					
					//Get the name until 00
					while (!(in[pointer].equals("00"))) {
						
						//Tentative de vérification de pointeur vers un autre pointeur mais impossible sur n pointeurs eparpilles
						/*String cal=Input_Parser.hexToBin(in[pointer]).substring(0,2);
						int pt=Integer.parseInt(in[pointer].substring(2)+Input_Parser.hexToBin(in[pointer+1]),2);
						while(cal.equals("11")) {
							cal=Input_Parser.hexToBin(in[Integer.parseInt(in[pt])]).substring(0,2);
							if(cal.equals("11"))
								pt=Integer.parseInt(in[pt].substring(2)+Input_Parser.hexToBin(in[pt+1]),2);
						}
						
						while(!(in[pt].equals("00"))) {
							if(map.containsKey(pt)) {
								res3.append(map.get(pt)+".");
								nbmots+=1;
							}else {
								return res.toString()+"DNS Invalid data Pointer Answer of Pointer";
							}
							colen+=Integer.parseInt(in[pointer],16);
							pt+=(1+Integer.parseInt(in[pt],16));
						}*/
						if((in[pointer].substring(0,2).equals("11"))) {
							int pt=Integer.parseInt(in[pointer].substring(2)+hexToBin(in[pointer+1]),2);
							System.out.println("Value "+pt);
							//Start the recusive method
							res3.append(dnsRec(in,pt,map));
							i+=2;
							tmp= hexToBin(in[i]);
							pointer+=(1+Integer.parseInt(in[pointer],16));
							continue;
							
						}
						
						if(map.containsKey(pointer)) {
							res3.append(map.get(pointer)+".");
							nbmots+=1;
						}else {
							return res.toString()+"DNS Invalid data Pointer Answer";
						}
						colen+=Integer.parseInt(in[pointer],16);
						pointer+=(1+Integer.parseInt(in[pointer],16));
						
						
					}
					
					totlength+=colen;
					i+=2;
					tmp= hexToBin(in[i]);
					
					
				}
				
				//if we did compression there is no 00 in the end
				if(isC) {
					i--;
					break;
				}
				
				//Total length
				int aclength=Integer.parseInt(in[i],16);
				totlength+=aclength;
				i++;
				String ra="";
				int fin=i+aclength;
				int index=i;
				for (int o=i;o<fin;o++) {
					ra=ra+in[o];
					i++;
				}
				res3.append(Input_DHCP.hexToAscii(ra)+".");
				nbmots++;
				//Put the word in hashmap if new offset
				map.put(index, Input_DHCP.hexToAscii(ra));
			}
			
			//remove the dot at the end
			if (res3.toString().charAt(res3.toString().length()-1)=='.') {
				res3.deleteCharAt(res3.length()-1);
			}
			
			//We need to increment to the next octet
			i++;
			

			res.append("    Name: "+res3.toString()+"\n");
			res.append("        [Name length: "+totlength+"]\n");
			res.append("        [Label Count: "+nbmots+"]\n");
			nbmots=0;
			
						
			
			//debut des if else
			int type=Integer.parseInt(in[i]+in[i+1],16);
			//5 types principaux + affinitÃ©
			if (type==1) {
				res.append("        Type: A (IPV4) (1)\n");
			}else if (type==28) {
				res.append("        Type: AAAA (IPV6) (28)\n");
			}else if (type==5) {
				res.append("        Type: CNAME (Canonical name) (5)\n");
			}else if (type==2) {
				res.append("        Type: NS (Name Server) (2)\n");
			}else if (type==15) {
				res.append("        Type: MX (Mail Server Name) (15)\n");
			}else if (type==12) {
				res.append("        Type: PTR (domain name PoinTeR) (12)\n");
			}else {
				res.append("        Type non pris en charge\n");
			}
			i=i+2;
			int classe=Integer.parseInt(in[i]+in[i+1],16);
			//5 types principaux + affinité
			if (classe==0) {
				res.append("        Classe: Reserved (0x0000)\n");
			}else if (classe==1) {
				res.append("        Classe: IN (0x0001)\n");
			}else if (classe==2) {
				res.append("        Classe: Unassigned (0x0002)\n");
			}else if (classe==3) {
				res.append("        Classe: Chaos (CH) (0x0003)\n");
			}else if (classe==4) {
				res.append("        Classe: Hesiod (HS) (0x0004)\n");
			}else if ((classe>=5)&&(classe<=253)) {
				res.append("        Classe: Unassigned (0x"+in[i]+in[i+1]+")\n");
			}else if (classe==254) {
				res.append("        Classe: Qclass None (0x"+in[i]+in[i+1]+")\n");
			}else if (classe==255) {
				res.append("        Classe: Qclass *(Any) (0x"+in[i]+in[i+1]+")\n");
			}else if ((classe>=256)&&(classe<=65279)) {
				res.append("        Classe: Unassigned (0x"+in[i]+in[i+1]+")\n");
			}else if ((classe>=65280)&&(classe<=65534)) {
				res.append("        Classe: Reserved for private Use (0x"+in[i]+in[i+1]+")\n");
			}else {
				res.append("        Classe: Reserved (0x"+in[i]+in[i+1]+")\n");
			}
			
			//We need to get to the next octet now that class is done
			i+=2;
			
			//Get the 4 next octets for TTL
			try {
				int ttl=Integer.parseInt(in[i]+in[i+1]+in[i+2]+in[i+3],16);
				res.append("Time to Live : "+ttl+" seconds\n");
			}catch(Exception e) {
				return "DNS Invalid Data TTL\n";
			}
			
			//Rdata part
			i+=4;
			
			try {
				
				int rdlength=Integer.parseInt(in[i]+in[i+1],16);
				res.append("DATA Length : "+rdlength+"\n");
				//The data
				i+=2;
				res3=new StringBuilder();
				//IPV4 case
				//Compression does not work on ipadresses
				if (type==1) {
					int ipc=0;
					while (ipc<rdlength) {
						res3.append(Integer.parseInt(in[i],16)+".");
						ipc++;
						i++;
						
					}
					//remove the dot at the end
					if (res3.toString().charAt(res3.toString().length()-1)=='.') {
						res3.deleteCharAt(res3.length()-1);
					}
					

					res.append("    IPv4 Address : "+res3.toString()+"\n");
			
				}else if (type==28) {
					//V6 address we have to get 2 octets each time
					int ipc=0;
					while (ipc<rdlength) {
						res3.append(in[i]+in[i+1]+":");
						ipc+=2;
						i+=2;
					}
					//remove the dot at the end
					if (res3.toString().charAt(res3.toString().length()-1)==':') {
						res3.deleteCharAt(res3.length()-1);
					}

					res.append("    IPv6 Address : "+res3.toString()+"\n");
				}else if (type==5) {
					//CNAME
					int ipc=0;
					while (ipc<rdlength) {
						boolean isC=false;
						tmp = Input_Parser.hexToBin(in[i]);
						
						
						//Compression part
						while (tmp.substring(0,2).equals("11")) {
							isC=true;
							
							//Get the value of offset
							int pointer=Integer.parseInt(tmp.substring(2)+Input_Parser.hexToBin(in[i+1]),2);
							if(pointer>=i) {
								return "DNS Invalid Data pointerCname\n";
							}
							//Get the name until 00
							while (!(in[pointer].equals("00"))) {
								
								if((in[pointer].substring(0,2).equals("11"))) {
									int pt=Integer.parseInt(in[pointer].substring(2)+hexToBin(in[pointer+1]),2);
									//Start the recusive method
									res3.append(dnsRec(in,pt,map));
									i+=2;
									ipc+=2;
									if(ipc>=rdlength) {
										break;
									}
									tmp= hexToBin(in[i]);
									pointer+=(1+Integer.parseInt(in[pointer],16));
									continue;
									
								}
								if(map.containsKey(pointer)) {
									res3.append(map.get(pointer)+".");
								}else {
									
									//return res.toString()+"DNS Invalid CNAME\n";
									res3.append(in[i]);
								}
								pointer+=(1+Integer.parseInt(in[pointer],16));
								
								
							}
							i+=2;
							ipc+=2;
							if(ipc>=rdlength) {
								break;
							}
							
							tmp= hexToBin(in[i]);
							
						}
						
						//if we did compression there is no 00 in the end
						if(isC) {
							i--;
							break;
						}
						
						//Total length
						int curlength=Integer.parseInt(in[i],16);
						i++;
						String ra="";
						int fin=i+curlength;
						int index=i;
						for (int o=i;o<fin;o++) {
							ra=ra+in[o];
							i++;
						}
						res3.append(Input_DHCP.hexToAscii(ra)+".");
						map.put(index-1, Input_DHCP.hexToAscii(ra));
						ipc+=curlength+1;
						
					}
					if (res3.toString().charAt(res3.toString().length()-1)=='.') {
						res3.deleteCharAt(res3.length()-1);
					}
					res.append("CNAME : "+res3.toString()+"\n");
				}else if (type==2) {
					int ipc=0;
					while (ipc<rdlength) {
						boolean isC=false;
						tmp = Input_Parser.hexToBin(in[i]);
						
						
						//Compression part
						while (tmp.substring(0,2).equals("11")) {
							isC=true;
							
							//Get the value of offset
							int pointer=Integer.parseInt(tmp.substring(2)+Input_Parser.hexToBin(in[i+1]),2);
							if(pointer>=i) {
								return "DNS Invalid Data pointerCname\n";
							}
							//Get the name until 00
							while (!(in[pointer].equals("00"))) {
								
								if((in[pointer].substring(0,2).equals("11"))) {
									int pt=Integer.parseInt(in[pointer].substring(2)+hexToBin(in[pointer+1]),2);
									//Start the recusive method
									res3.append(dnsRec(in,pt,map));
									i+=2;
									ipc+=2;
									if(ipc>=rdlength) {
										break;
									}
									tmp= hexToBin(in[i]);
									pointer+=(1+Integer.parseInt(in[pointer],16));
									continue;
									
								}
								if(map.containsKey(pointer)) {
									res3.append(map.get(pointer)+".");
								}else {
									
									//return res.toString()+"DNS Invalid CNAME\n";
									res3.append(in[i]);
								}
								pointer+=(1+Integer.parseInt(in[pointer],16));
								
								
							}
							i+=2;
							ipc+=2;
							if(ipc>=rdlength) {
								break;
							}
							
							tmp= hexToBin(in[i]);
							
						}
						
						//if we did compression there is no 00 in the end
						if(isC) {
							i--;
							break;
						}
						
						//Total length
						int curlength=Integer.parseInt(in[i],16);
						i++;
						String ra="";
						int fin=i+curlength;
						int index=i;
						for (int o=i;o<fin;o++) {
							ra=ra+in[o];
							i++;
						}
						res3.append(Input_DHCP.hexToAscii(ra)+".");
						map.put(index-1, Input_DHCP.hexToAscii(ra));
						ipc+=curlength+1;
						
					}
					if (res3.toString().charAt(res3.toString().length()-1)=='.') {
						res3.deleteCharAt(res3.length()-1);
					}
					res.append("NS : "+res3.toString()+"\n");
				}else if (type==15) {
					int ipc=0;
					while (ipc<rdlength) {
						boolean isC=false;
						tmp = Input_Parser.hexToBin(in[i]);
						
						
						//Compression part
						while (tmp.substring(0,2).equals("11")) {
							isC=true;
							
							//Get the value of offset
							int pointer=Integer.parseInt(tmp.substring(2)+Input_Parser.hexToBin(in[i+1]),2);
							if(pointer>=i) {
								return "DNS Invalid Data pointerCname\n";
							}
							//Get the name until 00
							while (!(in[pointer].equals("00"))) {
								
								if((in[pointer].substring(0,2).equals("11"))) {
									int pt=Integer.parseInt(in[pointer].substring(2)+hexToBin(in[pointer+1]),2);
									//Start the recusive method
									res3.append(dnsRec(in,pt,map));
									i+=2;
									ipc+=2;
									if(ipc>=rdlength) {
										break;
									}
									tmp= hexToBin(in[i]);
									pointer+=(1+Integer.parseInt(in[pointer],16));
									continue;
									
								}
								if(map.containsKey(pointer)) {
									res3.append(map.get(pointer)+".");
								}else {
									
									//return res.toString()+"DNS Invalid CNAME\n";
									res3.append(in[i]);
								}
								pointer+=(1+Integer.parseInt(in[pointer],16));
								
								
							}
							i+=2;
							ipc+=2;
							if(ipc>=rdlength) {
								break;
							}
							
							tmp= hexToBin(in[i]);
							
						}
						
						//if we did compression there is no 00 in the end
						if(isC) {
							i--;
							break;
						}
						
						//Total length
						int curlength=Integer.parseInt(in[i],16);
						i++;
						String ra="";
						int fin=i+curlength;
						int index=i;
						for (int o=i;o<fin;o++) {
							ra=ra+in[o];
							i++;
						}
						res3.append(Input_DHCP.hexToAscii(ra)+".");
						map.put(index-1, Input_DHCP.hexToAscii(ra));
						ipc+=curlength+1;
						
					}
					if (res3.toString().charAt(res3.toString().length()-1)=='.') {
						res3.deleteCharAt(res3.length()-1);
					}
					res.append("MX : "+res3.toString()+"\n");
				}else {
					i+=rdlength;
				}
				
				
			}catch(Exception e) {
				return "DNS Invalid Data\n";
			}
		}
		
		res3= new StringBuilder();
		if(arr >0) res.append("Authoritative: \n");
		
		for (int j=0;j<arr;j++) {
			
			int totlength=0;//taille total du name de 1 question
			int nbmots=0;//nb mots de 1 question
			
			while (!in[i].equals("00")) {
				boolean isC=false;
				tmp = Input_Parser.hexToBin(in[i]);
				
				
				//Compression part
				while (tmp.substring(0,2).equals("11")) {
					int colen=0;
					isC=true;
					
					//Get the value of offset
					
					int pointer=Integer.parseInt(tmp.substring(2)+Input_Parser.hexToBin(in[i+1]),2);
					
					if(pointer>=i) {
						return "DNS Invalid Data\n";
					}
					
					//Get the name until 00
					while (!(in[pointer].equals("00"))) {
						
						//Tentative de vérification de pointeur vers un autre pointeur mais impossible sur n pointeurs eparpilles
						/*String cal=Input_Parser.hexToBin(in[pointer]).substring(0,2);
						int pt=Integer.parseInt(in[pointer].substring(2)+Input_Parser.hexToBin(in[pointer+1]),2);
						while(cal.equals("11")) {
							cal=Input_Parser.hexToBin(in[Integer.parseInt(in[pt])]).substring(0,2);
							if(cal.equals("11"))
								pt=Integer.parseInt(in[pt].substring(2)+Input_Parser.hexToBin(in[pt+1]),2);
						}
						
						while(!(in[pt].equals("00"))) {
							if(map.containsKey(pt)) {
								res3.append(map.get(pt)+".");
								nbmots+=1;
							}else {
								return res.toString()+"DNS Invalid data Pointer Answer of Pointer";
							}
							colen+=Integer.parseInt(in[pointer],16);
							pt+=(1+Integer.parseInt(in[pt],16));
						}*/
						if((in[pointer].substring(0,2).equals("11"))) {
							int pt=Integer.parseInt(in[pointer].substring(2)+hexToBin(in[pointer+1]),2);
							System.out.println("Value "+pt);
							//Start the recusive method
							res3.append(dnsRec(in,pt,map));
							i+=2;
							tmp= hexToBin(in[i]);
							pointer+=(1+Integer.parseInt(in[pointer],16));
							continue;
							
						}
						
						if(map.containsKey(pointer)) {
							res3.append(map.get(pointer)+".");
							nbmots+=1;
						}else {
							return res.toString()+"DNS Invalid data Pointer Answer";
						}
						colen+=Integer.parseInt(in[pointer],16);
						pointer+=(1+Integer.parseInt(in[pointer],16));
						
						
					}
					
					totlength+=colen;
					i+=2;
					tmp= hexToBin(in[i]);
					
					
				}
				
				//if we did compression there is no 00 in the end
				if(isC) {
					i--;
					break;
				}
				
				//Total length
				int aclength=Integer.parseInt(in[i],16);
				totlength+=aclength;
				i++;
				String ra="";
				int fin=i+aclength;
				int index=i;
				for (int o=i;o<fin;o++) {
					ra=ra+in[o];
					i++;
				}
				res3.append(Input_DHCP.hexToAscii(ra)+".");
				nbmots++;
				//Put the word in hashmap if new offset
				map.put(index, Input_DHCP.hexToAscii(ra));
			}
			
			//remove the dot at the end
			if (res3.toString().charAt(res3.toString().length()-1)=='.') {
				res3.deleteCharAt(res3.length()-1);
			}
			
			//We need to increment to the next octet
			i++;
			

			res.append("    Name: "+res3.toString()+"\n");
			res.append("        [Name length: "+totlength+"]\n");
			res.append("        [Label Count: "+nbmots+"]\n");
			nbmots=0;
			
						
			
			//debut des if else
			int type=Integer.parseInt(in[i]+in[i+1],16);
			//5 types principaux + affinitÃ©
			if (type==1) {
				res.append("        Type: A (IPV4) (1)\n");
			}else if (type==28) {
				res.append("        Type: AAAA (IPV6) (28)\n");
			}else if (type==5) {
				res.append("        Type: CNAME (Canonical name) (5)\n");
			}else if (type==2) {
				res.append("        Type: NS (Name Server) (2)\n");
			}else if (type==15) {
				res.append("        Type: MX (Mail Server Name) (15)\n");
			}else if (type==12) {
				res.append("        Type: PTR (domain name PoinTeR) (12)\n");
			}else {
				res.append("        Type non pris en charge\n");
			}
			i=i+2;
			int classe=Integer.parseInt(in[i]+in[i+1],16);
			//5 types principaux + affinité
			if (classe==0) {
				res.append("        Classe: Reserved (0x0000)\n");
			}else if (classe==1) {
				res.append("        Classe: IN (0x0001)\n");
			}else if (classe==2) {
				res.append("        Classe: Unassigned (0x0002)\n");
			}else if (classe==3) {
				res.append("        Classe: Chaos (CH) (0x0003)\n");
			}else if (classe==4) {
				res.append("        Classe: Hesiod (HS) (0x0004)\n");
			}else if ((classe>=5)&&(classe<=253)) {
				res.append("        Classe: Unassigned (0x"+in[i]+in[i+1]+")\n");
			}else if (classe==254) {
				res.append("        Classe: Qclass None (0x"+in[i]+in[i+1]+")\n");
			}else if (classe==255) {
				res.append("        Classe: Qclass *(Any) (0x"+in[i]+in[i+1]+")\n");
			}else if ((classe>=256)&&(classe<=65279)) {
				res.append("        Classe: Unassigned (0x"+in[i]+in[i+1]+")\n");
			}else if ((classe>=65280)&&(classe<=65534)) {
				res.append("        Classe: Reserved for private Use (0x"+in[i]+in[i+1]+")\n");
			}else {
				res.append("        Classe: Reserved (0x"+in[i]+in[i+1]+")\n");
			}
			
			//We need to get to the next octet now that class is done
			i+=2;
			
			//Get the 4 next octets for TTL
			try {
				int ttl=Integer.parseInt(in[i]+in[i+1]+in[i+2]+in[i+3],16);
				res.append("Time to Live : "+ttl+" seconds\n");
			}catch(Exception e) {
				return "DNS Invalid Data TTL\n";
			}
			
			//Rdata part
			i+=4;
			
			try {
				
				int rdlength=Integer.parseInt(in[i]+in[i+1],16);
				res.append("DATA Length : "+rdlength+"\n");
				//The data
				i+=2;
				res3=new StringBuilder();
				//IPV4 case
				//Compression does not work on ipadresses
				if (type==1) {
					int ipc=0;
					while (ipc<rdlength) {
						res3.append(Integer.parseInt(in[i],16)+".");
						ipc++;
						i++;
						
					}
					//remove the dot at the end
					if (res3.toString().charAt(res3.toString().length()-1)=='.') {
						res3.deleteCharAt(res3.length()-1);
					}
					

					res.append("    IPv4 Address : "+res3.toString()+"\n");
			
				}else if (type==28) {
					//V6 address we have to get 2 octets each time
					int ipc=0;
					while (ipc<rdlength) {
						res3.append(in[i]+in[i+1]+":");
						ipc+=2;
						i+=2;
					}
					//remove the dot at the end
					if (res3.toString().charAt(res3.toString().length()-1)==':') {
						res3.deleteCharAt(res3.length()-1);
					}

					res.append("    IPv6 Address : "+res3.toString()+"\n");
				}else if (type==5) {
					//CNAME
					int ipc=0;
					while (ipc<rdlength) {
						boolean isC=false;
						tmp = Input_Parser.hexToBin(in[i]);
						
						
						//Compression part
						while (tmp.substring(0,2).equals("11")) {
							isC=true;
							
							//Get the value of offset
							int pointer=Integer.parseInt(tmp.substring(2)+Input_Parser.hexToBin(in[i+1]),2);
							if(pointer>=i) {
								return "DNS Invalid Data pointerCname\n";
							}
							//Get the name until 00
							while (!(in[pointer].equals("00"))) {
								
								if((in[pointer].substring(0,2).equals("11"))) {
									int pt=Integer.parseInt(in[pointer].substring(2)+hexToBin(in[pointer+1]),2);
									//Start the recusive method
									res3.append(dnsRec(in,pt,map));
									i+=2;
									ipc+=2;
									if(ipc>=rdlength) {
										break;
									}
									tmp= hexToBin(in[i]);
									pointer+=(1+Integer.parseInt(in[pointer],16));
									continue;
									
								}
								if(map.containsKey(pointer)) {
									res3.append(map.get(pointer)+".");
								}else {
									
									//return res.toString()+"DNS Invalid CNAME\n";
									res3.append(in[i]);
								}
								pointer+=(1+Integer.parseInt(in[pointer],16));
								
								
							}
							i+=2;
							ipc+=2;
							if(ipc>=rdlength) {
								break;
							}
							
							tmp= hexToBin(in[i]);
							
						}
						
						//if we did compression there is no 00 in the end
						if(isC) {
							i--;
							break;
						}
						
						//Total length
						int curlength=Integer.parseInt(in[i],16);
						i++;
						String ra="";
						int fin=i+curlength;
						int index=i;
						for (int o=i;o<fin;o++) {
							ra=ra+in[o];
							i++;
						}
						res3.append(Input_DHCP.hexToAscii(ra)+".");
						map.put(index-1, Input_DHCP.hexToAscii(ra));
						ipc+=curlength+1;
						
					}
					if (res3.toString().charAt(res3.toString().length()-1)=='.') {
						res3.deleteCharAt(res3.length()-1);
					}
					res.append("CNAME : "+res3.toString()+"\n");
				}else if (type==2) {
					int ipc=0;
					while (ipc<rdlength) {
						boolean isC=false;
						tmp = Input_Parser.hexToBin(in[i]);
						
						
						//Compression part
						while (tmp.substring(0,2).equals("11")) {
							isC=true;
							
							//Get the value of offset
							int pointer=Integer.parseInt(tmp.substring(2)+Input_Parser.hexToBin(in[i+1]),2);
							if(pointer>=i) {
								return "DNS Invalid Data pointerCname\n";
							}
							//Get the name until 00
							while (!(in[pointer].equals("00"))) {
								
								if((in[pointer].substring(0,2).equals("11"))) {
									int pt=Integer.parseInt(in[pointer].substring(2)+hexToBin(in[pointer+1]),2);
									//Start the recusive method
									res3.append(dnsRec(in,pt,map));
									i+=2;
									ipc+=2;
									if(ipc>=rdlength) {
										break;
									}
									tmp= hexToBin(in[i]);
									pointer+=(1+Integer.parseInt(in[pointer],16));
									continue;
									
								}
								if(map.containsKey(pointer)) {
									res3.append(map.get(pointer)+".");
								}else {
									
									//return res.toString()+"DNS Invalid CNAME\n";
									res3.append(in[i]);
								}
								pointer+=(1+Integer.parseInt(in[pointer],16));
								
								
							}
							i+=2;
							ipc+=2;
							if(ipc>=rdlength) {
								break;
							}
							
							tmp= hexToBin(in[i]);
							
						}
						
						//if we did compression there is no 00 in the end
						if(isC) {
							i--;
							break;
						}
						
						//Total length
						int curlength=Integer.parseInt(in[i],16);
						i++;
						String ra="";
						int fin=i+curlength;
						int index=i;
						for (int o=i;o<fin;o++) {
							ra=ra+in[o];
							i++;
						}
						res3.append(Input_DHCP.hexToAscii(ra)+".");
						map.put(index-1, Input_DHCP.hexToAscii(ra));
						ipc+=curlength+1;
						
					}
					if (res3.toString().charAt(res3.toString().length()-1)=='.') {
						res3.deleteCharAt(res3.length()-1);
					}
					res.append("NS : "+res3.toString()+"\n");
				}else if (type==15) {
					int ipc=0;
					while (ipc<rdlength) {
						boolean isC=false;
						tmp = Input_Parser.hexToBin(in[i]);
						
						
						//Compression part
						while (tmp.substring(0,2).equals("11")) {
							isC=true;
							
							//Get the value of offset
							int pointer=Integer.parseInt(tmp.substring(2)+Input_Parser.hexToBin(in[i+1]),2);
							if(pointer>=i) {
								return "DNS Invalid Data pointerCname\n";
							}
							//Get the name until 00
							while (!(in[pointer].equals("00"))) {
								
								if((in[pointer].substring(0,2).equals("11"))) {
									int pt=Integer.parseInt(in[pointer].substring(2)+hexToBin(in[pointer+1]),2);
									//Start the recusive method
									res3.append(dnsRec(in,pt,map));
									i+=2;
									ipc+=2;
									if(ipc>=rdlength) {
										break;
									}
									tmp= hexToBin(in[i]);
									pointer+=(1+Integer.parseInt(in[pointer],16));
									continue;
									
								}
								if(map.containsKey(pointer)) {
									res3.append(map.get(pointer)+".");
								}else {
									
									//return res.toString()+"DNS Invalid CNAME\n";
									res3.append(in[i]);
								}
								pointer+=(1+Integer.parseInt(in[pointer],16));
								
								
							}
							i+=2;
							ipc+=2;
							if(ipc>=rdlength) {
								break;
							}
							
							tmp= hexToBin(in[i]);
							
						}
						
						//if we did compression there is no 00 in the end
						if(isC) {
							i--;
							break;
						}
						
						//Total length
						int curlength=Integer.parseInt(in[i],16);
						i++;
						String ra="";
						int fin=i+curlength;
						int index=i;
						for (int o=i;o<fin;o++) {
							ra=ra+in[o];
							i++;
						}
						res3.append(Input_DHCP.hexToAscii(ra)+".");
						map.put(index-1, Input_DHCP.hexToAscii(ra));
						ipc+=curlength+1;
						
					}
					if (res3.toString().charAt(res3.toString().length()-1)=='.') {
						res3.deleteCharAt(res3.length()-1);
					}
					res.append("MX : "+res3.toString()+"\n");
				}else {
					i+=rdlength;
				}
				
				
			}catch(Exception e) {
				return "DNS Invalid Data\n";
			}
		}
		System.out.println("vals "+in[i]+in[i+1]);
		
		
		
		return res.toString();
		
	}
	
	private static String dnsRec(String[] in, int pt,Map<Integer,String> map) {
		StringBuilder res = new StringBuilder();
		if(in[pt].equals("00")) { 
			return "";
		}else if(in[pt].substring(0,2).equals("11")) {
			pt=Integer.parseInt(in[pt].substring(2)+hexToBin(in[pt]));
			res.append(dnsRec(in,pt,map));
			return res.toString();
		}else {
			if(map.containsKey(pt)) {
				res.append(map.get(pt));
				return res.toString();
			}else {
				//We try to get an inexisting value
				return "Invalid";
			}
		}
		
	}
	
	public static String [] dnsData(String[] in, int start) {
		String[] res=new String[Input_Parser.udpLong(Input_Parser.protocoleHData(in, Input_Parser.protocoleHStartByIP(Input_Parser.ipHData(in))))];
		int tmp=0;
		for (int i=start;i<in.length;i++) {
			res[tmp]=in[i];
			tmp++;
		}
		return res;
	}
	
	public static String hexToBin(String hex){
	    String bin = "";
	    String binFragment = "";
	    int iHex;
	    hex = hex.trim();
	    hex = hex.replaceFirst("0x", "");

	    for(int i = 0; i < hex.length(); i++){
	        iHex = Integer.parseInt(""+hex.charAt(i),16);
	        binFragment = Integer.toBinaryString(iHex);

	        while(binFragment.length() < 4){
	            binFragment = "0" + binFragment;
	        }
	        bin += binFragment;
	    }
	    return bin;
	}
	
}
