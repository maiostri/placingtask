package project.pairwise;

import java.util.LinkedList;

/**
 *
 * @author Jonas Henrique
 */
class RankedList
{
    private String name;
    private LinkedList<RankElement> elements;

    public RankedList(String name)
    {
        this.name = name;
        elements = new LinkedList<>();
    }

    public void addElement(RankElement r)
    {
        elements.add(r);
    }

    public RankElement getRankElement(int position)
    {
        return elements.get(position);
    }

    public LinkedList<RankElement> getRanking()
    {
        return elements;
    }

    public String getName()
    {
        return name;
    }
}
