package cn.edu.ruc.iir.pard.etcd.dao;

import cn.edu.ruc.iir.pard.catalog.GDD;
import cn.edu.ruc.iir.pard.etcd.EtcdUtil;
/**
 * pard: GDDDao
 * The data access object of GDD.
 * used for GDD's load & persist.
 *
 * @author hagen
 * */
public class GDDDao
{
    private static GDD gdd = null;
    public GDDDao(){}
    public GDD load()
    {
        gdd = EtcdUtil.LoadGddFromEtcd();
        return gdd;
    }
    public boolean persist()
    {
        return EtcdUtil.TransGddToEtcd(gdd);
    }
    public boolean persistGDD(GDD gdd)
    {
        return EtcdUtil.TransGddToEtcd(gdd);
    }
}