package cn.edu.ruc.iir.pard.catalog;

import java.util.ArrayList;
import java.util.List;

public class Schema
{
    private String name;
    private int id;
    private List<Table> tableList;
    private List<Privilege> userList;
    private int nextTableId;
    public Schema()
    {
        tableList = new ArrayList<Table>();
        userList = new ArrayList<Privilege>();
    }

    public Schema(String name, int id, List<Table> tableList, List<Privilege> userList)
    {
        this.name = name;
        this.id = id;
        this.tableList = tableList;
        this.userList = userList;
    }

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public List<Table> getTableList()
    {
        return tableList;
    }

    public List<Privilege> getUserList()
    {
        return userList;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setTableList(List<Table> tableList)
    {
        this.tableList = tableList;
    }

    public void setUserList(List<Privilege> userList)
    {
        this.userList = userList;
    }

    public int getNextTableId()
    {
        return nextTableId;
    }

    public void setNextTableId(int nextTableId)
    {
        this.nextTableId = nextTableId;
    }
    public int nextTableId()
    {
        return ++nextTableId;
    }
}
