package project.rlsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import project.FileUtils;

public class RLSim {

    /**
     * @param devSampleDir
     * @param testDir --> directory of the test ranked files
     * @param targetDir --> directory for the new ranked list
     * @param ks --> number of neighbors considered when algorithm starts
     * @param rounds --> number of iterations
     * @param imagesByRankedList --> how many positions for each ranked list
     */
    public void RlSim(File devSampleDir, File testDir, File targetDir, int ks, int rounds, int imagesByRankedList) throws NumberFormatException, IOException {
        int t = 0;
        int k = ks;

        System.out.println("Re-creating results folder...");
        FileUtils.deleteDirectory(targetDir);
        boolean isCreated = targetDir.mkdirs();
        if (!isCreated) {
            throw new IOException("Can't create temp directory");
        }

        boolean calculateDistance = Boolean.TRUE;
        while (t < rounds) {

            // only in the first time load the original matrix, after that always work with the new rankedList rebuilding after each iteraction
            File[] files = null;
            if (t == 0) {
                files = testDir.listFiles();
            } else {
                calculateDistance = Boolean.FALSE;
                files = targetDir.listFiles();
            }

            int testSampleCount = files.length;
            for (int i = 0; i < testSampleCount; i++) {
                File testSampleFile = files[i];
                int c = 0;

                System.out.println("executing from sample " + (i + 1) + " of " + testSampleCount);

                RankedList rankList = readRankedList(testSampleFile, calculateDistance);

                for (int j = 0; j < rankList.getElements().size(); j++) {
                    RankedListElement rankedListElement = rankList.getElements().get(j);

                    File file = new File(devSampleDir, rankedListElement.getId() + "_list.txt");

                    if (c < imagesByRankedList) {
                        // A(t+1)[i,j] <- d(ti,tj,k)

                        RankedList aux = readRankedList(file, calculateDistance);

                        // BigDecimal distance = intersectionMeasure(rankList, aux, k);

                        // TODO permitir a execucacao disto antes do loop. eecutar ou nao a depender de um parametro booleano do algoritmo
                        BigDecimal distance = mutualNeighborhs(rankList, aux);

                        rankedListElement.setNewDistance(distance);
                    } else {
                        // A(t+1)[i,j] <- 1 + A(t)[i,j]
                        rankedListElement.setNewDistance(rankedListElement.getDistance().add(BigDecimal.ONE));
                    }
                    c = c + 1;
                }

                orderByDistance(rankList);

                File auxFile = new File(targetDir, testSampleFile.getName());
                rankList.writeToFile(auxFile);
            }

            // Perform the ranking
            k = k + 1;
            t = t + 1;
        }
    }

    private RankedList orderByDistance(RankedList r1) {
        // ordena os itens do ranked list, decrescente, por valor de similaridade
        Collections.sort(r1.getElements(), new Comparator<RankedListElement>() {
            public int compare(RankedListElement o1, RankedListElement o2) {
                return o1.getNewDistance().compareTo(o2.getNewDistance());
            }
        });

        return r1;
    }

    private BigDecimal mutualNeighborhs(RankedList r1, RankedList r2) {
        int post1 = r1.getPosition(r2.getElements().get(0).getId());
        if (post1 == -1) {
            post1 = r1.getElements().size() + 1;
        }

        int post2 = r2.getPosition(r1.getElements().get(0).getId());
        if (post2 == -1) {
            post2 = r2.getElements().size() + 1;
        }

        int distance = post1 + post2;

        return new BigDecimal(distance);
    }

    // k is with how many images of the ranking list will work
    private BigDecimal intersectionMeasure(RankedList r1, RankedList r2, int k) {
        if (k > r1.getElements().size()) {
            k = r1.getElements().size();
        }

        int cont = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                if (r1.getElements().get(i).equals(r2.getElements().get(j))) {
                    cont = cont + 1;
                }
            }
        }

        BigDecimal v = new BigDecimal(cont).divide(new BigDecimal(k));
        BigDecimal distance = BigDecimal.ONE.divide(BigDecimal.ONE.add(v), 6, RoundingMode.HALF_UP);
        return distance;
    }

    private RankedList readRankedList(File file, boolean calculateDistance) throws NumberFormatException, IOException {
        String fileId = processFileName(file.getName());
        RankedList rankedList = new RankedList(fileId);

        BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
        String s = null;
        int cont = 0;
        while ((s = in.readLine()) != null) {
            cont = cont + 1;
            String[] s_args = s.trim().split("\t");

            String id = s_args[1].trim().replace("FlickrVideosTrain/", "");

            double dist = Double.parseDouble(s_args[0].trim());
            if (calculateDistance) {
                dist = 1 - dist;
            }

            rankedList.setPosition(id, cont);

            BigDecimal distance = new BigDecimal(dist).setScale(6, RoundingMode.HALF_UP);

            RankedListElement rle = new RankedListElement(id, distance);
            rle.setNewDistance(distance);

            rankedList.getElements().add(rle);
        }
        in.close();

        return rankedList;
    }

    private static String processFileName(String name) {
        return name.replace("_list.txt", "");
    }

    public static void main(String[] args) throws Exception {
        File devSampleDir = new File("C:/MO633_projeto/data/visual/MediaEval2012_JurandyLists/FlickrVideosTrain");
        File testDir = new File("C:/MO633_projeto/data/visual/MediaEval2012_JurandyLists/VideosPlacingTask");
        File targetDir = new File("C:/MO633_projeto/RLSim_results");
        int ks = 5;
        int rounds = 3;
        int imagesByRankedList = 5;

        new RLSim().RlSim(devSampleDir, testDir, targetDir, ks, rounds, imagesByRankedList);
    }
}
