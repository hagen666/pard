package cn.edu.ruc.iir.pard.client;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrettyTable
{
    private List<String> headers = new ArrayList<>();
    private List<List<String>> data = new ArrayList<>();
    private List<Integer> colLength = new ArrayList<>();

    public PrettyTable(String... headers)
    {
        this.headers.addAll(Arrays.asList(headers));
    }

    public void addRow(String... row)
    {
        data.add(Arrays.asList(row));
    }

    private int getMaxSizeForCol(int column)
    {
        int maxSize = headers.get(column).length();
        for (List<String> row : data) {
            if (row.get(column).length() > maxSize) {
                maxSize = row.get(column).length();
            }
        }
        return maxSize;
    }

    private String formatRule()
    {
        StringBuilder result = new StringBuilder();
        result.append("+");
        for (int i = 0; i < headers.size(); i++) {
            for (int j = 0; j < colLength.get(i).intValue() + 2; j++) {
                result.append("-");
            }
            result.append("+");
        }
        result.append("\n");
        return result.toString();
    }

    private String formatRuleNew()
    {
        StringBuilder result = new StringBuilder();
        result.append("+");
        for (int i = 0; i < headers.size(); i++) {
            colLength.add(new Integer(getMaxSizeForCol(i)));
            for (int j = 0; j < colLength.get(i).intValue() + 2; j++) {
                result.append("-");
            }
            result.append("+");
        }
        result.append("\n");
        return result.toString();
    }

    private String formatRowNew(List<String> row)
    {
        StringBuilder result = new StringBuilder();
        result.append("|");
        for (int i = 0; i < row.size(); i++) {
            result.append(StringUtils.center(row.get(i), colLength.get(i).intValue() + 2));
            result.append("|");
        }
        result.append("\n");
        return result.toString();
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(formatRuleNew());
        result.append(formatRowNew(headers));
        result.append(formatRule());
        for (List<String> row : data) {
            result.append(formatRowNew(row));
        }
        result.append(formatRule());
        return result.toString();
    }

    public int rowSize()
    {
        return data.size();
    }
}
