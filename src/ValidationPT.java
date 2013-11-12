
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

	private static final String DVLP_FILENAME = "MediaEval2012DvlpLatLong.txt";
    private static final String GROUND_TRUTH_FILENAME = "pt2012.txt";

    public static void main(String[] args) throws IOException {
	    if(args.length == 0){
	        throw new IllegalArgumentException("Wrong use. Please call the program specifying the .list files as the arguments.");
	    }

		// local variables
		List<Run> list_Run = new ArrayList<Run>();

		Map<Integer, Position> idPositionDVLPMap;

		Map<Integer, Position> idPositionTestMap;

		List<MediaSample> mediaSampleList = new ArrayList<MediaSample>();

		List<Double> thresholdsList = new ArrayList<Double>(Arrays.asList(new Double[] { 1.0, 10.0, 20.0, 50.0, 100.0, 200.0, 500.0, 1000.0, 15000.0, 20000.0 }));

		System.out.println("Valitation toolbox by Pascal Kelm for the Placing Task at MediaEval.\n");

		// Optional argument for new thresholds

		/*********************
		 * Import ground truth
		 *********************/
		System.out.print("Import ground truth... ");

		// Read file
		idPositionTestMap = loadGroundTruth();

		System.out.println(" Done ground truth. ");

		// Read training file
		System.out.print("Import training file... ");

		idPositionDVLPMap = loadDVLP();

		System.out.println(" Done training. ");

		// Import *.list files.
		File[] files = new File(args[0]).listFiles();

		for (File file : files) {

			Integer fileId = readFileNameId(file.getName());

			System.out.println(fileId);

			String s = null;
	        BufferedReader in;

			MediaSample mediaSample = new MediaSample();
			mediaSample.setFileName(file.getName());
			in = new BufferedReader(new FileReader(file.getAbsoluteFile()));

			Position correctPosition = idPositionDVLPMap.get(fileId);

			while ((s = in.readLine()) != null) {
				String[] s_args = s.trim().split("\t");
				Run temp_Run = new Run();

				temp_Run.setSimilarityFactor(Double.parseDouble(s_args[0].trim().replace(",", ".")));

				String id = s_args[1].trim();

				temp_Run.setFileName(processString(id));

				// temp_Run.fileName = Integer.parseInt(s_args[1].trim());

				Position pos_gt = findPosition(idPositionDVLPMap, temp_Run.getFileName());
				if (pos_gt != null) {
					temp_Run.setHavesineDistance(MathUtils.calcHaversineDistance(temp_Run, correctPosition));
					temp_Run.setGroundTruth(pos_gt);
					mediaSample.addSimilarityElement(temp_Run);
					list_Run.add(temp_Run);
				}

			}
			mediaSampleList.add(mediaSample);

			in.close();
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
				if (mediaSample.getAnswerMedia().getHavesineDistance() < threshold) {
					temp_rang.setThreshold(threshold);
				}
			}

			temp_rang.setMediaFile(mediaSample.getFileName());
			list_range.add(temp_rang);
		}


		/*****************
		 * Show Results
		 *****************/

		File fileList = new File("finalList.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileList));
		for (Rang rang : list_range) {
			bw.write("file: " + rang.getMediaFile() + " \t threshold: " + rang.getThreshold());
			bw.newLine();
		}
		bw.close();

		File fileListCompact = new File("finalListCompact.txt");
		BufferedWriter bwCompact = new BufferedWriter(new FileWriter(fileListCompact));
		bwCompact.write("Threshold \t Count \t Percentage");
		bwCompact.newLine();

		for (Double threshold : thresholdsList) {
			int countThreshold = 0;
			for (Rang rang : list_range) {
				if (rang.getThreshold() == threshold) {
					countThreshold++;
				}
			}
			bwCompact.write(threshold + "\t " + countThreshold + " \t " + (countThreshold * 100) / mediaSampleList.size());
			bwCompact.newLine();
		}

		bwCompact.close();

		// Save summary in folder
		System.out.println(" Finish.");
	}

    private static Map<Integer, Position> loadGroundTruth() throws IOException {
	    Map<Integer, Position> idPositionTestMap = new HashMap<Integer, Position>();

	    String s = null;
        BufferedReader in = new BufferedReader(new FileReader(GROUND_TRUTH_FILENAME));

        while ((s = in.readLine()) != null) {
            String[] arg = s.split(";");
            Position temp_Pos = new Position();
            temp_Pos.setFileName(Integer.parseInt(arg[0].trim()));
            temp_Pos.setLatitude(Double.parseDouble(arg[1].trim()));
            temp_Pos.setLongitude(Double.parseDouble(arg[2].trim()));
            idPositionTestMap.put(temp_Pos.getFileName(), temp_Pos);
        }
        in.close();

        return idPositionTestMap;
    }

    private static Map<Integer, Position> loadDVLP() throws IOException {
        Map<Integer, Position> idPositionDVLPMap = new HashMap<Integer, Position>();

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(DVLP_FILENAME));
        while ((s = in.readLine()) != null) {
            String[] arg = s.split(";");
            Position temp_Pos = new Position();
            temp_Pos.setFileName(Integer.parseInt(arg[0].trim().substring(0, 5)));
            temp_Pos.setLatitude(Double.parseDouble(arg[1].trim()));
            temp_Pos.setLongitude(Double.parseDouble(arg[2].trim()));
            idPositionDVLPMap.put(temp_Pos.getFileName(), temp_Pos);
        }
        in.close();

        return idPositionDVLPMap;
    }

    private static Integer readFileNameId(String name) {
		String fileName = name.replace("_list.txt", "");
		return Integer.parseInt(fileName);
	}

	private static Integer processString(String id) {
		String imageP1 = id.replace("FlickrVideosTrain/p1_", "");
		String imageP2 = id.replace("FlickrVideosTrain/p2_", "");
		return (imageP1.length() < imageP2.length()) ? Integer.parseInt(imageP1) : Integer.parseInt(imageP2);
	}

	private static void determineMediaAnswer(MediaSample mediaSample) {
		// Simple KNN with N = 1.
		mediaSample.setAnswerMedia(mediaSample.getSimilarityList().get(0));
	}

	private static Position findPosition(Map<Integer, Position> idPositionMap, int name) {
		return idPositionMap.get(name);
	}
}
