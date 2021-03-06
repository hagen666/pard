package cn.edu.ruc.iir.pard.sql.tree;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.List;

/**
 * pard
 *
 * @author guodong
 */
public final class Use
        extends Statement
{
    private static final long serialVersionUID = -5787911487055102574L;
    private final Identifier schema;

    public Use(Identifier schema)
    {
        this(null, schema);
    }

    public Use(Location location, Identifier schema)
    {
        super(location);
        this.schema = schema;
    }

    public Identifier getSchema()
    {
        return schema;
    }

    @Override
    public <R, C> R accept(AstVisitor<R, C> visitor, C context)
    {
        return visitor.visitUse(this, context);
    }

    @Override
    public List<? extends Node> getChildren()
    {
        return null;
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        return false;
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("schema", schema.toString())
                .omitNullValues()
                .toString();
    }
}
