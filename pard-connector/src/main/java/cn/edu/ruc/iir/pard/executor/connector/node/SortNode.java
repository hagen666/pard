package cn.edu.ruc.iir.pard.executor.connector.node;

import cn.edu.ruc.iir.pard.catalog.Column;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * pard
 *
 * @author guodong
 */
public class SortNode
        extends PlanNode
{
    private static final long serialVersionUID = -4043747552892823485L;
    private final List<Column> columns;
    private final List<Integer> orderings;
    public List<Column> getColumns()
    {
        return columns;
    }

    public List<Integer> getOrderings()
    {
        return orderings;
    }

    public SortNode()
    {
        this.name = "SORT";
        this.columns = new ArrayList<>();
        this.orderings = new ArrayList<>();
    }
    public SortNode(SortNode sort)
    {
        super(sort);
        this.name = "SORT";
        this.columns = new ArrayList<>();
        this.orderings = new ArrayList<>();
        this.columns.addAll(sort.columns);
        this.orderings.addAll(sort.orderings);
    }
    public void addSort(Column column, boolean order)
    {
        if (!columns.contains(column)) {
            this.columns.add(column);
            this.orderings.add(order ? 1 : 0);
        }
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("name", "SORT")
                .add("columns", columns)
                .add("orderings", orderings)
                .add("child", getLeftChild())
                .toString();
    }
}
