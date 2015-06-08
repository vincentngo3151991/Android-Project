package vincentngo.solaryou;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Vincent Ngo on 5/22/2015.
 */
public class PlanetList {
    private ArrayList<Planet> mPlanetList;
    private static PlanetList sPlanetList;

    public static PlanetList get() {
        if (sPlanetList==null) {
            sPlanetList=new PlanetList();
        } return sPlanetList;
    }

    private PlanetList() {
        mPlanetList=new ArrayList<Planet>();

        Planet Mercury=new Planet();
        Mercury.setProperties(0.2408,87.96,"Mercury");
        mPlanetList.add(Mercury);

        Planet Venus=new Planet();
        Venus.setProperties(0.615,224.68,"Venus");
        mPlanetList.add(Venus);

        Planet Earth=new Planet();
        Earth.setProperties(1,365.26,"Earth");
        mPlanetList.add(Earth);

        Planet Mars =new Planet();
        Mars.setProperties(1.88,686.96,"Mars");
        mPlanetList.add(Mars);

        Planet Jupiter=new Planet();
        Jupiter.setProperties(11.862,4266.96732,"Jupiter");
        mPlanetList.add(Jupiter);

        Planet Saturn=new Planet();
        Saturn.setProperties(29.456,10759.099,"Saturn");
        mPlanetList.add(Saturn);

        Planet Uranus =new Planet();
        Uranus.setProperties(84.07,30703.4082,"Uranus");
        mPlanetList.add(Uranus);

        Planet Neptune=new Planet();
        Neptune.setProperties(164.81,60198.5006,"Neptune");
        mPlanetList.add(Neptune);

    }
    public ArrayList<Planet> getPlanetList() {
        return mPlanetList;
    }
    public Planet getPlanet(String name) {
        for (Planet p:mPlanetList) {
            if (p.getName().equals(name)) {
                return p;
            }
        } return null;
    }
}
