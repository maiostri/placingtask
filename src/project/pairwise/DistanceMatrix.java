package project.pairwise;

/**
 *
 * @author Jonas Henrique
 */
public class DistanceMatrix {

    double matrix[][];

    public DistanceMatrix(int nLines, int nColumns)
    {
        matrix = new double[nLines][nColumns];
    }

    public void setValue(int line, int column, double value)
    {
        matrix[line][column] = value;
    }

    public double getValue(int line, int column)
    {
        return matrix[line][column];
    }

    public double[][] getMatrix()
    {
        return matrix;
    }
}
