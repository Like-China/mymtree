// 参数设定
package mtree.tests.monitor;
public class Settings
{   
    public Settings()
    {

    }
    // The director  path that stores a set of trajectories (txt file)
    public static String path = "E:/data/AAAI23/pt20/";
    // the number of X slices during sampling locations in  a ellipse,-
    public static int xNum = 10;
    // the number of Y slices during sampling locations in  a ellipse
    public static int yNum = xNum;
    // the ratio of overlapped area between two ellipses, -
    public static double threshold = 0.3;
    // use prechecking strategy or not
    public static boolean isPrechecking = true;
    // the layers of contact network
    public static int layers = 2;
    // initial query num
    public static int qNum = 1000;
    // initial database num
    public static int dbNum = 20000;
    // the number of moving objects
    public static int readObjectNum = 23000;
    // the number of timestamps
    public static int tsNum = 10;
    // the range of the maximum speed of each moving objects，+
    public static int maxSpeed = 3;
    // sampling rates
    public static int sr = 15;
}