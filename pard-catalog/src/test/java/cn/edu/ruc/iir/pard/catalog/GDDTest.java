package cn.edu.ruc.iir.pard.catalog;

import cn.edu.ruc.iir.pard.etcd.dao.SchemaDao;
import cn.edu.ruc.iir.pard.etcd.dao.SiteDao;
import net.sf.json.JSONObject;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GDDTest
{
    @Test
    public void testGDDDAO() throws IOException
    {
        SchemaDao schemaDao = new SchemaDao();
        SiteDao siteDao = new SiteDao();
        schemaDao.dropAll();
        siteDao.dropAll();
        GDD gdd = siteDao.load();
        gdd.getUserMap().clear();
        siteDao.persistGDD(gdd);
        FileInputStream fis = new FileInputStream(new File("iplist.properties"));
        Properties p = new Properties();
        p.load(fis);
        String[] iplist = p.getProperty("iplist").split(",");
        boolean hasLeader = false;
        for (String ip : iplist) {
            System.out.println(ip);
            Site s = new Site();
            s.setIp(ip);
            s.setName(ip);
            s.setPort(1239);
            if (!hasLeader) {
                s.setLeader(1);
                hasLeader = true;
            }
            boolean t = siteDao.add(s, false);
            System.out.println("___________________________________" + t);
        }
        gdd = siteDao.load();
        System.out.println(JSONObject.fromObject(gdd).toString(1));
    }
}
