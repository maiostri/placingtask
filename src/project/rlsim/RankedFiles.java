package project.rlsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankedFiles {

	private BufferedReader in;


	/*pathDir --> Base directory for create a temp folder for build the new ranked list
	 * testDir --> directory of the test ranked files
	 * targetDir --> directory for the new ranked list
	 * ks --> number of neighbors considered when algorithm starts
	 * rounds --> number of iterations
	 * imagesByRankedList --> how many positions for each ranked list
	 */
	public void RlSim(String pathDir,String testDir, String targetDir, int ks, int rounds, int imagesByRankedList) throws NumberFormatException, IOException{

		int t =0;
		int k =ks;

		//create temp directory for create and calculate the new ranked list

		String tempResults = pathDir + "/tempRankedList";

		File f = new File(tempResults);

		Methods.deleteDirectory(f);

		boolean isCreated = f.mkdir();

		if(!isCreated){
			System.out.println("Can't create temp directory");
			return;
		}


		while (t < rounds){
			//for (File file : files) {

			// only in the first time load the original matrix, after that always work with the new rankedList rebuilding after each iteraction
			File[] files = null;

			if(t==0){
				files = new File(testDir).listFiles();
			}else{
				files = new File(tempResults).listFiles();
			}


			for (int i =0; i< files.length;i++) {
				int c = 0;

				RankedList rankList = createRankedList(files[i]);

				for(int j =0; j< rankList.getRanking().size();j++){



					String path = rankList.getRanking().get(j)+"_list.txt";

					File file = new File (pathDir+"/"+ path);

					RankedList aux = createRankedList(file);

					if(c < imagesByRankedList){

						//A(t+1)[i,j] <- d(ti,tj,k)


						BigDecimal distance = intersectionMeasure(rankList, aux, k);

						//TODO permitir a execucacao disto antes do loop. eecutar ou nao a depender de um parametro booleano do algoritmo
						//BigDecimal distance = mutualNeighborhs(rankList, aux);

						rankList.getNewDistances().set(j, distance);
					}else{
						//A(t+1)[i,j] <- 1 + A(t)[i,j]
						rankList.getNewDistances().set(j, rankList.getCurrentDistances().get(j).add(BigDecimal.ONE));
					}
					c = c+1;
				}

				String dirTmp = tempResults + "/" + files[i].getName();

				File auxFile = new File(dirTmp);

				orderByDistance(rankList);

				Methods.writeFile(auxFile, rankList);
			}

			//Perform the ranking
			k=k+1;
			t = t+1;
		}

	}


	private RankedList orderByDistance(RankedList r1){
		//ordena os itens do ranked list, decrescente, por valor de similaridade
		/*Collections.sort(r1.elements, new Comparator<RankedListElement>() {
			public int compare(RankedListElement o1, RankedListElement o2) {
				return o2.similarityValue - o1.similarityValue;
			}
		});*/

		List<String> videos = new ArrayList<String>();

		List<BigDecimal> ordered = new ArrayList<BigDecimal>();

		for(int i=0; i < r1.getNewDistances().size();i++){
			ordered.add(r1.getNewDistances().get(i));
		}


		Collections.sort(ordered);

		for(int i=0; i < ordered.size();i++){
			for(int j=0; j < r1.getNewDistances().size();j++){
				if(ordered.get(i).equals(r1.getNewDistances().get(j))){
					if(!videos.contains(r1.getRanking().get(j))){
						videos.add(r1.getRanking().get(j));
						break;
					}
				}
			}
		}

		r1.setNewDistances(ordered);

		r1.setRanking(videos);

		return r1;
	}


	//k is with how many images of the ranking list will work

	//k is with how many images of the ranking list will work

	private BigDecimal mutualNeighborhs(RankedList r1, RankedList r2){

		String name1 = r1.getRanking().get(0);

		String name2 = r2.getRanking().get(0);

		int post1 = r1.getRanking().indexOf(name2);

		if(post1 == -1){
			post1 = r1.getRanking().size()+1;
		}

		int post2 = r2.getRanking().indexOf(name1);

		if(post2 == -1){
			post2 = r2.getRanking().size()+1;
		}

		int distance = post1+post2;

		return new BigDecimal(distance);
	}




	//k is with how many images of the ranking list will work
	private BigDecimal intersectionMeasure(RankedList r1, RankedList r2, int k){

		if(k > r1.getRanking().size()){
			k = r1.getRanking().size();
		}

		int cont =0;
		for(int i=0; i< k;i++){

			for(int j=0; j< k;j++){
				if(r1.getRanking().get(i).equals(r2.getRanking().get(j))){
					cont = cont + 1;
				}
			}
		}

		BigDecimal v = new BigDecimal(cont).divide(new BigDecimal(k));
		BigDecimal distance = BigDecimal.ONE.divide(BigDecimal.ONE.add(v),6,RoundingMode.HALF_UP);
		return distance;
	}

//	private static String processString(String id) {
//		String imageP1 = id.replace("FlickrVideosTrain/p1_", "");
//		String imageP2 = id.replace("FlickrVideosTrain/p2_", "");
//		return (imageP1.length() < imageP2.length()) ? imageP1 : imageP2;
//
//	}


	private RankedList  createRankedList(File file) throws NumberFormatException, IOException{
		RankedList rankedList = new RankedList();
		String fileId = processFileName(file.getName());
		rankedList.setVideoName(fileId);

		try {
			in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
			String s = null;
			while ((s = in.readLine()) != null) {
				String[] s_args = s.trim().split("\t");

				String id = s_args[1].trim();

				Double dist = 1- Double.parseDouble(s_args[0].trim());

				rankedList.getRanking().add(id);

				BigDecimal bd = new BigDecimal(dist).setScale(6, RoundingMode.HALF_UP);

				rankedList.getCurrentDistances().add(bd);
				rankedList.getNewDistances().add(bd);
			}




		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR IN CREATED RANKED LIST " + e);
		}

		return rankedList;
	}

	private static String processFileName(String name) {
		String fileName = name.replace("_list.txt", "");
		return fileName;
	}



}
