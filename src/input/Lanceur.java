package input;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Lanceur {
	private final String[][] datas;
	private String [] select;
	private int actuel;
	public Lanceur(String path) throws IOException,FileNotFoundException {
		this.datas=Input_Parser.parse(path);
		this.setectData(0);
	}
	public void setectData(int choix) {
		select=datas[choix];
		actuel=choix;
	}
	public String ethernetToString() {
		return Input_Parser.ethernetToString(Input_Parser.ethernetData(select));
	}
	public String ipHToString() {
		return Input_Parser.ipHToString(Input_Parser.ipHData(select));
	}
	public String brutToString() {
		String res ="";
		for (int i = 0;i<select.length;i++) {
			res=res+select[i]+" ";
		}
		return res;
	}
	public String protocoleToString() {
		if (Input_Parser.protocolenameToString(Input_Parser.ipHData(select)).equals("UDP")) {
			return Input_Parser.protocoleHToString(Input_Parser.protocoleHData(select, Input_Parser.protocoleHStartByIP(Input_Parser.ipHData(select))));
		}else {
			return "Le Protocole: \""+Input_Parser.protocolenameToString(Input_Parser.ipHData(select))+"\" n'est pas pris en charge par ce projet!";
		}
	}
	
	public String protocolNameToString() {
		return Input_Parser.protocolenameToString(Input_Parser.ipHData(select));
	}
	
	public String dNameToString() {
		return Input_Parser.dNameToString(Input_Parser.protocoleHData(select, Input_Parser.protocoleHStartByIP(Input_Parser.ipHData(select))));
	}
	
	public String dToString() {
		if (this.dNameToString().equals("DHCP")) {
			
			return Input_Parser.dhcpToString(Input_Parser.dhcpData(select, Input_Parser.dStartByIP(Input_Parser.ipHData(select))),Input_Parser.udpLong(Input_Parser.protocoleHData(select, Input_Parser.protocoleHStartByIP(Input_Parser.ipHData(select)))));
		}else if (this.dNameToString().equals("DNS")) {
			return Input_Parser.dnsToString(Input_Parser.dnsData(select, Input_Parser.dStartByIP(Input_Parser.ipHData(select))),Input_Parser.udpLong(Input_Parser.protocoleHData(select, Input_Parser.protocoleHStartByIP(Input_Parser.ipHData(select)))));
		}else {return "Un Protocole Non Pris en charge est Détecter!";}
		
	}
	
	public int nbChoix() {
		
		return datas.length;
	}
	public int getActuel() {
		return actuel;
	}
}
