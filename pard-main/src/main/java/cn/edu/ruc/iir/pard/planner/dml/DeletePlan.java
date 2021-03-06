package cn.edu.ruc.iir.pard.planner.dml;

import cn.edu.ruc.iir.pard.catalog.Column;
import cn.edu.ruc.iir.pard.catalog.Fragment;
import cn.edu.ruc.iir.pard.catalog.Schema;
import cn.edu.ruc.iir.pard.catalog.Table;
import cn.edu.ruc.iir.pard.commons.exception.ErrorMessage;
import cn.edu.ruc.iir.pard.etcd.dao.SchemaDao;
import cn.edu.ruc.iir.pard.etcd.dao.TableDao;
import cn.edu.ruc.iir.pard.planner.Plan;
import cn.edu.ruc.iir.pard.planner.ddl.UsePlan;
import cn.edu.ruc.iir.pard.sql.expr.ColumnItem;
import cn.edu.ruc.iir.pard.sql.expr.Expr;
import cn.edu.ruc.iir.pard.sql.expr.Expr.LogicOperator;
import cn.edu.ruc.iir.pard.sql.expr.FalseExpr;
import cn.edu.ruc.iir.pard.sql.tree.ComparisonExpression;
import cn.edu.ruc.iir.pard.sql.tree.Delete;
import cn.edu.ruc.iir.pard.sql.tree.DereferenceExpression;
import cn.edu.ruc.iir.pard.sql.tree.Expression;
import cn.edu.ruc.iir.pard.sql.tree.Identifier;
import cn.edu.ruc.iir.pard.sql.tree.IsNotNullPredicate;
import cn.edu.ruc.iir.pard.sql.tree.IsNullPredicate;
import cn.edu.ruc.iir.pard.sql.tree.Literal;
import cn.edu.ruc.iir.pard.sql.tree.Statement;

import java.util.HashMap;
import java.util.Map;
/**
 * pard
 *
 * @author guodong
 */
public class DeletePlan
        extends Plan
{
    private Map<String, Expr> distributionHints;
    private String schemaName;
    private String tableName;

    public DeletePlan(Statement stmt)
    {
        super(stmt);
    }

    public Map<String, Expr> getDistributionHints()
    {
        return this.distributionHints;
    }

    @Override
    public ErrorMessage semanticAnalysis()
    {
        Statement statement = this.getStatment();
        distributionHints = new HashMap<>();
        if (!(statement instanceof Delete)) {
            return ErrorMessage.throwMessage(ErrorMessage.ErrCode.ParseError, "delete statement.");
        }
        Schema schema = null;
        Table table = null;

        Delete delete = (Delete) statement;

        //check the table name
        tableName = delete.getName().getSuffix();
        if (delete.getName().getPrefix().isPresent()) {
            schemaName = delete.getName().getPrefix().get().toString();
        }
        boolean checkSchema = false;
        if (schemaName == null) {
            schema = UsePlan.getCurrentSchema();
            if (schema != null) {
                schemaName = schema.getName();
                checkSchema = true;
            }
        }
        if (schemaName == null) {
            return ErrorMessage.throwMessage(ErrorMessage.ErrCode.SchemaNotSpecified);
        }
        if (!checkSchema) {
            SchemaDao schemaDao = new SchemaDao();
            schema = schemaDao.loadByName(schemaName);
            if (schema == null) {
                return ErrorMessage.throwMessage(ErrorMessage.ErrCode.SchemaNotExsits, schemaName);
            }
        }
        TableDao tableDao = new TableDao(schema);
        table = tableDao.loadByName(tableName);
        if (table == null) {
            return ErrorMessage.throwMessage(ErrorMessage.ErrCode.TableNotExists, schemaName + "." + tableName);
        }
        Expression expression = delete.getExpression();
        ErrorMessage checkExpressionResult = checkExpression(table, expression);
        if (checkExpressionResult.getErrcode() < 0) {
            return checkExpressionResult;
        }

        Map<String, String> col2tbl = ColumnItem.getCol2TblMap();
        Table catalogTable = tableDao.loadByName(tableName);
        if (catalogTable == null) {
            return ErrorMessage.throwMessage(ErrorMessage.ErrCode.TableNotExists, schemaName + "." + tableName);
        }
        for (Column col : catalogTable.getColumns().values()) {
            col2tbl.put(col.getColumnName(), tableName);
        }
        Expr deleteExpr = Expr.parse(expression);
        for (String key : table.getFragment().keySet()) {
            Fragment f = table.getFragment().get(key);
            Expr fragExpr = Expr.parse(f.getCondition(), tableName);
            Expr composeExpr = Expr.and(fragExpr, deleteExpr, LogicOperator.AND);
            if (!(composeExpr instanceof FalseExpr)) {
                distributionHints.put(f.getSiteName(), composeExpr);
            }
        }
        return ErrorMessage.getOKMessage();
    }

    public String getSchemaName()
    {
        return schemaName;
    }

    public String getTableName()
    {
        return tableName;
    }

    private ErrorMessage checkExpression(Table table, Expression expression)
    {
        ErrorMessage result = ErrorMessage.getOKMessage();
        if (expression instanceof ComparisonExpression) {
            result = checkExpression(table, ((ComparisonExpression) expression).getLeft());
            if (result.getErrcode() >= 0) {
                result = checkExpression(table, ((ComparisonExpression) expression).getRight());
            }
        }
        else if (expression instanceof IsNullPredicate) {
            result = checkExpression(table, ((IsNullPredicate) expression).getValue());
        }
        else if (expression instanceof IsNotNullPredicate) {
            result = checkExpression(table, ((IsNotNullPredicate) expression).getValue());
        }
        else if (expression instanceof IsNotNullPredicate) {
            result = checkExpression(table, ((IsNotNullPredicate) expression).getValue());
        }
        else if (expression instanceof Identifier) {
            if (!table.getColumns().keySet().contains(((Identifier) expression).getValue())) {
                return ErrorMessage.throwMessage(ErrorMessage.ErrCode.ColumnInTableNotFound);
            }
        }
        else if (expression instanceof DereferenceExpression) {
            Expression base = ((DereferenceExpression) expression).getBase();
            if (!(base instanceof Identifier)) {
                return ErrorMessage.throwMessage(ErrorMessage.ErrCode.UnSupportedQuery, "unsupported expression type!");
            }
            result = checkExpression(table, ((DereferenceExpression) expression).getField());
        }
        else if (expression instanceof Literal) {
            // skip
        }
        else {
            return ErrorMessage.throwMessage(ErrorMessage.ErrCode.UnSupportedQuery, "unsupported expression type!");
        }
        return result;
    }
}
