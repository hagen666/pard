package cn.edu.ruc.iir.pard.commons.config;

import java.util.Properties;

/**
 * pard
 *
 * @author guodong
 */
public abstract class PardConfiguration
{
    Properties configProps;
    Properties defaultProps;

    PardConfiguration()
    {
        configProps = new Properties();
        defaultProps = new Properties();
    }

    PardConfiguration setDefault(String key, String value)
    {
        defaultProps.setProperty(key, value);
        return this;
    }

    void setProperty(String key, String value)
    {
        configProps.setProperty(key, value);
    }

    String getProperty(String key)
    {
        return configProps.getProperty(key);
    }
}
