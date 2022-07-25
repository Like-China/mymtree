package mtree.tests.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

// ����ĳһ���ʱ�������λ�õ��ı�·��
// E:\data\contact_tracer\beijing10
// ���ı�ÿһ�м�¼��ϢΪ 1,2008-02-02 15:36:08,116.51172,39.92123

public class Stream {
	// ��ǰ��ȡ��txt_path
	String txt_path;
	// �洢��ǰʱ�̶�ȡ����λ�õ�����, ���ڻ�ȡ��������
	public int current_index;
	// �ı����ܵ�λ�õ����,��¼���ļ�����
	public int location_totalLocNum;
	// ά��һ��reader
	BufferedReader reader;
	// ����˳���ȡ���ٶ�ÿ��ʱ�̵ĵ�һ��λ�õ㣬���ά����һ����һ��λ�õ�
	Location first_loc = null;
	int id;
	
	public Stream(String txt_path, int id) {
		this.txt_path = txt_path + id+".txt";
		current_index = 0;
		this.id = id;
		// TODO Auto-generated constructor stub
		try {
			File file = new File(this.txt_path);
			reader = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	/**
	 *   ��ȡһ��ʱ�̵�λ�õ�, ��¼��ArrayList<Location> batch�в�����
	 */
	public ArrayList<Location> read()
	{	
		ArrayList<Location> location_batch = new ArrayList<>();
		try {
			String lineString;
			int count = 0;
			while((lineString = reader.readLine()) != null)
			{	
				String[] line = lineString.split(" ");
				float lon = Float.parseFloat(line[0]);
				float lat = Float.parseFloat(line[1]);
				int ts = Integer.parseInt(line[2]);
				// ���λ�õ�
				location_batch.add(new Location(id, lon, lat,ts));
				count++;
			}
			current_index += count;
			if(location_batch.isEmpty())
			{
				reader.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return location_batch;
	}


	public static void main(String[] args) {
		// Stream s = new Stream("E:/data/contact_tracer/beijing10/2_27886741.txt");
		Stream s = new Stream("D:/data23/bj10/day2/",1);
		ArrayList<Location> locations = s.read();
		// ��������ֱ�Ӱ��վ�γ�ȼ���vs ת��Ϊƽ������ϵ�������
		System.out.println(locations.size());
	}
}



