package input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;



public class Input_Parser {
	private Input_Parser(String fileName) {}
	public static String[][] parse(String fileName) throws IOException,FileNotFoundException {//Lis le fichier
		BufferedReader br = null;
		StringBuilder res=new StringBuilder();
		try{
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while((line=br.readLine())!=null) {
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
		String[][] allres=filterCall(res.toString().toLowerCase());
		return allres;
	}
	
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
	}
	
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
				if(tsplit[j]!="" && tsplit[j].length()==2) {
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
	
	public static String[][] filterCall(String in){
		String[]tmp=splitOffSet(in);
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
	
	public static String dnsToString(String[] in) {
		if (in.length!=Input_Parser.udpLong(Input_Parser.protocoleHData(in, Input_Parser.protocoleHStartByIP(Input_Parser.ipHData(in))))) {throw new RuntimeException("Appel erroné dnsToString");}
		return "TODO DNS";
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
		StringBuilder res2= new StringBuilder();res2.append("DHCP Message Type: Unknown\n");
		String tmp=in[0];
		if (tmp.equals("01")) {
			res.append("Message Type: Boot Request (0x01)\n");
		}else if (tmp.equals("02")) {
			res.append("Message Type: Boot Reply (0x02)\\n");
		}else {
			res.append("Message Type: Unknown (0x"+tmp+")\\n");
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
					res.append("DHCP Option: "+Input_DHCP.getOption(Integer.parseInt(tmp,16))+"("+index+")");
				}
				else {
					res.append("DHCP Option: Unknown "+index+"\n");
				}
				
				int len=Integer.parseInt(in[i+1],16);
				//option end 255
				if (tmp.equals("ff")||tmp.equals("FF")) {
					lastind=i+len+1;
					fin=false;//padding todo
				
				}
				
				if (Integer.parseInt(tmp,16)==53) {
					if (in[i+1+len].equals("01")) {
						res.append(" : DISCOVER\n");
					}else if (in[i+1+len].equals("02")) {
						res.append(" : OFFER\n");
					}else if (in[i+1+len].equals("03")) {
						res.append(" : REQUEST\n");
					}else if (in[i+1+len].equals("04")) {

						res.append(" : DECLINE\n");
					}else if (in[i+1+len].equals("05")) {
						res.append(" : ACK\n");
					}else if (in[i+1+len].equals("06")) {

						res.append(" : NAK\n");
					}else if (in[i+1+len].equals("07")) {
						res.append(" : RELEASE\n");
					}else if (in[i+1+len].equals("08")) {

						res.append(" : INFORM\n");
					}else {
						res.append(" : UNKNOWN\n");
					}
				}else {
					res.append("\n");
				}
				res.append("    Longueur: "+len+"\n");
				
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
			res.append(""
					+ "\nPadding : "+pad.toString());
		}
		
		return res.toString();
		
	}
	
	
	
	
}
