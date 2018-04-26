package utils;
/*
 * General purpose utilities
 */

import java.lang.Math;
import java.util.Map;
import java.util.HashMap;

public class AlgoUtils {

    
    public static int raceVal (String raceType) {
	    String[] classes = {"MO","M","C","S","CO","NO","T","AO","R","A","N","G3","G2","G1"};
	    int[] tiers      = { 0  , 0 , 1 , 2 , 2  , 2  , 2 , 2  , 2 , 3 , 4 , 5  , 6  , 7};
        int val = 0;

        for (int i=0; i<14; i++) {
            if (raceType.equals(classes[i])) {
                val = tiers[i];
            }
        }
        return val;
    }


    public static boolean isTurf (String surface) {
	    boolean turf = false;
	    if (surface.equals("T") || surface.equals("t")) {
		    turf = true;
	    }
    	    return turf;
    }

    public static int steppingUpInClass (String raceType, String priorRaceType, 
                                         int purse, int priorPurse,
                                         int claimingPrice, int priorHighClaimingPrice) {
	    int result = 0;
	    int raceLevel = 0;
	    int priorLevel = 0;

        int classChange = raceVal(raceType) - raceVal(priorRaceType);

        // Treat matching race types with significantly different purses as different classes
        if (classChange == 0) {
            if (raceType.equals("A") && priorRaceType.equals("A")) {
                if (purse - priorPurse >= 18000) {
                    classChange = 1;
                }
                else if (priorPurse - purse >= 18000) {
                    classChange = -1;
                }
            }

            else if ((raceType.equals("C") && priorRaceType.equals("C")) ||
                     (raceType.equals("M") && priorRaceType.equals("M"))) {
                if (claimingPrice - priorHighClaimingPrice >= 18000) {
                    classChange = 1;
                }
                else if (priorHighClaimingPrice - claimingPrice >= 18000) {
                    classChange = -1;
                }
            }
        }
	    return classChange;
    } 

    public static int getDaysSince (int currentDate, int pastDate) {
        int[] monthsLen = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};

        //Arbitrarily use Jan 1, 2010 as base date (this is just easy and good enough)
        int baseYear = 2010;
        pastDate = Math.max(pastDate, 20100101);

        int currYear = currentDate / 10000;
        int pastYear = pastDate / 10000;
        int currMonth = (currentDate - (currYear * 10000)) / 100;
        int pastMonth = (pastDate - (pastYear * 10000)) / 100;
        int currDay = currentDate - (currYear * 10000) - (currMonth * 100);
        int pastDay = pastDate - (pastYear * 10000) - (pastMonth * 100);
        int currNormalizedDate = (currYear - baseYear) * 365 + monthsLen[currMonth-1] + currDay; 
        int pastNormalizedDate = (pastYear - baseYear) * 365 + monthsLen[pastMonth-1] + pastDay; 
        int daysSince = currNormalizedDate - pastNormalizedDate;
        return daysSince;
    }

    public static int compareCircuit (String todayTrack, String priorTrack) {
        int todayCircuit = 4;
        int priorCircuit = 4;

        todayTrack = todayTrack.trim();
        todayTrack = todayTrack.toUpperCase();
        priorTrack = priorTrack.trim();
        priorTrack = priorTrack.toUpperCase();

        Map<String, Integer> tracks = new HashMap<String, Integer>();
        tracks.put("AQU", 1);
        tracks.put("BEL", 1);
        tracks.put("DMR", 1);
        tracks.put("SA",  1);
        tracks.put("SAR", 1);
        tracks.put("KEE", 1);
        tracks.put("KD",  1);
        tracks.put("CD",  2);
        tracks.put("GP",  2);
        tracks.put("PIM", 2);
        tracks.put("SUF", 2);
        tracks.put("OP",  2);
        tracks.put("WO",  2);
        tracks.put("PARX", 2);
        tracks.put("LRL", 2);
        tracks.put("IND", 2);
        tracks.put("MTH", 2);
        tracks.put("FG",  2);
        tracks.put("AP",  2);
        tracks.put("LA",  2);
        tracks.put("DED", 3);
        tracks.put("PRM", 3);
        tracks.put("RP",  3);
        tracks.put("TIM", 3);
        tracks.put("CBY", 3);
        tracks.put("CT",  3);
        tracks.put("DEL", 3);
        tracks.put("ELP", 3);
        tracks.put("EMD", 3);
        tracks.put("EVD", 3);
        tracks.put("FL",  3);
        tracks.put("GG",  3);
        tracks.put("HAW", 3);
        tracks.put("HST", 3);
        tracks.put("LS",  3);
        tracks.put("LAD", 3);
        tracks.put("MYR", 3);
        tracks.put("NP",  3);
        tracks.put("PEN", 3);
        tracks.put("PID", 3);
        tracks.put("HOU", 3);
        tracks.put("SR",  3);
        tracks.put("SUN", 3);
        tracks.put("TAM", 3);
        tracks.put("TDN", 3);
        tracks.put("ZIA", 3);
        tracks.put("BTP", 3);

        if (tracks.containsKey(todayTrack)) {
            todayCircuit = tracks.get(todayTrack);
        }

        if (tracks.containsKey(priorTrack)) {
            priorCircuit = tracks.get(priorTrack);
        }

        return  todayCircuit - priorCircuit ; // Negative means Shipping To Higher Circuit, Positive means Dropping To Lower Circuit
    }
}
