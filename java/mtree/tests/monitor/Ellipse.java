package mtree.tests.monitor;

import java.util.Arrays;

/*
 * 椭圆类
 * 包含了移动物体在两个连续位置点的最大移动范围
 */
public class Ellipse {
    
    // 当前时刻位置点
    public Location curLocation;
    // 下一个位置点
    public Location nextLocation;
    // 物体id
    public int objectID;
    // 椭圆中心坐标
    public double[] center = new double[2];
    // 长半径
    public double a = 0;
    // 短半径
    public double b = 0;
    // 平均速度，手动计算
    public double meanSpeed;
    // 最大速度，随机赋值
    public double maxSpeed;


    public Ellipse(Location curLocation, Location nextLocation, double maxSpeed)
    {
        assert curLocation.objectID == nextLocation.objectID;
        this.objectID = curLocation.objectID;
        this.curLocation = curLocation;
        this.nextLocation = nextLocation;
        this.maxSpeed = maxSpeed;
        location2ellipse();
    }

    // 获得两点速度 m/s
    public double getSpeed()
    {
        double dist = betweenDist();
        return dist/Settings.sr;
    }

    // 计算椭圆两个位置点之间的距离,单位为m
    public double betweenDist()
    {
        return curLocation.distTo(nextLocation);
    }

    // 根据两个位置点构建一个移动物体活动区域椭圆
    public void location2ellipse()
    {
        center[0] = (curLocation.x+nextLocation.x)/2;
        center[1] = (curLocation.y+nextLocation.y)/2;
        // 获取最大速度
        meanSpeed = getSpeed();
        if(maxSpeed < meanSpeed)
        {
            this.maxSpeed = meanSpeed;
        }
        // 计算a,b
        a = maxSpeed*(Settings.sr)/2;
        b = Math.sqrt(a*a-(Math.pow(curLocation.x-nextLocation.x,2)+Math.pow(curLocation.y-nextLocation.y,2))/4);
        assert a>0;
        assert b>0;
        
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return curLocation.toString()+"->\n"+nextLocation.toString()+"\nCenter: "+Arrays.toString(center)+" meanSpeed: "+meanSpeed+" maxSpeed: "+maxSpeed;
    }
}
