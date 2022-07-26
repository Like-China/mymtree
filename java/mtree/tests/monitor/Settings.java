// 参数设定
package mtree.tests.monitor;
public class Settings
{   
    public Settings()
    {

    }
    // the number of X slices during sampling locations in  a ellipse
    public static int xNum = 20;
    // the number of Y slices during sampling locations in  a ellipse
    public static int yNum = 20;
    // the ratio of overlapped area between two ellipses
    public static double threshold = 0.4;
}