package cn.edu.ruc.iir.pard.commons.utils;

import cn.edu.ruc.iir.pard.commons.memory.Block;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * pard
 *
 * @author guodong
 */
public class PardResultSet
        implements Serializable
{
    private static final long serialVersionUID = 8184501795566412803L;

    public enum ResultStatus
    {
        OK("OK"),
        BEGIN_ERR("Create job error"),
        PARSING_ERR("Parse error"),
        PLANNING_ERR("Plan error"),
        SCHEDULING_ERR("Schedule error"),
        EXECUTING_ERR("Execution error"),
        EOR("End of result set");

        private String msg;

        ResultStatus(String msg)
        {
            this.msg = msg;
        }

        @Override
        public String toString()
        {
            return this.msg;
        }
    }

    private List<Block> blocks;
    private ResultStatus resultStatus;
    private String taskId;
    private int resultSeqId;
    private boolean resultHasNext;
    private int resultSetNum = 0;

    public PardResultSet()
    {
        this(ResultStatus.OK);
    }

    public PardResultSet(ResultStatus resultStatus)
    {
        blocks = new ArrayList<>();
        this.resultStatus = resultStatus;
    }

    public void addResultSet(PardResultSet resultSet)
    {
        if (resultSet.getStatus() != ResultStatus.OK) {
            this.resultStatus = resultSet.resultStatus;
        }
        else {
            while (resultSet.hasNext()) {
                blocks.add(resultSet.getNext());
            }
        }
        this.resultSetNum++;
    }

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public int getResultSeqId()
    {
        return resultSeqId;
    }

    public void setResultSeqId(int resultSeqId)
    {
        this.resultSeqId = resultSeqId;
    }

    public boolean isResultHasNext()
    {
        return resultHasNext;
    }

    public void setResultHasNext(boolean resultHasNext)
    {
        this.resultHasNext = resultHasNext;
    }

    public void addBlock(Block block)
    {
        blocks.add(block);
    }

    public boolean hasNext()
    {
        return blocks.size() > 0;
    }

    public Block getNext()
    {
        return blocks.remove(blocks.size() - 1);
    }

    public ResultStatus getStatus()
    {
        return resultStatus;
    }

    public int getResultSetNum()
    {
        return resultSetNum;
    }
}
