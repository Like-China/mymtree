package mtree;
import java.util.Random;

import mtree.DistanceFunctions.EuclideanCoordinate;
import mtree.tests.monitor.Ellipse;

/**
 * 逼近椭圆的圆形区域类，用来构建mtree索引
 */
public class Data implements EuclideanCoordinate, Comparable<Data>{
    
    public Ellipse bead;
    public double[] values;
    public double radius;
    private final int hashCode;
	private Random r = new Random();
    public Data(double... values)
    {
		this.values = values;
		this.radius = r.nextDouble()*10;

        int hashCode = 1;
		for(double value : values) {
			hashCode = (int)(31*hashCode + 11*value);
		}
		this.hashCode = hashCode;
    }

	public Data(int[] values)
    {
		double[] vals = new double[values.length];
		for(int i=0;i<vals.length;i++)
		{
			vals[i] = values[i];
		}
		this.values = vals;
		this.radius = r.nextDouble()*10;

        int hashCode = 1;
		for(double value : values) {
			hashCode = (int)(31*hashCode + 11*value);
		}
		this.hashCode = hashCode;
    }

	public Data(Ellipse bead){
		this.bead = bead;
        this.values = bead.center;
        this.radius = bead.a;

		int hashCode = 1;
		for(double value : values) {
			hashCode = (int)(31*hashCode + 11*value);
		}
		this.hashCode = hashCode;
	}
	
    // self-defined
	public double[] getVales()
	{
		return values;
	}

	@Override
	public int dimensions() {
		return values.length;
	}

	@Override
	public double get(int index) {
		return values[index];
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Data) {
			Data that = (Data) obj;
			if(this.dimensions() != that.dimensions()) {
				return false;
			}
			for(int i = 0; i < this.dimensions(); i++) {
				if(this.values[i] != that.values[i]) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int compareTo(Data that) {
		int dimensions = Math.min(this.dimensions(), that.dimensions());
		for(int i = 0; i < dimensions; i++) {
			double v1 = this.values[i];
			double v2 = that.values[i];
			if(v1 > v2) {
				return +1;
			}
			if(v1 < v2) {
				return -1;
			}
		}
		
		if(this.dimensions() > dimensions) {
			return +1;
		}
		
		if(that.dimensions() > dimensions) {
			return -1;
		}
		
		return 0;
	}


}
