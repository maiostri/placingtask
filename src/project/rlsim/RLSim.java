package project.rlsim;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import project.FileUtils;

public class RLSim {

	/**
	 * @param sampleDirirectory --> directory of the sample ranked files
	 * @param targetDir --> directory for the new ranked list
	 * @param ks --> number of neighbors considered when algorithm starts
	 * @param rounds --> number of iterations
	 * @param sampleByRankedList --> how many positions for each ranked list
	 */
    public void RlSim(File sampleDirectory, File targetDir, int ks, int rounds, int samplesByRankedList) throws IOException {
		//create temp directory for create and calculate the new ranked list
		FileUtils.deleteDirectory(targetDir);
		boolean isCreated = targetDir.mkdirs();
        if (!isCreated) {
            throw new IOException("Can't create temp directory");
        }

        int t = 0;
        int k = ks;
		while (t < rounds){
			// only in the first time load the original matrix, after that always work with the new rankedList rebuilding after each iteraction
			File[] files = null;
			if(t==0){
				files = sampleDirectory.listFiles();
			}else{
				files = targetDir.listFiles();
			}

            int sampleNumber = files.length;
            for (int i = 0; i < sampleNumber; i++) {
                System.out.println("Running to sample " + (i + 1) + " of " + sampleNumber);

			    File currentFile = files[i];
                Sample sample = SampleIO.loadSample(currentFile);

                int rankedListSize = sample.getRankedList().size();

                List<RankedListElement> newRankedList = new ArrayList<>(rankedListSize);

                for (int j = 0; j < rankedListSize; j++) {
                    RankedListElement rankedListElement = sample.getRankedList().get(j);
                    String devSampleIdentifier = rankedListElement.devSampleId;
                    if (j < samplesByRankedList) {
					    //A(t+1)[i,j] <- d(ti,tj,k)

					    Sample devSample = SampleIO.loadSample(sampleDirectory, devSampleIdentifier);

						BigDecimal distance = intersectionMeasure(sample, devSample, k);

						//TODO permitir a execucacao disto antes do loop. eecutar ou nao a depender de um parametro booleano do algoritmo
						//BigDecimal distance = mutualNeighborhs(rankList, aux);

						newRankedList.add(new RankedListElement(devSampleIdentifier, distance));
					}else{
						//A(t+1)[i,j] <- 1 + A(t)[i,j]
						newRankedList.add(new RankedListElement(devSampleIdentifier, rankedListElement.similarityValue.add(BigDecimal.ONE)));
					}
				}

				sortRankedListElementsDecreasingByDistance(newRankedList);

				File auxFile = new File(targetDir, currentFile.getName());
				SampleIO.writeSample(auxFile, newRankedList);
			}

			//Perform the ranking
            k = k + 1;
            t = t + 1;
		}
	}

    private void sortRankedListElementsDecreasingByDistance(List<RankedListElement> rankedList) {
		Collections.sort(rankedList, new Comparator<RankedListElement>() {
            public int compare(RankedListElement o1, RankedListElement o2) {
				return o2.similarityValue.compareTo(o1.similarityValue);
			}
		});
	}

    private BigDecimal mutualNeighborhs(Sample r1, Sample r2) {
		List<RankedListElement> rankedList1 = r1.getRankedList();
		List<RankedListElement> rankedList2 = r2.getRankedList();

        String name1 = rankedList1.get(0).devSampleId;
        String name2 = rankedList2.get(0).devSampleId;

        int post1 = r1.getIndexOfDevSampleId(name2);
        if (post1 == -1) {
            post1 = rankedList1.size() + 1;
        }

        int post2 = r2.getIndexOfDevSampleId(name1);
        if (post2 == -1) {
            post2 = rankedList2.size() + 1;
        }

        int distance = post1 + post2;

        return new BigDecimal(distance);
	}

	//k is with how many samples of the ranking lists will be considered
	private BigDecimal intersectionMeasure(Sample sample1, Sample sample2, int k){
		List<RankedListElement> rankedList1 = sample1.getRankedList();
		List<RankedListElement> rankedList2 = sample2.getRankedList();

        if(k > rankedList1.size()){
			k = rankedList1.size();
		}

        int intersectionCount = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                if(rankedList1.get(i).devSampleId.equals(rankedList2.get(j).devSampleId)){
					intersectionCount++;
				}
			}
		}

		BigDecimal v = new BigDecimal(intersectionCount).divide(new BigDecimal(k));
		BigDecimal distance = BigDecimal.ONE.divide(BigDecimal.ONE.add(v),6,RoundingMode.HALF_UP);
		return distance;
	}

	public static void main(String[] args) throws Exception {
        File sampleDirectory = new File("C:/MO633_projeto/data/visual/MediaEval2012_JurandyLists/FlickrVideosTrain");
        File targetDirectory = new File("C:/MO633_projeto/rl_sim_results");
        int ks = 5;
        int rounds = 1;
        int imagesByRankedList = 5;
        new RLSim().RlSim(sampleDirectory, targetDirectory, ks, rounds, imagesByRankedList);
    }
}
