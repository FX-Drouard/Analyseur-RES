package output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class output_parser {

	public static String writer(String path, String txt) throws IOException {
		String pathok = path.split(".txt")[0];
		try {
			   File file = new File(pathok+".txt");
			   int i =0;
			   // créer le fichier s'il n'existe pas
			   while (file.exists()) {
				   i++;
				   pathok = path.split(".txt")[0]+"("+i+")";
				   file = new File(pathok+".txt");
			   }
			   file.createNewFile();
			   FileWriter fw = new FileWriter(file.getAbsoluteFile());
			   BufferedWriter bw = new BufferedWriter(fw);
			   bw.write(txt);
			   bw.close();
			   return pathok+".txt";
			  } catch (IOException e) {
				 throw new IOException("Erreur");
			  }
	}
}
