package project.rlsim;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import project.FileUtils;

public class RLSim {

    public void RlSim(File devSampleDir, File sampleDir, File targetDir, int ks, int rounds,
        int imagesByRankedList, boolean applyInitialMutualNeighbors) throws NumberFormatException, IOException
    {
        System.out.println("Re-creating results folder...");
        FileUtils.deleteDirectory(targetDir);
        boolean isCreated = targetDir.mkdirs();
        if (!isCreated) {
            throw new IOException("Can't create temp directory");
        }

        if (applyInitialMutualNeighbors) {
            //execute mutual neighbors, which changes similarities to a certain kind of distance
            executeRLSim(devSampleDir, sampleDir, targetDir, ks, 1, imagesByRankedList, true, false);

            //execute intersectionMeasure considering input measures as distances (do not convert the values)
            executeRLSim(devSampleDir, sampleDir, targetDir, ks, rounds, imagesByRankedList, false, false);
        } else {
            //execute intersectionMeasure considering input measures as similarities
            executeRLSim(devSampleDir, sampleDir, targetDir, ks, rounds, imagesByRankedList, false, true);
        }
    }

    private void executeRLSim(File devSampleDir, File sampleDir, File targetDir, int ks, int rounds,
        int imagesByRankedList, boolean useMutualNeighborDistanceFunction, boolean readFirstRoundAsSimilarity) throws IOException
    {
        int k = ks;

        RankedListIO rankedListIO = new RankedListIO();
        
        Map <String, RankedList> auxRankedListCache = new HashMap<String, RankedList>();

        for (int t = 0; t < rounds; t++) {
            // only in the first time load the original matrix, after that always work with the new rankedList rebuilding after each iteraction
            File[] files = null;
            if (t == 0) {
                files = sampleDir.listFiles();
            } else {
                files = targetDir.listFiles();
            }

            //so inverter medidas na primeiro round de execucao, para intersectionMeasure
            boolean invertMeasures = readFirstRoundAsSimilarity && t == 0;

            int testSampleCount = files.length;
            for (int i = 0; i < testSampleCount; i++) {
                File testSampleFile = files[i];
                int c = 0;

                System.out.println("executing from sample " + (i + 1) + " of " + testSampleCount);

                RankedList rankList = rankedListIO.readRankedList(testSampleFile, invertMeasures, 100);

                int elementCount = rankList.getCount();
                for (int j = 0; j < elementCount; j++) {
                    RankedListElement rankedListElement = rankList.getElement(j);

                    if (c < imagesByRankedList || useMutualNeighborDistanceFunction) {
                        // A(t+1)[i,j] <- d(ti,tj,k)

                	RankedList aux;

                        BigDecimal distance;
                        if (useMutualNeighborDistanceFunction) {
                            aux = rankedListIO.readRankedList(devSampleDir, rankedListElement.getId(), invertMeasures);
                            distance = mutualNeighborhs(rankList, aux);
                        } else {
                            RankedList rankedListAlreadyReaded = auxRankedListCache.get(rankedListElement.getId());
                            if  (rankedListAlreadyReaded == null) {
                        	rankedListAlreadyReaded = rankedListIO.readRankedList(devSampleDir, rankedListElement.getId(), invertMeasures, k);
                        	auxRankedListCache.put(rankedListElement.getId(), rankedListAlreadyReaded);
                            }
                            distance = intersectionMeasure(rankList, rankedListAlreadyReaded, k);
                        }

                        rankedListElement.setNewDistance(distance);
                    } else {
                        // A(t+1)[i,j] <- 1 + A(t)[i,j]
                        rankedListElement.setNewDistance(rankedListElement.getDistance().add(BigDecimal.ONE));
                    }
                    c = c + 1;
                }

                rankList.orderByDistance();

                File auxFile = new File(targetDir, testSampleFile.getName());                
                rankedListIO.writeRankedListToFile(rankList, auxFile);
            }

            // Perform the ranking
            k = k + 1;
        }
    }

    private BigDecimal mutualNeighborhs(RankedList r1, RankedList r2) {
        int post1 = r1.getPosition(r2.getElement(0).getId());
        if (post1 == -1) {
            post1 = r1.getCount() + 1;
        }

        int post2 = r2.getPosition(r1.getElement(0).getId());
        if (post2 == -1) {
            post2 = r2.getCount() + 1;
        }

        int distance = post1 + post2;

        return new BigDecimal(distance);
    }

    // k is with how many images of the ranking list will work
    private BigDecimal intersectionMeasure(RankedList r1, RankedList r2, int k) {
        if (k > r1.getCount()) {
            k = r1.getCount();
        }
        
        int cont = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                if (r1.getElement(i).equals(r2.getElement(j))) {
		    cont = cont + 1;
                    break;
                }
            }
        }

        BigDecimal v = new BigDecimal(cont).divide(new BigDecimal(k));
        BigDecimal distance = BigDecimal.ONE.divide(BigDecimal.ONE.add(v), 6, RoundingMode.HALF_UP);
        return distance;
    }

    public static void main(String[] args) throws Exception {
        File devSampleDir = new File("/run/media/ricardo/Expansion Drive/Unicamp/Banco_de_Dados_II/JurandyLists/FlickrVideosTrain");
        File sampleDir = new File("/run/media/ricardo/Expansion Drive/Unicamp/Banco_de_Dados_II/JurandyLists/FlickrVideosTrain");
        File targetDir = new File("/run/media/ricardo/Expansion Drive/RLSim_results");
        int ks = 20;
        int rounds = 3;
        int imagesByRankedList = 100;
        boolean applyInitialMutualNeighbors = false;

        new RLSim().RlSim(devSampleDir, sampleDir, targetDir, ks, rounds, imagesByRankedList, applyInitialMutualNeighbors);
    }
}
