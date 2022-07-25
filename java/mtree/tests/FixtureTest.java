package mtree.tests;

import java.util.HashSet;
import java.util.Set;

import mtree.Data;
import mtree.MTreeClass;



// 鑷繁娴嬭瘯鏁村舰鏁版嵁鐨勬纭��
public class FixtureTest {
	public MTreeClass mtree = new MTreeClass();
	public Set<Data> allData = new HashSet<Data>();

	public long indexTimeCost = 0;
	public long foecrTimeCost = 0;
	public int forceResCount = 0;
	public int indexResCount = 0;

	public void test(String fixtureName) {

		Fixture fixture = Fixture.load(fixtureName);
		for (Fixture.Action action : fixture.actions) {
			switch (action.cmd) {
				case 'A':
					allData.add(action.data);
					mtree.add(action.data);
					break;
				case 'R':
					allData.remove(action.data);
					boolean removed = mtree.remove(action.data);
					assert removed;
					break;
				default:
					throw new RuntimeException(Character.toString(action.cmd));
			}

			// 1. index matching
			long t1 = System.currentTimeMillis();
			MTreeClass.Query query = mtree.getNearestByRange(action.queryData, action.radius);
			indexResCount += query.rangeQuery();
			long t2 = System.currentTimeMillis();
			indexTimeCost += (t2-t1);

			// 2. force matching
			t1 = System.currentTimeMillis();
			for(Data dbdata: allData)
			{
				double distance = 0;
				for(int i = 0; i < dbdata.getVales().length; i++) {
					double diff = action.queryData.getVales()[i] - dbdata.getVales()[i];
					distance += diff * diff;
				}
				distance = Math.sqrt(distance);
				if(distance<=action.radius)
				{
					forceResCount += 1;
				}
			}
			t2 = System.currentTimeMillis();
			foecrTimeCost += (t2-t1);
		}
		System.out.println("force time cost:" + foecrTimeCost + " metch number: "+forceResCount);
		System.out.println("index time cost:" + indexTimeCost+ " match number: "+indexResCount);
	}

	public static void main(String[] args) {
		FixtureTest t = new FixtureTest();
		t.test("fLots");
	}

}
