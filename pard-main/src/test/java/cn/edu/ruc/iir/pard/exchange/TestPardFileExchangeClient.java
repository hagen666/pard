package cn.edu.ruc.iir.pard.exchange;

/**
 * pard
 *
 * @author guodong
 */
public class TestPardFileExchangeClient
{
    private TestPardFileExchangeClient()
    {}

    public static void main(String[] args)
    {
        PardFileExchangeClient fileExchangeClient = new PardFileExchangeClient("127.0.0.1", 10012, "pard", "emp", "/Users/Jelly/Desktop/emp.tsv");
        fileExchangeClient.run();
    }
}