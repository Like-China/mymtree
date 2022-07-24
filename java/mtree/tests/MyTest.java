package mtree.tests;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class MyTest {
    public MTreeClass mtree = new MTreeClass();
	public Set<Data> allData = new HashSet<Data>();

    public  void test(String fixtureName) {
		Fixture fixture = Fixture.load(fixtureName);
		testFixture(fixture);
	}

    public  void testFixture(Fixture fixture) {
		for(Fixture.Action action : fixture.actions) {
			switch(action.cmd) {
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
            System.out.println("DB数据：");
            for(Data ddata:allData)
            {
                System.out.println(Arrays.toString(ddata.getVales())+" Dist to query: "+mtree.getDistanceFunction().calculate(ddata, action.queryData));
            }
            System.out.println("query data");
            System.out.println(Arrays.toString(action.queryData.getVales()));
			_checkNearestByRange(action.queryData, action.radius);
            System.out.println();
		}
	}

    private void _checkNearestByRange(Data queryData, double radius) {
		List<MTreeClass.ResultItem> results = new ArrayList<MTreeClass.ResultItem>();
		Set<Data> strippedResults = new HashSet<Data>();
		MTreeClass.Query query = mtree.getNearestByRange(queryData, radius);

        System.out.println("查询结果:");
		for(MTreeClass.ResultItem ri : query) {
			results.add(ri);
			strippedResults.add(ri.data);
            System.out.println(Arrays.toString(ri.data.getVales())+" Dist to query: "+mtree.getDistanceFunction().calculate(ri.data, queryData));
		}
        

	}


    public static void main(String[] args) {
        MyTest t = new MyTest();
        t.test("f01");
    }
	
}
