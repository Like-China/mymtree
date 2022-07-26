package mtree.tests.monitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.naming.ContextNotEmptyException;

import mtree.Data;
import mtree.MTreeClass;
import mtree.MTree.ResultItem;

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
                queries.add(data);
            }else
            {
                db.add(data);
                mtree.add(data);
            }
        }
    }

    // check the correctness of indexed results v.s. force search
    // Correct
    public List<ContactPair> check() {
        
		long t1 = System.currentTimeMillis();
		long t2 = System.currentTimeMillis();
        t1 = System.currentTimeMillis();
        int candidateCount = 0;
        int matchCount = 0;

        // force match
        List<ContactPair> forceRes = new ArrayList<>();
        for(Data qdata: queries)
        {   
            for(Data dbdata: db)
            {
                double distance = Math.sqrt(Math.pow((qdata.getVales()[0]-dbdata.getVales()[0]),2)+Math.pow((qdata.getVales()[1]-dbdata.getVales()[1]),2));
				if(distance-qdata.radius-dbdata.radius<0.0001)
                {
                    candidateCount += 1;
                    if(Math.abs(qdata.get(1)-dbdata.get(1))<=(qdata.bead.b+dbdata.bead.b))
                    {
                        matchCount += 1;
                        forceRes.add(new ContactPair(qdata, dbdata));
                    }
                }
                
            }
        }
        t2 = System.currentTimeMillis();
        System.out.println("force time cost:" + (t2-t1) + " candidate number: " + candidateCount + " match number: "+matchCount);
        
        // index match
        List<ContactPair> indexRes = new ArrayList<>();
        t1 = System.currentTimeMillis();
        candidateCount = 0; 
        matchCount = 0;
        for(Data qdata: queries)
        {   
            MTreeClass.Query query = mtree.getNearestByRange(qdata, qdata.radius);
            List<ResultItem> candidate = query.rangeQuery();
            candidateCount += candidate.size();
            for(ResultItem item: candidate)
            {   
                Data dbdata = item.Data;
                // pruning
                if(Math.abs(qdata.get(1)-dbdata.get(1))<=(qdata.bead.b+dbdata.bead.b))
                {
                    matchCount += 1;
                    indexRes.add(new ContactPair(qdata, dbdata));
                }
            }
		    
        }
        t2 = System.currentTimeMillis();
        System.out.println("index time cost:" + (t2-t1)+ " candidate number: " + candidateCount+ " match number: "+matchCount);
        // return indexed results after pruning
        return indexRes;
        
	}

    // get n sampled coordinates within a given rectangle
    public ArrayList<double[]> sample(double minX, double maxX, double minY, double maxY, int xNum, int yNum)
    {
        ArrayList<double[]> sampleValues = new ArrayList<>();
        for(int i=0;i<xNum;i++)
        {
            for(int j=0;j<yNum;j++)
            {
                double sample_x = minX + (maxX-minX)/xNum*i;
                double sample_y = minY + (maxY-minY)/yNum*j;
                double[] sampleLoction = new double[]{sample_x,sample_y};
                sampleValues.add(sampleLoction);
            }
        }
        return sampleValues;
    }


    // after indexing and pruing, the remaining candidates are further checked
    public List<ContactPair> refine(List<ContactPair> candidates, double threshold)
    {
        List<ContactPair> refinedResult = new ArrayList<>();
        for(ContactPair pair: candidates)
        {
            Data q = pair.query;
            Data db = pair.db;

            // find the rectangle that covers the ellipse, then get a set of sample coordinates
            double minX = q.get(0)-q.bead.a;
            double maxX = q.get(0)+q.bead.a;
            double minY = q.get(1)-q.bead.b;
            double maxY = q.get(1)+q.bead.b;
            // Given a set of locations in query ellipse, consider how many are in db ellipse
            // System.out.println(minX+" "+maxX+" "+minY+" "+maxY);
            int validCount = 0;
            int hitCount = 0;
            ArrayList<double[]> sampleValues = sample(minX, maxX, minY, maxY, Settings.xNum, Settings.yNum);
            int sampleSize = sampleValues.size();
            for(int i=0;i<sampleSize;i++)
            {
                double[] val = sampleValues.get(i);
                double dist = Math.pow(val[0]-q.get(0),2)/Math.pow(q.bead.a, 2)+Math.pow(val[1]-q.get(1),2)/Math.pow(q.bead.b, 2);
                // if val in query ellipse, validCount += 1;
                if(dist<1) 
                {
                    validCount+=1;
                }else
                {
                    continue;
                }
                dist = Math.pow(val[0]-db.get(0),2)/Math.pow(db.bead.a, 2)+Math.pow(val[1]-db.get(1),2)/Math.pow(db.bead.b, 2);
                // if val in db ellipse as well, hitCount +=1
                if(dist<1)  hitCount +=1;
                // If all the remaining locations are not enough to meet the threshold, exit
                if((double)(hitCount+(sampleSize-i-1))/validCount<threshold) break;
            }

            // If the ratio of overlap area exceeds threshold, add the pair to refined result
            if((double)hitCount/validCount>threshold)
            {
                refinedResult.add(pair);
            } 
            
        }
        
        return refinedResult;
    }

    public static void main(String[] args) {
		QueryTest test = new QueryTest();
        // test.randomGenerate(2, 100, 1000);
        test.establish(1000, 4000);
        List<ContactPair> candidates = test.check();
        // further filter results
        List<ContactPair> refinedRes = test.refine(candidates, Settings.threshold);
        System.out.println("Final number of contact: "+refinedRes.size());
    }
    
		// 将两个点直接按照经纬度计算vs 转换为平面坐标系计算距离
		// System.out.println(locations);
}
