package vincentngo.solaryou;

/**
 * Created by Vincent Ngo on 5/21/2015.
 */
public class Converter {
    public static double convert(double inputNum, Planet inputPlanet, Planet outputPlanet) {
        return inputNum * inputPlanet.getEarthYear() / outputPlanet.getEarthYear();
    }
}