package mtree.tests.monitor;
import java.util.ArrayList;
import java.util.HashSet;

import mtree.Data;
import mtree.MTreeClass;


public class QueryTest {

    public MTreeClass mtree = new MTreeClass();
    // query set
    HashSet<Data> queries = new HashSet<>();
    // databse set
    HashSet<Data> db = new HashSet<>();

    // simlulate a set of queires and databse objects
    public void establish(int qNum, int dbNum)
    {
        // 当前时间索引
        int timeIndex = 0;
        for(int i=0;i<qNum+dbNum;i++)
        {   
            Stream s = new Stream("D:/data23/bj10/day2/",i);
            ArrayList<Location> locations = s.read();
            Location curLocation = locations.get(timeIndex);
            Location nextLocation = locations.get(timeIndex+1);
            Ellipse bead = new Ellipse(curLocation, nextLocation, 20);
            Data circle = new Data(bead);
            if(i<qNum)
            {
                queries.add(circle);
            }else
            {
                db.add(circle);
                mtree.add(circle);
            }
        }
    }


    // 检查遍历查找和索引查找的结果是否相同
    public void check() {
        
		long t1 = System.currentTimeMillis();
		long t2 = System.currentTimeMillis();
        t1 = System.currentTimeMillis();
        int resCount = 0;
        for(Data qdata: queries)
        {   
            for(Data dbdata: db)
            {
                double distance = Math.sqrt(Math.pow((qdata.getVales()[0]-dbdata.getVales()[0]),2)+Math.pow((qdata.getVales()[1]-dbdata.getVales()[1]),2));
				if(distance-qdata.radius<0.0001)
                {
                    resCount += 1;
                }
            }
        }
        t2 = System.currentTimeMillis();
        System.out.println("force time cost:" + (t2-t1) + " match number: "+resCount);
        
        t1 = System.currentTimeMillis();
        resCount = 0; 
        for(Data qdata: queries)
        {   
            resCount += _checkNearestByRange(qdata, qdata.radius);
        }
        t2 = System.currentTimeMillis();
        System.out.println("index time cost:" + (t2-t1)+ " match number: "+resCount);
	}

    private int _checkNearestByRange(Data queryData, double radius) {
		MTreeClass.Query query = mtree.getNearestByRange(queryData, radius);
		int count = query.rangeQuery();
        return count;
	}

    public static void main(String[] args) {
		QueryTest test = new QueryTest();
        test.establish(1000, 4000);
        test.check();
    }
    
		// 将两个点直接按照经纬度计算vs 转换为平面坐标系计算距离
		// System.out.println(locations);
}
