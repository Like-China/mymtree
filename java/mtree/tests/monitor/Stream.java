package mtree.tests.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

// 给定某一天的时间有序的位置点文本路径
// E:\data\contact_tracer\beijing10
// 该文本每一行记录信息为 1,2008-02-02 15:36:08,116.51172,39.92123

public class Stream {
	// 当前读取的txt_path
	String txt_path;
	// 存储当前时刻读取到的位置点索引, 用于获取批量数据
	public int current_index;
	// 文本中总的位置点个数,记录在文件名中
	public int location_totalLocNum;
	// 维护一个reader
	BufferedReader reader;
	// 由于顺序读取会少读每个时刻的第一个位置点，因此维护下一个第一个位置点
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
	 *   读取一个时刻的位置点, 记录到ArrayList<Location> batch中并返回
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
				// 添加位置点
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
		// 将两个点直接按照经纬度计算vs 转换为平面坐标系计算距离
		System.out.println(locations.size());
	}
}



