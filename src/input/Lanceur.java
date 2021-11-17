package input;
import java.io.IOException;

public class Lanceur {
	private final String[][] datas;
	private String [] select;
	private int actuel;
	public Lanceur(String path) throws IOException {
		this.datas=Input_Parser.parse(path);
		this.setectData(0);
	}
	public void setectData(int choix) {
		select=datas[choix];
		actuel=choix;
	}
	public String ethernetToString() {
		StringBuilder res=new StringBuilder();
		
		String[] ethernet=Input_Parser.ethernetData(select);
		res.append(Input_Parser.ethernetToString(ethernet));
		return res.toString();
	}
	public String ipHToString() {
		StringBuilder res=new StringBuilder();
		String[] ipH=Input_Parser.ipHData(select);
		res.append(Input_Parser.ipHToString(ipH));
		return res.toString();
	}
	public String brutToString() {
		String res ="";
		for (int i = 0;i<select.length;i++) {
			res=res+select[i]+" ";
		}
		return res;
	}
	public String protocoleToString() {
		return "TODO";
	}
	public String dToString() {
		return "TODO";
	}
	public String protocolNameToString() {
		String[] ipH=Input_Parser.ipHData(select);
		return Input_Parser.protocolenameToString(ipH);
	}
	public String dNameToString() {
		return "DNS";
	}
	public int nbChoix() {
		
		return datas.length;
	}
	public int getActuel() {
		return actuel;
	}
}
