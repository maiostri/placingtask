package project.pairwise;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Jonas Henrique
 */
class PairwiseAlgorithm
{
    //Caminhos das pastas de arquivos de treinamento e teste
    private File[] arqRanksTeste;
    private File[] arqRankTreino;

    //Dados de treinamento
    private LinkedList<TrainingData> dados;

    //Lista de rankings que compoem algoritmo
    private LinkedList<RankedList> ranks;

    private DistanceMatrix matriz;
    private DistanceMatrix At;

    private int t = 0;

    private double currentCohesion, lastCohesion;

    private LinkedList<RankedList> Rt, Ri, Rc;
    private LinkedList<Float> cohesion = new LinkedList<>();

    public PairwiseAlgorithm()
    {
        ranks = new LinkedList<>();
        //Leitura dos dados de treinamento
        try
        {
            leituraDadosTreinamento();

            arqRankTreino = arquivosEntrada();
            arqRanksTeste = arquivosEntrada();

            Scanner entrada;
            //Para cada caso de teste
            for(int i=0; i<arqRanksTeste.length; i++)
            {
                //Limpa ranks do teste anterior
                ranks.clear();

                File arq = arqRanksTeste[i];
                entrada = new Scanner(arq);

                String nomeFile = arqRanksTeste[i].getName().substring(0, 10);

                RankedList rank = new RankedList(nomeFile);
                while(entrada.hasNext())
                {
                    double valor = new Double(entrada.next());
                    String nome = entrada.next();

                    String nomeFim = ""+nome.subSequence(18, 26);

                    rank.addElement(new RankElement((float)valor, nomeFim));
                }

                ranks.add(rank);

                //Pega os n primeiros arquivos do ranking de teste para montar matrizes
                RankedList r;

                for(int j=0; j<Configs.NEIGHBOURSIZE; j++)
                {
                    for(int k = 0; k<rank.getRanking().size(); k++)
                    {
                        arq = arqRankTreino[k];

                        String nomeArq = arq.getName().substring(0, 8);

                        if(nomeArq.equalsIgnoreCase(rank.getRanking().get(j).getTrainElementName()))
                        {
                            entrada = new Scanner(arq);
                            r = new RankedList(nomeArq);
                            while(entrada.hasNext())
                            {
                                double valor = new Double(entrada.next());
                                String nome = entrada.next();

                                String nomeFim = ""+nome.subSequence(18, 26);
                                r.addElement(new RankElement((float)valor, nomeFim));
                            }
                            ranks.add(r);
                        }

                    }
                }

                //Montar matriz de distancias
                matriz = new DistanceMatrix(Configs.NEIGHBOURSIZE+1, dados.size());

                for(int j=0; j<Configs.NEIGHBOURSIZE+1; j++)
                {
                    RankedList aux = ranks.get(j);
                    for(int k=0; k<aux.getRanking().size(); k++)
                    {
                        for(int l=0; l<dados.size(); l++)
                        {

                            System.out.println(j+" "+k+" "+l);
                            if(aux.getRanking().get(k).getTrainElementName().equalsIgnoreCase(dados.get(l).getName()))
                            {
                                matriz.setValue(j, l, (1 - aux.getRanking().get(k).getSimilarity()));
                                l = dados.size()+1;
                            }
                        }
                    }
                }

                i = arqRanksTeste.length;
                algorithmPairwise();

                //Gerar um arquivo contendo o ranking cujo nome eh igual
                try
                {
                    String fileName = "Ranking"+Rt.getFirst().getName()+".txt";
                    File solution = new File("C:\\", fileName);
                    BufferedWriter bf = new BufferedWriter(new FileWriter(solution));

                    RankedList rSolution = Rt.getFirst();
                    for(int m=0; m<rSolution.getRanking().size(); m++)
                    {
                        bf.write(rSolution.getRankElement(i).getSimilarity()+" "+
                                 rSolution.getRankElement(i).getTrainElementName());
                    }
                    bf.flush();
                    bf.close();
                }catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Fudeu");
                }
            }

        }catch(FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(null, "Erro no arquivo");
        }
    }

    private File[] arquivosEntrada() {
        String arquivoWave;
          JFileChooser arquivo = new JFileChooser();
          arquivo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

          if(arquivo.showOpenDialog(arquivo) == JFileChooser.APPROVE_OPTION){
              arquivoWave = arquivo.getSelectedFile().getPath();
          }

          else{
              arquivoWave = "";

          }

          File teste = new File(arquivoWave);
          File arquivos [] = teste.listFiles();
          return arquivos;
    }

    private void algorithmPairwise()
    {
        cohesion = new LinkedList<>();

        //Inicializacao de variaveis
        currentCohesion = 0;
        Rt = new LinkedList<>();
        Rt.addAll(ranks);
        At = matriz;

        do
        {
            cohesion.clear();
            for(int i=0; i<ranks.size(); i++)
            {
                cohesion.add(computeCohesion(i, ranks.get(i)));
            }

            Rc = sortRankedListByCohesion(Rt, cohesion);

            for(int i=0; i<Rc.size(); i++)
            {
                performRecommendations(Rc.get(i), cohesion.get(i));
            }

            // for(int i=0; i<Rc.size(); i++)
            //{
                performClusterRecommendations();//Rc.get(i));
            //}

            Rt = performReRanking();

            lastCohesion = currentCohesion;

            currentCohesion = computAvgCohesion();
        }while((currentCohesion - lastCohesion)<(currentCohesion-Configs.ECOHESION));

    }

    private float computAvgCohesion()
    {
        float co = 0;

        for(int i=0; i<cohesion.size(); i++)
            co += cohesion.get(i);

        return co / cohesion.size();
    }
    private LinkedList<RankedList> performReRanking()
    {
        RankedList r;
        LinkedList<RankedList> rf = new LinkedList<>();
        String nome;
        float valor;

        for(int i=0; i<At.getMatrix().length; i++)
        {
            r = new RankedList(Rt.get(i).getName());
            r.getRanking().clear();

            for(int j=0; j<At.getMatrix()[i].length; j++)
            {
                nome = dados.get(j).getName();
                valor = (float) (1 - At.getValue(i, j));
                r.addElement(new RankElement(valor, nome));
            }
            rf.add(r);
        }
        return rf;
    }
    private void performClusterRecommendations()
    {
        for(int i=0; i<At.getMatrix().length; i++)
        {
            for(int j=0; j<At.getMatrix()[0].length; j++)
            {
                if(At.getMatrix()[i][j] == 0)
                {
                    At.setValue(j, i, 0);
                }
            }
        }
    }
    private void performRecommendations(RankedList r, Float c)
    {
        RankedList rk = knn(r);
        int x, y;
        float wx, wy, w, lambda;

        x=1;
        while(x < rk.getRanking().size())
        {
            wx = (float) 1 - (x/Configs.K);
            y=1;

            while(y < rk.getRanking().size())
            {
                wy = (float) 1 - (y/Configs.K);

                w = c * wx * wy;

                lambda = 1 - Math.min(1, Configs.L * w);

                double A1 = At.getValue(x, y);
                double A2 = At.getValue(y, x);
                At.setValue(x, y, Math.min((lambda * A1), A2));
            }
        }
    }

    private RankedList knn(RankedList r)
    {
        RankedList rk = new RankedList(r.getName());

        for(int i=0; i<Configs.K; i++)
        {
            rk.addElement(r.getRankElement(i));
        }

        return rk;
    }
    private LinkedList<RankedList> sortRankedListByCohesion(LinkedList<RankedList> Rt, LinkedList<Float> cohesion)
    {
        LinkedList<RankedList> Rc = new LinkedList<>();
        Rc.addAll(Rt);
        RankedList aux;
        for(int i = 0; i<cohesion.size(); i++)
        {
		for(int j = 0; j<cohesion.size()-1; j++)
                {
			if(cohesion.get(j) > cohesion.get(j + 1))
                        {
				aux = Rc.get(j);
				Rc.set(j, Rc.get(j+1));
				Rc.set(j+1, aux);
			}
		}
	}
        return Rc;
    }
    private float computeCohesion(int i, RankedList Ri)
    {
        float coesao = 0;
        float w_p, s;
        float numerador = 0, denominador = 0;
        String nome;
        String nome2;
        for(int k=0; k<Rt.size(); k++)
        {
             nome = Ri.getRankElement(k).getTrainElementName();

             for(int j=0;j<Rt.size(); j++)
             {
                 if(j != i)
                 {
                     if(Rt.get(j).getName().equalsIgnoreCase(nome))
                     {
                         for(int l = 0; l<Configs.NEIGHBOURSIZE; l++)
                         {
                             nome2 = Rt.get(j).getRankElement(l).getTrainElementName();
                             w_p = w(l);
                             s = calcS(Ri, nome2);

                             numerador += (w_p*s);
                             denominador += w_p;
                         }

                     }
                 }
             }

        }

        coesao = numerador/denominador;
        return coesao;
    }

    private float calcS(RankedList Ri, String nome2)
    {
        float achou = 0;
        for(int i=0; i<Ri.getRanking().size(); i++)
        {
            if(Ri.getRankElement(i).getTrainElementName().equalsIgnoreCase(nome2)) achou = 1;
        }

        return achou;
    }

    private float w(int posicao)
    {
        return (float)1/posicao;
    }

    private void leituraDadosTreinamento() throws FileNotFoundException {

        JFileChooser jfc = new JFileChooser();

	int result = jfc.showOpenDialog(null);

	if(result == JFileChooser.CANCEL_OPTION) return;

	File arquivo = jfc.getSelectedFile();

	Scanner entrada;
        entrada = new Scanner(arquivo);

        dados = new LinkedList<>();
	while(entrada.hasNext())
        {
            entrada.next();
            dados.add(new TrainingData(entrada.next()));
        }
    }
}
