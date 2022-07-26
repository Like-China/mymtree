package mtree.tests.monitor;
import java.awt.geom.Ellipse2D;
public class Intersection {
    
    public static void main(String[] args) {
        Ellipse2D s1=new Ellipse2D.Float(0,0,100,100);
        System.out.println(s1.intersects(99, 30, 100, 100));
    }
}
