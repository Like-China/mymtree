package mtree.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import mtree.Data;
import mtree.MTreeClass;
import mtree.tests.monitor.Ellipse;
import mtree.tests.monitor.Location;
import mtree.tests.monitor.Stream;



public class FloatTest {
	public MTreeClass mtree = new MTreeClass();
	public Set<Data> queryData = new HashSet<>();
    public Set<Data> dbData = new HashSet<>();

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
            Ellipse bead = new Ellipse(curLocation, nextLocation, 10);
            Data circle = new Data(curLocation.x, curLocation.y);
            if(i<qNum)
            {
                queryData.add(circle);
            }else
            {
                dbData.add(circle);
                mtree.add(circle);
            }
        }
    }

    public void randomGenerate(int dimensions, int dbNum, int qNum)
    {
        Random r = new Random();
        for(int count=0;count<dbNum+qNum;count++)
        {
            double[] values = new double[dimensions];
            for(int i=0;i<dimensions;i++)
            {
                values[i] = r.nextDouble()*100;
            }
            Data data = new Data(values);
            if(count<qNum)
            {
                queryData.add(data);
            }else
            {
                dbData.add(data);
                mtree.add(data);
            }
        }
    }

	public void check() {
        
		long t1 = System.currentTimeMillis();
		long t2 = System.currentTimeMillis();
        t1 = System.currentTimeMillis();
        int resCount = 0;
        for(Data qdata: queryData)
        {   
            for(Data dbdata: dbData)
            {
                double distance = Math.sqrt(Math.pow((qdata.getVales()[0]-dbdata.getVales()[0]),2)+Math.pow((qdata.getVales()[1]-dbdata.getVales()[1]),2));
				if(distance-qdata.radius<0.0001)
                {
                    resCount += 1;
                }
            }
        }
        t2 = System.currentTimeMillis();
        System.out.println("force time cost:" + (t2-t1) + " metch number: "+resCount);
        
        t1 = System.currentTimeMillis();
        resCount = 0; 
        for(Data qdata: queryData)
        {   
            resCount += _checkNearestByRange(qdata, qdata.radius);
        }
        t2 = System.currentTimeMillis();
        System.out.println("index time cost:" + (t2-t1)+ " metch number: "+resCount);
	}


	private int _checkNearestByRange(Data queryData, double radius) {
		MTreeClass.Query query = mtree.getNearestByRange(queryData, radius);

		// System.out.println("Range result:");
		// for (MTreeClass.ResultItem ri : query) {
		// 	results.add(ri);
		// 	// strippedResults.add(ri.data);
		// 	System.out.println(Arrays.toString(ri.data.getVales()) + " Dist to query: "
		// 			+ mtree.getDistanceFunction().calculate(ri.data, queryData));
		// 	// double dist = Math.sqrt(Math.pow((ri.data.getVales()[0]-queryData.getVales()[0]),2)+Math.pow((ri.data.getVales()[1]-queryData.getVales()[1]),2));
		// 	// System.out.println(dist);
		// }
		int count = query.rangeQuery();
        return count;
	}

	public static void main(String[] args) {
		FloatTest t = new FloatTest();
        t.randomGenerate(2, 100, 1000);
        t.establish(1000,1000);
		t.check();
	}

}
