package vincentngo.solaryou;

/**
 * Created by Vincent Ngo on 5/21/2015.
 */
public class Planet {
    public double mEarthYear;
    public double mEarthDay;
    public String mName;

    public double getEarthYear() {
        return mEarthYear;
    }

    public double getEarthDay() {
        return mEarthDay;
    }

    public String getName() {
        return mName;
    }

    public void setProperties(double year,double day,String name) {
        mEarthDay=day;
        mEarthYear=year;
        mName=name;
    }
    public double getAttribute(String str) {
        if (str=="mEarthYear") {
            return mEarthYear;
        } else if (str=="mEarthDay") {
            return mEarthDay;
        } else return 0;
    }

}
