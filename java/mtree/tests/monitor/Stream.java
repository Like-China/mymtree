package mtree.tests.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

// Line: ralativeTimestamp real_lon real_lat pred_lon pred_lat
// e.g., 11 -8.625843 41.170005 -8.626070976257324 41.17040252685547

public class Stream {
	String txt_path;
	// total number of locations of a moving object
	public int size;
	// a file reader
	BufferedReader reader;
	// object id
	int id;
	// a list of real locations
	ArrayList<Location> realLocations = new ArrayList<>();
	// a list of real locations
	ArrayList<Location> predLocations = new ArrayList<>();

	public Stream(String txt_path, int id) {
		this.txt_path = txt_path + id+".txt";
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
	 *   read all predicted locations of a moving objects, as well as the real locations
	 */
	public void read()
	{	
		realLocations = new ArrayList<>();
		try {
			String lineString;
			predLocations = new ArrayList<>();
			while((lineString = reader.readLine()) != null)
			{	
				String[] line = lineString.split(" ");
				if(line.length == 5)
				{
					int relativeTS = Integer.parseInt(line[0]);
					float real_lon = Float.parseFloat(line[1]);
					float real_lat = Float.parseFloat(line[2]);
					float pred_lon = Float.parseFloat(line[3]);
					float pred_lat = Float.parseFloat(line[4]);
					// add locations
					realLocations.add(new Location(id, real_lon, real_lat,relativeTS));
					predLocations.add(new Location(id, pred_lon, pred_lat,relativeTS));
				}
			}
			reader.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		size = realLocations.size();
	}


	public static void main(String[] args) {
		// Stream s = new Stream("E:/data/contact_tracer/beijing10/2_27886741.txt");
		Stream s = new Stream("E:/data/AAAI23/predict_porto/",1);
		s.read();
	}
}



