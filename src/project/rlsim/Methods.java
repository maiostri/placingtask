package project.rlsim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


public class Methods {

	public static void deleteDirectory(File file) throws IOException{

		File[] files = file.listFiles();
		if(files!=null) { //some JVMs return null for empty dirs
		    for(File f: files) {
		        if(f.isDirectory()) {
		        	deleteDirectory(f);
		        } else {
		            f.delete();
		        }
		    }
		}
		file.delete();

	}

	public static void writeFile(File file, RankedList r) throws IOException{

		List<BigDecimal> distanceList = r.getNewDistances();

		List<String> videos = r.getRanking();

		BufferedWriter bw = new BufferedWriter(new FileWriter(file));

		for(int i = 0; i < videos.size(); i++) {
		        String s = distanceList.get(i) + "\t" + videos.get(i);
		        bw.write(s);
		        bw.newLine();
		        bw.flush();
		}

		bw.close();

	}

	public static void writeString(String s, File file) throws IOException{


		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));


		        bw.write(s);
		        bw.newLine();
		        bw.flush();


		bw.close();

	}


}
