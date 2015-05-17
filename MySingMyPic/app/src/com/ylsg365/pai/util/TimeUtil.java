package com.ylsg365.pai.util;

/**
 * Created by ann on 2015-05-07.
 */
public class TimeUtil {
    public static String getTimeStr(long time)
    {
        long hs=0;
        long s=0;
        long m=0;

        String result="";
        hs=time%1000;
        time=time/1000;
        s=time%60;
        time=time/60;
        m=time;
        if(m>0)
        {
            result=m+"分"+s+"秒"+hs+"毫秒";
        }
        else
        {
            if(s>0)
                result=s+"秒"+hs+"毫秒";
            else result=hs+"毫秒";
        }
        return result;
    }

    public static String getTimeStr2(int time)
    {

        long s=0;
        long m=0;

        String result="";
        time=time/1000;
        s=time%60;
        time=time/60;
        m=time;
        if(m>0)
        {
            result=m+"'"+s+"''";
        }
        else
        {
            result=s+"''";
        }
        return result;
    }

    public static String getTimeStr3(int time)
    {

        long s=0;
        long m=0;

        String result="";
        time=time/1000;
        s=time%60;
        time=time/60;
        m=time;
        if(m>0)
        {
            result=m+":"+s+"";
        }
        else
        {
            result="0:"+s+"";
        }
        return result;
    }
}
