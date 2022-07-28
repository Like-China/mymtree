package mtree.tests.monitor;

import java.util.Arrays;

/*
 * ��Բ��
 * �������ƶ���������������λ�õ������ƶ���Χ
 */
public class Ellipse {
    
    // ��ǰʱ��λ�õ�
    public Location curLocation;
    // ��һ��λ�õ�
    public Location nextLocation;
    // ����id
    public int objectID;
    // ��Բ��������
    public double[] center = new double[2];
    // ���뾶
    public double a = 0;
    // �̰뾶
    public double b = 0;
    // ƽ���ٶȣ��ֶ�����
    public double meanSpeed;
    // ����ٶȣ������ֵ
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

    // ��������ٶ� m/s
    public double getSpeed()
    {
        double dist = betweenDist();
        return dist/Settings.sr;
    }

    // ������Բ����λ�õ�֮��ľ���,��λΪm
    public double betweenDist()
    {
        return curLocation.distTo(nextLocation);
    }

    // ��������λ�õ㹹��һ���ƶ�����������Բ
    public void location2ellipse()
    {
        center[0] = (curLocation.x+nextLocation.x)/2;
        center[1] = (curLocation.y+nextLocation.y)/2;
        // ��ȡ����ٶ�
        meanSpeed = getSpeed();
        if(maxSpeed < meanSpeed)
        {
            this.maxSpeed = meanSpeed;
        }
        // ����a,b
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
