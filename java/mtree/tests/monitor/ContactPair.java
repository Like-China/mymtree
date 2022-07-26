package mtree.tests.monitor;
import mtree.*;

public class ContactPair {
    /** A nearest-neighbor. */
    public Data query;
    public Data db;


    /** 
     * The distance from the nearest-neighbor to the query Data object
     * parameter.
     */
    public double distance;
    
    public ContactPair(Data query, Data db) {
        this.query = query;
        this.db = db;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return query.toString()+" "+db.toString();
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return query.hashCode()*db.hashCode();
    }


    
}
