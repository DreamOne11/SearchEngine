package cn.mxy;


import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class test {
    public static void main(String[] args) throws Exception {
        String url = "https://www.swpu.edu.cn/cxcy/zxjj/jgshe_z/cyx.htm";
        String timeRaw = "null";

        if(isRightTime(timeRaw)) {
            System.out.println("时间在范围区间内 " + timeRaw);
        } else {
            String gottenTime = getLastModified(url);
            System.out.println("时间在范围区间外 " + gottenTime );
        }

    }

    private static String getLastModified(String urlRaw) throws Exception {
        URL url = new URL(urlRaw);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        long lastModified = conn.getLastModified();
        //如果header中没有Last-modified，则用Date替换
        if(lastModified  == 0) {
            lastModified = conn.getDate();
            System.out.println("111");
        }
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.systemDefault()));
    }

    private static boolean isRightTime(String timeRaw) throws Exception {
        boolean rightTime = true;
        if(timeRaw.equals("null")) {
            //如果没有原本时间，则去爬取网页的last-modified时间
            try {
                rightTime = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String beginTime = "2005-01-01";
            String endTime = "2021-06-01";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = sdf.parse(beginTime);
            Date endDate = sdf.parse(endTime);
            Date urlDate ;

            //如果时间格式中包含分秒
            if(timeRaw.contains(":")) {
                String[] splits = timeRaw.split(" ");
                //获取年月日
                String[] ymd = splits[0].split("-");
                //判断月份是否在1-12月之间
                if (Integer.parseInt(ymd[1]) >= 1 && Integer.parseInt(ymd[1]) <= 12) {
                    //判断日期是否在1-31日之内
                    if(Integer.parseInt(ymd[2]) >= 1 && Integer.parseInt(ymd[2]) <= 31) {
                        urlDate = sdf.parse(timeRaw);
                        if(urlDate.after(beginDate)&&urlDate.before(endDate)) {
                            rightTime = true;
                        } else {
                            //规定时间范围外，爬取
                            try {
                                rightTime = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //规定时间范围外，爬取
                        try {
                            rightTime = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //规定时间范围外，爬取
                    try {
                        rightTime = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                String[] splits = timeRaw.split("-");
                if (Integer.parseInt(splits[1]) >= 1 && Integer.parseInt(splits[1]) <= 12) {
                    //判断日期是否在1-31日之内
                    if(Integer.parseInt(splits[2]) >= 1 && Integer.parseInt(splits[2]) <= 31) {
                        urlDate = sdf.parse(timeRaw);
                        if(urlDate.after(beginDate)&&urlDate.before(endDate)) {
                            rightTime = true;
                        } else {
                            //规定时间范围外，爬取
                            try {
                                rightTime = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //规定时间范围外，爬取
                        try {
                            rightTime = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //规定时间范围外，爬取
                    try {
                        rightTime = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return rightTime;
    }
}
