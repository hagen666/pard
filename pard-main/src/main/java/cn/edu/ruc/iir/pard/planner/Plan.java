package cn.edu.ruc.iir.pard.planner;

import cn.edu.ruc.iir.pard.sql.tree.Statement;

/**
 * pard
 *
 * @author guodong
 */
public abstract class Plan
{
    private Statement statement;
    private String jobId;

    public Plan(Statement stmt)
    {
        statement = stmt;
    }

    public Statement getStatment()
    {
        return statement;
    }

    public void setStatment(Statement statment)
    {
        this.statement = statment;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }

    public abstract ErrorMessage semanticAnalysis();

    public boolean afterExecution(boolean executeSuccess)
    {
        return true;
    }
}
