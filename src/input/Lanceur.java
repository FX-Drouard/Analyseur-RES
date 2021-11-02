package input;
import java.io.IOException;

public class Lanceur {
	private final String datas;
	private String select;
	private int actuel;
	public Lanceur(String path) throws IOException {
		this.datas=Input_Parser.parse(path);
		this.setectData(0);
	}
	public void setectData(int choix) {
		//a changer
		
		select=datas;
		actuel=choix;
	}
	public String ethernetToString() {
		StringBuilder res=new StringBuilder();
		
		String[] tmp=Input_Parser.split(select);
		String[] ethernet=Input_Parser.ethernetData(tmp);
		res.append(Input_Parser.ethernetToString(ethernet));
		return res.toString();
	}
	public String ipHToString() {
		StringBuilder res=new StringBuilder();
		String[] tmp=Input_Parser.split(select);
		String[] ipH=Input_Parser.ipHData(tmp);
		res.append(Input_Parser.ipHToString(ipH));
		return res.toString();
	}
	public String brutToString() {
		return select;
	}
	public String protocoleToString() {
		return "TODO";
	}
	public String dToString() {
		return "TODO";
	}
	public String protocolNameToString() {
		String[] tmp=Input_Parser.split(select);
		String[] ipH=Input_Parser.ipHData(tmp);
		return Input_Parser.protocolenameToString(ipH);
	}
	public String dNameToString() {
		return "DNS";
	}
	public int nbChoix() {
		//a changer
		return 1;
	}
	public int getActuel() {
		return actuel;
	}
}
