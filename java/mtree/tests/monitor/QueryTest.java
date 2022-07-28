package mtree.tests.monitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import mtree.Data;
import mtree.MTreeClass;
import mtree.MTree.ResultItem;

public class QueryTest {

    public MTreeClass mtree = new MTreeClass();
    // query set
    HashSet<Data> queries = new HashSet<>();
    // databse set
    HashSet<Data> db = new HashSet<>();
    // random
    Random r = new Random(1);
    // store all (location, nextlocation) in a ArryLiat<Data> (real values)
    List<List<Data>> allRealLocation2nextLocations = new ArrayList<>();
    // store all (location, nextlocation) in a ArryLiat<Data>  (pred values)
    List<List<Data>> allPredLocation2nextLocations = new ArrayList<>();

    // simlulate a set of queires and databse objects
    public void getAllData(int readOnjectsNum, int readTimeNum)
    {   
        long t1 = System.currentTimeMillis();
        for(int i=1;i<readOnjectsNum+1;i++)
        {   
            Stream s = new Stream(Settings.path,i);
            s.read();
            // get real and predicted locations
            ArrayList<Location> realLocations = s.realLocations;
	        ArrayList<Location> predLocations = s.predLocations;
            // create real sequence of beads and predicted sequence of beads
            List<Data> realSeq = new ArrayList<>();
            List<Data> predSeq = new ArrayList<>();
            readTimeNum = readTimeNum>s.size?s.size:readTimeNum;
            for(int timeIndex = 0;timeIndex<readTimeNum;timeIndex++)
            {
                Location curLocation = realLocations.get(timeIndex);
                Location nextRealLocation = realLocations.get(timeIndex+1);
                Location nextPredLocation = predLocations.get(timeIndex+1);
                realSeq.add(new Data(new Ellipse(curLocation, nextRealLocation, Settings.maxSpeed)));
                predSeq.add(new Data(new Ellipse(curLocation, nextPredLocation, Settings.maxSpeed)));
            }
            allRealLocation2nextLocations.add(realSeq);
            allPredLocation2nextLocations.add(predSeq);
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Read fininshed, time cost: "+(t2-t1));
        System.out.println("The total number of moving objecct: "+allRealLocation2nextLocations.size());
        System.out.println("Mean number of timestamps: "+allRealLocation2nextLocations.get(0).size());
        System.out.println();
    }

    public void getDataByTime(int timeIndex, boolean isReal, int qNum, int dbNum)
    {   
        mtree = new MTreeClass();
        queries = new HashSet<>();
        db = new HashSet<>();
        List<List<Data>> allData = isReal ? allRealLocation2nextLocations:allPredLocation2nextLocations;
        assert allData.size()>(qNum+dbNum);
        for(int i=0;i<qNum+dbNum;i++)
        {
            Data circle = allData.get(i).get(timeIndex);
            if(i<qNum)
            {
                queries.add(circle);
                // System.out.println(circle.bead);
                // System.out.println();
                circle.isQuery = true;
            }else
            {
                circle.isQuery = false;
                db.add(circle);
                mtree.add(circle);
            }
        }
    }

    // check the correctness of indexed results v.s. force search
    public List<ContactPair> getCandidatesByForce() {
        
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
        System.out.println("force time cost:" + (t2-t1) + " candidate number: " + candidateCount + " candidate number after pruning: "+matchCount);
        return forceRes;
        
	}

    public List<ContactPair> getCandidatesByIndex()
    {
        List<ContactPair> indexRes = new ArrayList<>();
        // long t1 = System.currentTimeMillis();
        int candidateCount = 0; 
        int matchCount = 0;
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
        // long t2 = System.currentTimeMillis();
        // System.out.println("indexing candidates time cost:" + (t2-t1)+ " candidate number: " + candidateCount+ " candidate number after pruning: "+matchCount);
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

    // integrate index search
    public HashMap<Integer, List<ContactPair>> monitorByIndex()
    {   
        // get a set of location data
        // establish(Settings.qNum, Settings.dbNum);
        HashMap<Integer, List<ContactPair>>  layerMapResult = new HashMap<>();
        for(int layer=0;layer<Settings.layers;layer++)
        {   
            if(queries.isEmpty())
            {
                return layerMapResult;
            }
            // get candidates
            List<ContactPair> candidates = getCandidatesByIndex();
            // further filter results
            List<ContactPair> refinedResult = new ArrayList<>();
            for(ContactPair pair: candidates)
            {
                // If the ratio of overlap area exceeds threshold, add the pair to refined result
                if(isContact(pair.query, pair.db))
                {
                    refinedResult.add(pair);
                } 
            }
            // Add res to layerMapResult
            layerMapResult.put(layer, refinedResult);
            // update queries 
            queries = new HashSet<>();
            for(ContactPair pair:refinedResult)
            {
                queries.add(pair.db);
                pair.db.isQuery = true;
            }
        }
        return layerMapResult;
    }

    // integrate force search
    public HashMap<Integer, List<ContactPair>> monitorByForce()
    {
        HashMap<Integer, List<ContactPair>>  layerMapResult = new HashMap<>();
        for(int layer=0;layer<Settings.layers;layer++)
        {   
            if (queries.isEmpty())
            {
                return layerMapResult;
            }
            List<ContactPair> refinedResult = new ArrayList<>();
            for (Data qdata: queries)
            {
                for(Data dbdata:db)
                {   
                    if(isContact(qdata, dbdata))
                    {
                        refinedResult.add(new ContactPair(qdata, dbdata));
                    }
                }
            }
            // Add res to layerMapResult
            layerMapResult.put(layer, refinedResult);
            // update queries 
            queries = new HashSet<>();
            for(ContactPair pair:refinedResult)
            {
                queries.add(pair.db);
                pair.db.isQuery = true;
            }
        }
        return layerMapResult;
    }

    // finally check two eclipses are contacted or not
    public boolean isContact(Data qdata, Data dbdata)
    {   
        // check if dbdata has changed to query
        if (dbdata.isQuery) return false;
        if(Settings.isPrechecking)
        {
            if(Math.abs(qdata.get(0)-dbdata.get(0))>(qdata.bead.a+dbdata.bead.a) && Math.abs(qdata.get(1)-dbdata.get(1))>(qdata.bead.b+dbdata.bead.b))
            {
                return false;
            }
        }
        
        
        // Given a set of locations in query ellipse, consider how many are in db ellipse
        // find the rectangle that covers the ellipse, then get a set of sample coordinates
        double minX = qdata.get(0)-qdata.bead.a;
        double maxX = qdata.get(0)+qdata.bead.a;
        double minY = qdata.get(1)-qdata.bead.b;
        double maxY = qdata.get(1)+qdata.bead.b;
        // System.out.println(minX+" "+maxX+" "+minY+" "+maxY);
        int validCount_a = 0;
        int hitCount_a = 0;
        ArrayList<double[]> sampleValues = sample(minX, maxX, minY, maxY, Settings.xNum, Settings.yNum);
        int sampleSize = sampleValues.size();
        for(int i=0;i<sampleSize;i++)
        {
            double[] val = sampleValues.get(i);
            double dist = Math.pow(val[0]-qdata.get(0),2)/Math.pow(qdata.bead.a, 2)+Math.pow(val[1]-qdata.get(1),2)/Math.pow(qdata.bead.b, 2);
            // if val in query ellipse, validCount += 1;
            if(dist<1) 
            {
                validCount_a+=1;
            }else
            {
                continue;
            }
            dist = Math.pow(val[0]-dbdata.get(0),2)/Math.pow(dbdata.bead.a, 2)+Math.pow(val[1]-dbdata.get(1),2)/Math.pow(dbdata.bead.b, 2);
            // if val in db ellipse as well, hitCount +=1
            if(dist<1)  hitCount_a +=1;
            // If all the remaining locations are not enough to meet the threshold, exit
            if((double)(hitCount_a+(sampleSize-i-1))/validCount_a<Settings.threshold) break;
        }
        // If the ratio of overlap area exceeds threshold, add the pair to refined result
        if((double)hitCount_a/validCount_a<Settings.threshold)
        {
            return false;
        } 

        // Given a set of locations in db ellipse, consider how many are in query ellipse
        // find the rectangle that covers the ellipse, then get a set of sample coordinates
        minX = dbdata.get(0)-dbdata.bead.a;
        maxX = dbdata.get(0)+dbdata.bead.a;
        minY = dbdata.get(1)-dbdata.bead.b;
        maxY = dbdata.get(1)+dbdata.bead.b;
        // System.out.println(minX+" "+maxX+" "+minY+" "+maxY);
        int validCount_b = 0;
        int hitCount_b = 0;
        sampleValues = sample(minX, maxX, minY, maxY, Settings.xNum, Settings.yNum);
        sampleSize = sampleValues.size();
        for(int i=0;i<sampleSize;i++)
        {
            double[] val = sampleValues.get(i);
            double dist = Math.pow(val[0]-dbdata.get(0),2)/Math.pow(dbdata.bead.a, 2)+Math.pow(val[1]-dbdata.get(1),2)/Math.pow(dbdata.bead.b, 2);
            // if val in query ellipse, validCount += 1;
            if(dist<1) 
            {
                validCount_b+=1;
            }else
            {
                continue;
            }
            dist = Math.pow(val[0]-qdata.get(0),2)/Math.pow(qdata.bead.a, 2)+Math.pow(val[1]-qdata.get(1),2)/Math.pow(qdata.bead.b, 2);
            // if val in db ellipse as well, hitCount +=1
            if(dist<1)  hitCount_b +=1;
            // If all the remaining locations are not enough to meet the threshold, exit
            if((double)(hitCount_b+(sampleSize-i-1))/validCount_b<Settings.threshold) break;
        }
        // If the ratio of overlap area exceeds threshold, add the pair to refined result
        // if((double)hitCount_a/validCount_a+(double)hitCount_b/validCount_b>=Settings.threshold*2)
        // {
        //     return true;
        // } 
        if((double)hitCount_b/validCount_b>=Settings.threshold)
        {
            return true;
        } 
        return false;
    }

    // given two sets of ContactPair, output the accuracy ratio
    // return the accuracy of directly contact cases, and the overall accuracy
    public double[] getAccuracy(HashMap<Integer, List<ContactPair>> predictionResults, HashMap<Integer, List<ContactPair>> referenceResults)
    {
        double[] accuracy = new double[2];
        HashSet<Integer> predContactId = new HashSet<>();
        HashSet<Integer> refContactId = new HashSet<>();
        for(ContactPair predPair: predictionResults.get(0))
        {
            predContactId.add(predPair.db.bead.curLocation.objectID);
        }
        for(ContactPair predPair: referenceResults.get(0))
        {
            refContactId.add(predPair.db.bead.curLocation.objectID);
        }
        // System.out.println(refContactId.size()+" "+predContactId.size());
        predContactId.retainAll(refContactId);
        // System.out.println(refContactId.size()+" "+predContactId.size());
        accuracy[0] = (double)(predContactId.size())/refContactId.size();

        // store all predicted contact cases, and all reference contact cases
        predContactId = new HashSet<>();
        for(int i=0;i<predictionResults.size();i++)
        {
            for(ContactPair predPair: predictionResults.get(i))
            {
                predContactId.add(predPair.db.bead.curLocation.objectID);
            }
        }
        refContactId = new HashSet<>();
        for(int i=0;i<referenceResults.size();i++)
        {
            for(ContactPair predPair: referenceResults.get(i))
            {
                refContactId.add(predPair.db.bead.curLocation.objectID);
            }
        }
        // count the number of intersection
        // System.out.println(refContactId.size()+" "+predContactId.size());
        predContactId.retainAll(refContactId);
        // System.out.println(refContactId.size()+" "+predContactId.size());
        accuracy[1] = (double)(predContactId.size())/refContactId.size();
        return accuracy;
    } 

    
    public void _check()
    {
        int timeIndex = 10;
        long t1, t2;
        getAllData(Settings.readObjectNum, Settings.tsNum);
        getDataByTime(timeIndex, false, Settings.qNum, Settings.dbNum);
        List<Integer> nums;
        t1 = System.currentTimeMillis();
        HashMap<Integer, List<ContactPair>> refinedResIndex = monitorByIndex();
        t2 = System.currentTimeMillis();
        nums = new ArrayList<>();
        for(int i=0;i<refinedResIndex.size();i++)
        {
            nums.add(refinedResIndex.get(i).size());
        }
        System.out.println("Index Time Cost: "+(t2-t1)+ " Number: "+ nums);

        System.out.println();
        getDataByTime(timeIndex, false, Settings.qNum, Settings.dbNum);
        t1 = System.currentTimeMillis();
        HashMap<Integer, List<ContactPair>> refinedResForce = monitorByForce();
        t2 = System.currentTimeMillis();
        nums = new ArrayList<>();
        for(int i=0;i<refinedResForce.size();i++)
        {
            nums.add(refinedResForce.get(i).size());
        }
        System.out.println("Force Time Cost: "+(t2-t1)+ " Number: "+ nums);

        double[] accuracys = getAccuracy(refinedResIndex, refinedResForce);
        System.out.println(accuracys[0]+ " "+accuracys[1]);
    }

    // experimental evaluation
    public void evaluate()
    {
        long t1, t2, start, end;
        int timeIndex = 10; //r.nextInt(Settings.tsNum);
        getAllData(Settings.readObjectNum, Settings.tsNum);
        double mean_accuracy = 0;
        start = System.currentTimeMillis();
        for(timeIndex=0;timeIndex<Settings.tsNum;timeIndex++)
        {
            getDataByTime(timeIndex, false, Settings.qNum, Settings.dbNum);
            List<Integer> nums;
            t1 = System.currentTimeMillis();
            HashMap<Integer, List<ContactPair>> predRes = monitorByIndex();
            t2 = System.currentTimeMillis();
            nums = new ArrayList<>();
            for(int i=0;i<predRes.size();i++)
            {
                nums.add(predRes.get(i).size());
            }
            System.out.println("Predict Time Cost: "+(t2-t1)+ " Number: "+ nums);

            getDataByTime(timeIndex, true, Settings.qNum, Settings.dbNum);
            t1 = System.currentTimeMillis();
            HashMap<Integer, List<ContactPair>> realRes = monitorByIndex();
            t2 = System.currentTimeMillis();
            nums = new ArrayList<>();
            for(int i=0;i<realRes.size();i++)
            {
                nums.add(realRes.get(i).size());
            }
            System.out.println("Real Time Cost: "+(t2-t1)+ " Number: "+ nums);

            double[] accuracys = getAccuracy(predRes, realRes);
            System.out.println(accuracys[0]+ " "+accuracys[1]);
            mean_accuracy += accuracys[1];
        }
        mean_accuracy = mean_accuracy/(Settings.tsNum);
        System.out.println(mean_accuracy);
        end = System.currentTimeMillis();
        System.out.println("Total time cost: "+(end-start));
    }

    public static void main(String[] args) {
		QueryTest test = new QueryTest();
        test.evaluate();
    }
    
    // 将两个点直接按照经纬度计算vs 转换为平面坐标系计算距离
    // System.out.println(locations);
}
