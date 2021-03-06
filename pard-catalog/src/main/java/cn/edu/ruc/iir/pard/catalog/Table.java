package cn.edu.ruc.iir.pard.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Table
{
    private String tablename;
    private int id;
    private HashMap<String, Column> columns;
    private HashMap<String, Fragment> fragment;
    private List<Privilege> privilegeList;
    private int isFragment;
    private HashMap<String, Statics> staticsMap;
    private int nextColumnId;
    public Table()
    {
        columns = new HashMap<String, Column>();
        fragment = new HashMap<String, Fragment>();
        privilegeList = new ArrayList<Privilege>();
        staticsMap = new HashMap<String, Statics>();
    }

    public Table(String tablename, int id, HashMap<String, Column> columns, HashMap<String, Fragment> fragment, List<Privilege> privilegeList, int isFragment, HashMap<String, Statics> staticsMap)
    {
        this.tablename = tablename;
        this.id = id;
        this.columns = columns;
        this.fragment = fragment;
        this.privilegeList = privilegeList;
        this.isFragment = isFragment;
        this.staticsMap = staticsMap;
    }

    public String getTablename()
    {
        return tablename;
    }

    public int getId()
    {
        return id;
    }

    public HashMap<String, Column> getColumns()
    {
        return columns;
    }

    public HashMap<String, Fragment> getFragment()
    {
        return fragment;
    }

    public List<Privilege> getPrivilegeList()
    {
        return privilegeList;
    }

    public int getIsFragment()
    {
        return isFragment;
    }

    public void setTablename(String tablename)
    {
        this.tablename = tablename;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setColumns(HashMap<String, Column> columns)
    {
        this.columns = columns;
    }

    public void setFragment(HashMap<String, Fragment> fragment)
    {
        this.fragment = fragment;
    }

    public void setPrivilegeList(List<Privilege> privilegeList)
    {
        this.privilegeList = privilegeList;
    }

    public void setIsFragment(int isFragment)
    {
        this.isFragment = isFragment;
    }

    public void setStaticsMap(HashMap<String, Statics> staticsMap)
    {
        this.staticsMap = staticsMap;
    }

    public HashMap<String, Statics> getStaticsMap()
    {
        return staticsMap;
    }

    public int getNextColumnId()
    {
        return nextColumnId;
    }
    public int nextColumnId()
    {
        return ++nextColumnId;
    }
    public void setNextColumnId(int nextColumnId)
    {
        this.nextColumnId = nextColumnId;
    }
}
