package input;
import java.io.IOException;

public class Lanceur {
	private final String datas;
	public Lanceur(String path) throws IOException {
		this.datas=Input_Parser.parse(path);
	}
	public String ethernetToString() {
		StringBuilder res=new StringBuilder();
		String[] tmp=Input_Parser.split(datas);
		String[] ethernet=Input_Parser.ethernetData(tmp);
		res.append(Input_Parser.ethernetToString(ethernet));
		return res.toString();
	}
	public String ipHToString() {
		StringBuilder res=new StringBuilder();
		String[] tmp=Input_Parser.split(datas);
		String[] ipH=Input_Parser.ipHData(tmp);
		res.append(Input_Parser.ipHToString(ipH));
		return res.toString();
	}
	public String brutToString() {
		return datas;
	}
	public String protocolNameToString() {
		String[] tmp=Input_Parser.split(datas);
		String[] ipH=Input_Parser.ipHData(tmp);
		return Input_Parser.protocolenameToString(ipH);
	}
}
