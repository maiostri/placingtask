import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationPT {

	public static void main(String[] args) throws IOException {
		// local variables
		List<Run> list_Run = new ArrayList<Run>();

		Map<Integer, Position> idPositionDVLPMap = new HashMap<Integer, Position>();

		Map<Integer, Position> idPositionTestMap = new HashMap<Integer, Position>();

		List<MediaSample> mediaSampleList = new ArrayList<MediaSample>();

		List<Double> thresholdsList = new ArrayList<Double>(
				Arrays.asList(new Double[] { 1.0, 10.0, 20.0, 50.0, 100.0,
						200.0, 500.0, 1000.0, 15000.0, 20000.0 }));

		System.out
				.println("Valitation toolbox by Pascal Kelm for the Placing Task at MediaEval.");
		System.out.println("");

		// Optional argument for new thresholds

		/*********************
		 * Import ground truth
		 *********************/
		System.out.print("Import ground truth... ");

		// Read file
		String s = null;
		BufferedReader in = new BufferedReader(new FileReader("pt2012.txt"));

		while ((s = in.readLine()) != null) {
			String[] arg = s.split(";");
			Position temp_Pos = new Position();
			temp_Pos.FileName = Integer.parseInt(arg[0].trim());
			temp_Pos.Latitude = Double.parseDouble(arg[1].trim());
			temp_Pos.Longitude = Double.parseDouble(arg[2].trim());
			idPositionTestMap.put(temp_Pos.FileName, temp_Pos);
		}
		in.close();

		System.out.println(" Done ground truth. ");

		// Read training file
		System.out.print("Import training file... ");

		s = null;
		in = new BufferedReader(new FileReader("MediaEval2012DvlpLatLong.txt"));
		while ((s = in.readLine()) != null) {
			String[] arg = s.split(";");
			Position temp_Pos = new Position();
			temp_Pos.FileName = Integer.parseInt(arg[0].trim().substring(0, 5));
			temp_Pos.Latitude = Double.parseDouble(arg[1].trim());
			temp_Pos.Longitude = Double.parseDouble(arg[2].trim());
			idPositionDVLPMap.put(temp_Pos.FileName, temp_Pos);
		}
		in.close();

		System.out.println(" Done training. ");

		// Import *.list files.
		File[] files = new File(args[0]).listFiles();

		for (File file : files) {

			Integer fileId = processFileName(file.getName());

			System.out.println(fileId);

			MediaSample mediaSample = new MediaSample();
			mediaSample.setFileName(file.getName());
			in = new BufferedReader(new FileReader(file.getAbsoluteFile()));

			Position correctPosition = idPositionDVLPMap.get(fileId);

			while ((s = in.readLine()) != null) {
				String[] s_args = s.trim().split("\t");
				Run temp_Run = new Run();

				temp_Run.similarityFactor = Double.parseDouble(s_args[0].trim()
						.replace(",", "."));

				String id = s_args[1].trim();

				temp_Run.fileName = processString(id);

				// temp_Run.fileName = Integer.parseInt(s_args[1].trim());

				Position pos_gt = findPosition(idPositionDVLPMap,
						temp_Run.fileName);
				if (pos_gt != null) {
					temp_Run.HavesineDistance = calc_HaversineDist(temp_Run,
							correctPosition);
					temp_Run.GroundTruth = pos_gt;
					mediaSample.addSimilarityElement(temp_Run);
					list_Run.add(temp_Run);
				}

			}
			mediaSampleList.add(mediaSample);

		}

		for (MediaSample mediaSample : mediaSampleList) {
			determineMediaAnswer(mediaSample);
		}

		System.out.println(" Done.");

		/****************
		 * Calc thresholds
		 ****************/
		System.out.print("Calculating thresholds... ");
		List<Rang> list_range = new ArrayList<Rang>();
		for (MediaSample mediaSample : mediaSampleList) {
			Rang temp_rang = new Rang();

			for (Double threshold : thresholdsList) {
				if (mediaSample.getAnswerMedia().HavesineDistance < threshold) {
					temp_rang.Threshold = threshold;
				}
			}

			temp_rang.mediaFile = mediaSample.getFileName();
			list_range.add(temp_rang);
		}


		/*****************
		 * Show Results
		 *****************/

		File fileList = new File("finalList.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileList));
		for (Rang rang : list_range) {
			bw.write("file: " + rang.mediaFile + " \t threshold: "
					+ rang.Threshold);
			bw.newLine();
		}
		bw.close();

		File fileListCompact = new File("finalListCompact.txt");
		BufferedWriter bwCompact = new BufferedWriter(new FileWriter(
				fileListCompact));
		bwCompact.write("Threshold \t Count \t Percentage");
		bwCompact.newLine();

		for (Double threshold : thresholdsList) {
			int countThreshold = 0;
			for (Rang rang : list_range) {
				if (rang.Threshold == threshold) {
					countThreshold++;					
				}
			}
			bwCompact.write(threshold + "\t " + countThreshold + " \t "
					+ (countThreshold * 100) / mediaSampleList.size());
			bwCompact.newLine();
		}

		bwCompact.close();

		// Save summary in folder
		System.out.println(" Finish.");

	}

	private static Integer processFileName(String name) {
		String fileName = name.replace("_list.txt", "");
		return Integer.parseInt(fileName);
	}

	private static Integer processString(String id) {
		String imageP1 = id.replace("FlickrVideosTrain/p1_", "");
		String imageP2 = id.replace("FlickrVideosTrain/p2_", "");
		return (imageP1.length() < imageP2.length()) ? Integer
				.parseInt(imageP1) : Integer.parseInt(imageP2);

	}

	private static void determineMediaAnswer(MediaSample mediaSample) {
		// Simple KNN with N = 1.
		mediaSample.setAnswerMedia(mediaSample.getSimilarityList().get(0));
	}

	public static double calc_HaversineDist(Run pos1, Position pos2) {
		double R = 6371.0;
		double dLat = toRadian(pos2.Latitude - pos1.Latitude);
		double dLon = toRadian(pos2.Longitude - pos1.Longitude);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(toRadian(pos1.Latitude))
				* Math.cos(toRadian(pos2.Latitude)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.min(1, Math.sqrt(a)));
		double d = R * c;
		return d;
	}

	public static double toRadian(double val) {
		return (Math.PI / 180) * val;
	}

	private static Position findPosition(Map<Integer, Position> idPositionMap,
			int name) {
		return idPositionMap.get(name);
	}

}

class Rang {
	public double Threshold;
	public int Count;
	public String mediaFile;
}

class Position {
	public int FileName;
	public double Latitude;
	public double Longitude;
}

class MediaSample {
	private String fileName;
	List<Run> similarityList;

	private Run answerMedia;

	public MediaSample() {
		this.similarityList = new ArrayList<Run>();
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public List<Run> getSimilarityList() {
		return this.similarityList;
	}

	public void addSimilarityElement(final Run element) {
		this.similarityList.add(element);
	}

	public void setAnswerMedia(final Run answerMedia) {
		this.answerMedia = answerMedia;
	}

	public Run getAnswerMedia() {
		return this.answerMedia;
	}

}

class Run {

	public Integer fileName;
	public double Latitude;
	public double Longitude;
	public double HavesineDistance;
	public double similarityFactor;
	public Position GroundTruth;

}
