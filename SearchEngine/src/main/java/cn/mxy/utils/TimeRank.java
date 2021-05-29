package cn.mxy.utils;

public class TimeRank {
    public Double timeTFIDFValue(String time){
        String[] splits = time.split("-");
        String year = splits[0];
        Double timePercent = null;
        switch (year) {
            case "2005":
                timePercent = 0.1;
                break;
            case "2006":
                timePercent = 0.12;
                break;
            case "2007":
                timePercent = 0.14;
                break;
            case "2008":
                timePercent = 0.16;
                break;
            case "2009":
                timePercent = 0.18;
                break;
            case "2010":
                timePercent = 0.21;
                break;
            case "2011":
                timePercent = 0.24;
                break;
            case "2012":
                timePercent = 0.28;
                break;
            case "2013":
                timePercent = 0.32;
                break;
            case "2014":
                timePercent = 0.37;
                break;
            case "2015":
                timePercent = 0.43;
                break;
            case "2016":
                timePercent = 0.5;
                break;
            case "2017":
                timePercent = 0.59;
                break;
            case "2018":
                timePercent = 0.69;
                break;
            case "2019":
                timePercent = 0.82;
                break;
            case "2020":
                timePercent = 0.9;
                break;
            case "2021":
                timePercent = 0.96;
                break;
        }
        return timePercent;
    }
}
