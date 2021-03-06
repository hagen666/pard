package cn.edu.ruc.iir.pard.sql.tree;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

/**
 * pard
 *
 * @author guodong
 */
public class AliasedRelation
        extends Relation
{
    private static final long serialVersionUID = 168169268920259769L;
    private final Relation relation;
    private final Identifier alias;
    private final List<Identifier> columnNames;

    public AliasedRelation(Relation relation, Identifier alias, List<Identifier> columnNames)
    {
        this(null, relation, alias, columnNames);
    }

    public AliasedRelation(Location location, Relation relation, Identifier alias, List<Identifier> columnNames)
    {
        super(location);
        requireNonNull(relation, "relation is null");
        requireNonNull(alias, " is null");

        this.relation = relation;
        this.alias = alias;
        this.columnNames = columnNames;
    }

    public Relation getRelation()
    {
        return relation;
    }

    public Identifier getAlias()
    {
        return alias;
    }

    public List<Identifier> getColumnNames()
    {
        return columnNames;
    }

    @Override
    public <R, C> R accept(AstVisitor<R, C> visitor, C context)
    {
        return visitor.visitAliasedRelation(this, context);
    }

    @Override
    public List<Node> getChildren()
    {
        return ImmutableList.of(relation);
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("relation", relation)
                .add("alias", alias)
                .add("columnNames", columnNames)
                .omitNullValues()
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AliasedRelation that = (AliasedRelation) o;
        return Objects.equals(relation, that.relation) &&
                Objects.equals(alias, that.alias) &&
                Objects.equals(columnNames, that.columnNames);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(relation, alias, columnNames);
    }
}
