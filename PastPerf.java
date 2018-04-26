/*
 * Each horse has records for up to 10 prior races.
 * This will return arrays of the past performance data.
 */

import java.util.List;
import utils.AlgoUtils;
import static utils.CSVUtils.getInt;
import static utils.CSVUtils.getDouble;
import org.apache.commons.lang3.StringUtils;

public class PastPerf {

	private int[]    ppRaceDate    = new int[10];
	private int[]    ppDistance    = new int[10];
    private int[]    ppPurse       = new int[10];
    private int[]    ppHighClaimingPrice = new int[10];
    private int[]    ppSpeedRating = new int[10];
	private String[] ppTrack       = new String[10];
	private String[] ppSurface     = new String[10];
	private double[] ppOdds        = new double[10];
	private String[] pp1stCallPos  = new String[10];
	private String[] pp2ndCallPos  = new String[10];
	private String[] ppGateCallPos = new String[10];
	private String[] ppStretchPos  = new String[10];
	private String[] ppFinishPos   = new String[10];
	private double[] pp1stCallBtn  = new double[10];
	private double[] pp2ndCallBtn  = new double[10];
	private double[] ppStretchBtn  = new double[10];
	private double[] ppFinishBtn   = new double[10];
	private double[] pp2fFraction  = new double[10];
	private double[] pp4fFraction  = new double[10];
	private double[] pp6fFraction  = new double[10];
	private double[] pp8fFraction  = new double[10];
	private double[] pp10fFraction = new double[10];
	private double[] pp12fFraction = new double[10];
	private double[] pp14fFraction = new double[10];
	private double[] pp16fFraction = new double[10];
	private double[] ppFinalTime   = new double[10];
	private String[] ppJockey      = new String[10];
	private String[] ppRaceType    = new String[10];

	public int[] getRaceDate() {
		return ppRaceDate;
	}

	public int[] getDistance() {
		return ppDistance;
	}

	public String[] getTrack() {
		return ppTrack;
	}

	public String[] getSurface() {
		return ppSurface;
	}

	public String[] get1stCallPos() {
		return pp1stCallPos;
	}

	public String[] get2ndCallPos() {
		return pp2ndCallPos;
	}

	public String[] getGateCallPos() {
		return ppGateCallPos;
	}

	public String[] getStretchPos() {
		return ppStretchPos;
	}

	public String[] getFinishPos() {
		return ppFinishPos;
	}

	public double[] get1stCallBtn() {
		return pp1stCallBtn;
	}

	public double[] get2ndCallBtn() {
		return pp2ndCallBtn;
	}

	public double[] getStretchBtn() {
		return ppStretchBtn;
	}

	public double[] getFinishBtn() {
		return ppFinishBtn;
	}

	public int[] getSpeedRating() {
		return ppSpeedRating;
	}

	public double[] get2fFraction() {
		return pp2fFraction;
	}

	public double[] get4fFraction() {
		return pp4fFraction;
	}

	public double[] get6fFraction() {
		return pp6fFraction;
	}

	public double[] get8fFraction() {
		return pp8fFraction;
	}

	public double[] get10fFraction() {
		return pp10fFraction;
	}

	public double[] get12fFraction() {
		return pp12fFraction;
	}

	public double[] get14fFraction() {
		return pp14fFraction;
	}

	public double[] get16fFraction() {
		return pp16fFraction;
	}

	public double[] getFinalTime() {
		return ppFinalTime;
	}

	public String[] getJockey() {
		return ppJockey;
	}

	public String[] getRaceType() {
		return ppRaceType;
	}

	public int[] getPurse() {
		return ppPurse;
	}

	public int[] getHighClaimingPrice() {
		return ppHighClaimingPrice;
	}

    public boolean hasRacedBefore() {
    	boolean racedBefore = false;
    	if (ppDistance[0] != 0) {
	        racedBefore = true;
	    }
	    return racedBefore;
    }

/*
 * No Sign of Life
 */    
    public boolean noSignOfLife() {
	    /*
	     * if in race[0] horse was never within 5 lengths of leader
	     *   1st Call Pos, 2nd Call Pos, Gate Call Pos
	     *   1st Call Btn, 2nd Call Btn, Stretch Btn, Finish Btn
	     */
	    boolean  result = true;
        String[] callPositions = new String[3];
        double[] callBtn       = new double[4];

	    callPositions[0] = pp1stCallPos[0];
	    callPositions[1] = pp2ndCallPos[0];
	    callPositions[2] = ppGateCallPos[0];

	    callBtn[0] = pp1stCallBtn[0];
	    callBtn[1] = pp2ndCallBtn[0];
	    callBtn[2] = ppStretchBtn[0];
	    callBtn[3] = ppFinishBtn[0];
	    
	    // If any call positions were 1, the horse has potential
	    for (String callPos: callPositions){	
		   if (callPos == "1") {
			  result = false; 
		   }
	    }
	    // If any call Btn is < 5, horse has potential
	    for (double callBetween: callBtn){	
		   if (callBetween < 5.0) {
			  result = false; 
		   }
	    }

	    return result;
    }

/*
 * Back to Dirt
 */
    public boolean backToDirt (String currentSurface){
	    /*
	     * Surface = 'D' && ppSurface[0]=='T' && ppSurface[1]=='T'
	     */
	    boolean result = false;
	    if (currentSurface.equals("D") && AlgoUtils.isTurf(ppSurface[0]) && AlgoUtils.isTurf(ppSurface[1])) {
	    //if (currentSurface.equals("D") && lastRaceSurface.equals("T") && priorRaceSurface.equals("T")) {
		    result = true;
	    }
	    return result;
    }

/*
 * Shortening Up
 */
    public boolean shorteningUp (int currentDistance){
	    /*
	     * distance<1760 && l10distance[0]>=1760 && l10Distance[1]>1760
	     */
	    boolean result = false;
	    if (currentDistance < 1760 && ppDistance[0] >= 1760 && ppDistance[1] >= 1760) {
		    result = true;
	    }
	    return result;
    }

/*
 * Debut Too Long
 */
    public boolean debutTooLong (boolean maiden, int distance){
	    /*
	     * race is maiden && distance>(>=)1320 && never raced before
	     */
	    boolean result = false;
	    if (maiden && distance > 1320 && !hasRacedBefore()) {
		    result = true;
	    }
	    return result;
    }

/*
 * Bad Debut Post
 */
    public boolean badDebutPost (boolean maiden, int postPos){
	    /*
	     * maiden race && never raced before && PostPos==1
	     */
	    boolean result = false;
	    if (maiden && postPos==1 && !hasRacedBefore()) {
		    result = true;
	    }
	    return result;
    }

/*
 * Tried The Distance
 */
    public boolean triedTheDist (boolean maiden, int distance){
	    /*
	     * maiden race && Distance >= 1760 && any of l10Distance[0-9]>=1760
	     */
	    boolean result = false;
	    if (maiden && distance>=1760) {
		   for (int i=0; i<10; i++) { 
			   if (ppDistance[i] >= 1760) {
				   result = true;
				   break;
			   }
		   }
	    }
	    return result;
    }

/*
 * Well-Backed
 */
    public boolean wellBacked (boolean maiden ){
	    /*
	     * maiden race && (l10Odds[0]<=5 || l10Odds[1]<=5)
	     */
	    boolean result = false;
	    if (maiden && hasRacedBefore() && ((ppOdds[0]>0 && ppOdds[0]<=5.0) || (ppOdds[1]>1 && ppOdds[1]<=5.0))) {
		    result = true;
	    }
	    return result;
    }

/*
 * Too Many Chances to Win
 */
    public boolean tooManyChancesToWin (boolean maiden, int lStarts) {
	    boolean result = false;
	    int finishPosition;

	    if (StringUtils.isNumeric(ppFinishPos[0])) {
		    finishPosition = getInt(ppFinishPos[0]);
	    	    if (maiden && lStarts>6 && (finishPosition>2 || ppFinishBtn[0]>1.0)) {
		    // lifetime starts  last race finish pos    last race finish btn lengths
		    	result = true;
		    }
	    }
	    return result;
    }

/*
 * Tough Surface for Debut
 */
    public boolean toughSurfaceForDebut (boolean maiden, boolean turf) {
	    boolean result = false;
	    if (maiden && turf && !hasRacedBefore()) {
		    result = true;
	    }
	    return result;
    }

/*
 * From Top Class to Maidens
 */
    public boolean fromTopClassToMaidens (boolean maiden) {
	    boolean result  = false;
	    String[] grades = {"G1", "G2", "G3", "N"};
	    if (maiden) {
		   for (String grade: grades) {
			  if (ppRaceType[0].equals(grade)) { 
		    	      result = true;
			  }
		   }
	    }
	    return result;
    }

/*
 * From Straight Maiden to Claimers (Strong)
 */
    public boolean fromStraightMdnToClaimersStrong (String raceType) {
	    boolean result = false;
	    if (raceType.equals("M") && ppRaceType[0].equals("S")) {
	    	result = true;
	    }
	    return result;
    }

/*
 * From Straight Maiden to Claimers (Weak)
 */
    public boolean fromStraightMdnToClaimersWeak (String raceType) {
	    boolean result = false;
	    if (raceType.equals("MO") && ppRaceType[0].equals("S")) {
	    	result = true;
	    }
	    return result;
    }

    public double adjustedFinalTime(int race) {
        if (ppFinishPos[race].equals("1")) {
            return ppFinalTime[race];
        }
        else {
            return ppFinalTime[race] + (ppFinishBtn[race] * 0.2);
        }
    }

    private double[] getFractions(int race) {
	    double[] fractions = new double[8];	//Fractional times for a race

		fractions[0] = pp2fFraction[race];
		fractions[1] = pp4fFraction[race];
		fractions[2] = pp6fFraction[race];
		fractions[3] = pp8fFraction[race];
		fractions[4] = pp10fFraction[race];
		fractions[5] = pp12fFraction[race];
		fractions[6] = pp14fFraction[race];
		fractions[7] = pp16fFraction[race];
        return fractions;
    }

    public double timeAtStretch(int race) {
	    if (ppStretchPos[race].equals("1")) {
		    return fractionTime(ppDistance[race], getFractions(race));
	    }
	    else {
		    return fractionTime(ppDistance[race], getFractions(race)) + (ppStretchBtn[race] * 0.2);
	    }
    }

    public double closingKick(int race) {
	    return (adjustedFinalTime(race) - timeAtStretch(race));
    }

	/*
	 * closingKick/distance, multiply by 100 just to bring it into the range of 0-10
	 */
    public double normalizedClosingKick(int race, int distance) {
	    return (closingKick(race) / (double)stretchDistance(distance) * 100.0);
    }

    /*
     * Best closing kick ([0]=regular, [1]=normalized for distance)
     */
    public double[] getBestClosingKick (int today, String surface, int distance ) {
        double bestClosingKick = 999.0;
        double bestNormalizedClosingKick = 999.0;
        int validRaces = 0;

        for (int race=0; race<10 && validRaces<2; race++) { //look at the last 10 races, only need two valid races
            if (AlgoUtils.getDaysSince(today, ppRaceDate[race]) < 180) { //race was in the past six months
                if (surface.equals(ppSurface[race])) {                 //race was the same surface as today's
                    if (Math.abs(distance - ppDistance[race]) <= 330) {  //distance is within 1 1/2 furlongs (330 yards) of current distance
                        validRaces++;   //This is a valid race
                        bestClosingKick = Math.min(bestClosingKick, closingKick(race));
                        bestNormalizedClosingKick = Math.min(bestNormalizedClosingKick, normalizedClosingKick(race, distance));
                    }
                }
            }
        }
        double[] bestCK = {bestClosingKick, bestNormalizedClosingKick}; 
        return bestCK;
    }

/*
 * Each standard race distance has an associated standard final stretch length
 */
    public int stretchDistance(int distance) {
	    int[] raceDistance  = { 880,  990, 1100, 1210, 1320, 1430, 1540, 1650, 1760, 1870, 1980, 2200, 2310, 2420,
		                   2530, 2640, 2750, 2860, 2970, 3080, 3190, 3300, 3520, 3630, 3740, 3850, 3960, 4070};
	    int[] stretchLength = { 440,  110,  220,  330,  440,  550,  660,  330,  440,  550,  660,  440,  550,  660, 
		                    330,  440,  550,  440,  550,  440,  550,  660,  440,  550,  660,  770,  440,  550}; 

	    for (int i=0; i<28; i++) {
		    if (distance <= raceDistance[i] ) {
			    return stretchLength[i];
		    }
	    }

	    System.out.format("Error in distance %d %n", distance);
	    System.exit(0);
	    return (0);
    }

/*
 * Annoyingly, the CSV file has different fields for the fraction times based on race distance
 */
    public double fractionTime(int distance, double[] fractions) {
	    double fTime = 0;

	    if (distance == 880) {
		    fTime = fractions[0];
	    }
	    else if (distance <= 1540) {
		    fTime = fractions[1];
	    }
	    else if (distance <= 1980) {
		    fTime = fractions[2];
	    }
	    else if (distance <= 2420) {
		    fTime = fractions[3];
	    }
	    else if (distance <= 2750) {
		    fTime = fractions[4];
	    }
	    else if (distance <= 2970) {
		    fTime = (fractions[4] + fractions[5]) / 2.0;
	    }
	    else if (distance <= 3300) {
		    fTime = fractions[5];
	    }
	    else if (distance <= 3850) {
		    fTime = fractions[6];
	    }
	    else if (distance <= 4070) {
		    fTime = fractions[7];
	    }
	    else {
		    System.out.format("Error in distance %d %n", distance);
		    System.exit(0);
	    }

	    return fTime;
    }


/*
 * Positive Jockey Choice
 */
    public boolean isJockeyChoicePlus (String todayJockey) {
	    boolean result = false;
	    int sameJockey = 0;

	    for (int i=0; i<10; i++) {
		    if (todayJockey.equals(ppJockey[i])) {
			    sameJockey++;
		    }
	    }
	    if (sameJockey >= 2) {
		    result = true;
	    }
	    return result;
    }    

/*
 * Can Do [Today's | Past] Class
 *   Today's is for races in the past year
 *   Past is for races at any time
 */
    public boolean canDo (int today, String raceType, int purse, int claimingPrice, String when) {
        boolean canDo = false;
        int dateRange = 365; //Default = one year

        if (when.equals("Past")) {
            dateRange = 10000; //Some arbitrarily long date range guaranteed to catch all past races
        }

        for (int i=0; i<10; i++) {
            if ((AlgoUtils.getDaysSince(today, ppRaceDate[i]) < dateRange) && //race was in the specified range
	            (StringUtils.isNumeric(ppFinishPos[i])) &&              //the finish position is numeric
                (getInt(ppFinishPos[i]) < 3)) {                          //horse finished 1st or 2nd
                if (ppRaceType[i].equals("A") && raceType.equals("A")) {
                    if ((purse-ppPurse[i]) < 18000) {
                        canDo = true;
                    }
                }
                else if (ppRaceType[i].equals("C") && raceType.equals("C")) {
                    if ((claimingPrice-ppHighClaimingPrice[i]) < 18000) {
                        canDo = true;
                    }
                }
                else if (ppRaceType[i].equals("M") && raceType.equals("M")) {
                    if ((claimingPrice-ppHighClaimingPrice[i]) < 10000) {
                        canDo = true;
                    }
                }
                else if (AlgoUtils.raceVal(ppRaceType[i]) >= AlgoUtils.raceVal(raceType)) {    //race was at or above today's type
                    canDo = true;
                }
            }
        }
        return canDo;
    }


    public boolean isSharp (int today) {
        boolean sharp = false;

	    if (StringUtils.isNumeric(ppFinishPos[0]) &&
	        StringUtils.isNumeric(ppStretchPos[0]) &&
            AlgoUtils.getDaysSince(today, ppRaceDate[0]) < 45) {

		    int finishPosition = getInt(ppFinishPos[0]);
		    int stretchPosition = getInt(ppStretchPos[0]);

            if (finishPosition == 1 || finishPosition == 2 || ppFinishBtn[0] < 1.0 ) {
                sharp = true;
            }

            else if ((finishPosition == 3) && ((ppStretchBtn[0] - ppFinishBtn[0]) > 0) && 
                    ((ppStretchBtn[0] - ppFinishBtn[0]) < 5.0)) {
                sharp = true;
            }
        }
        return sharp;
    }

/*
 * Dropping In To Lower Circuit
 */
    public boolean isDroppingInToLowerCircuit (String todaysTrack) {
        boolean droppingCircuits = false;
        if (AlgoUtils.compareCircuit(todaysTrack, ppTrack[0]) > 0 && hasRacedBefore()) {
            droppingCircuits = true;
        }
        return droppingCircuits;
    }

/*
 * Shipping To Higher Circuit
 */
    public boolean isShippingToHigherCircuit (String todaysTrack) {
        boolean higherCircuit = false;
        if (AlgoUtils.compareCircuit(todaysTrack, ppTrack[0]) < 0 && hasRacedBefore()) {
            higherCircuit = true;
        }
        return higherCircuit;
    }

/*
 * Prior Jockey Win Ratio
    public double getPriorJockeyRatio() {
        double ratio = 0.0;
	    if (jockeySts>0) {
            ratio = (double)jockeyWins/(double)jockeySts; 
	    }
	    return ratio;
    }
 */

/*
 * Speed Ratings 
 *   Return speed ratings for last three valid races
 */
    private int[] getSpeedRatings (int today, String surface, int distance) {
        int validRaces = 0;
        int[] speedRating = {0, 0, 0};

        for (int race=0; race<10 && validRaces<3; race++) {                //look at the last 10 races, need three valid races
            if ((AlgoUtils.getDaysSince(today, ppRaceDate[race]) < 366) &&  //race was in the past year
                (surface.equals(ppSurface[race])) &&                        //race was the same surface as today's
                (Math.abs(distance - Math.abs(ppDistance[race])) <= 330)) {  //distance is within 1 1/2 furlongs (330 yards) of current distance
                   speedRating[validRaces] = ppSpeedRating[race];
                   validRaces++;  
             }
        }
        return speedRating;
    }

/*
 * Speed Rating Trend
 */
    public boolean hasSpeedRatingTrend (int today, String surface, int distance) {
        boolean trend = false;
        int[] speedRating = new int[3];

        speedRating = getSpeedRatings(today, surface, distance);
        if (speedRating[0] != 0 && speedRating[1] != 0 && speedRating[2] != 0) {
            //Must show a trend of improving rating by 5 per race
            if ((speedRating[0] - speedRating[1]) >= 5 &&
                (speedRating[1] - speedRating[2]) >= 5) {
                trend = true;
                System.out.println("Speed Rating Trend");
            }
        }
        return trend;
    }

/*
 * Max Speed Rating 
 *   Return highest of three qualifying speed ratings
 */
    public int getMaxSpeedRating(int today, String surface, int distance) {
        int[] speedRating = new int[3];

        speedRating = getSpeedRatings(today, surface, distance);
        return Math.max(speedRating[0], Math.max(speedRating[1], speedRating[2]));
    }

/*
 * Find the fastest two fractions.
 *  Using the last two (or if only one qualifies, one) races run in the past 6 months 
 *  on the same surface
 *  and within 1/8 mile (220 yards) of today's race distance:
 *   Find the fastest two fractions 
 */
    public double[] getFastestFractions (int today, String surface, int distance ) {
        double[] fastestFraction = {999.0, 999.0};
        double[] checkFraction = new double[2];
        String[] callPos = new String[2];
        double[] callBtn = new double[2];
        int validRaces = 0;

        for (int race=0; race<10 && validRaces<2; race++) { //look at the last 10 races, only need two valid races
            if (AlgoUtils.getDaysSince(today, ppRaceDate[race]) < 180) { //race was in the past six months
                if (surface.equals(ppSurface[race])) {                 //race was the same surface as today's
                    if (Math.abs(distance - Math.abs(ppDistance[race])) <= 330) {  //distance is within 1 1/2 furlongs (330 yards) of current distance
                        validRaces++;   //This is a valid race

                        callPos[0] = pp1stCallPos[race];
                        callPos[1] = pp2ndCallPos[race];
                        callBtn[0] = pp1stCallBtn[race];
                        callBtn[1] = pp2ndCallBtn[race];

                        if (ppDistance[race] < 1300) { //For short races, use the 2f and 4f fractions
                            checkFraction[0] = pp2fFraction[race];
                            checkFraction[1] = pp4fFraction[race];
                        }
                        else {                      //For long races, use the 4f and 6f fractions
                            checkFraction[0] = pp4fFraction[race];
                            checkFraction[1] = pp6fFraction[race];
                        }

                        // Adjust fractions for position at fraction time
                        for (int j=0; j<2; j++) {   
                            //tweak time if not frontrunner
	                        if (!callPos[j].equals("1")) {
                                checkFraction[j] += (callBtn[j] * 0.2);
                            }
                        }

                        // Save the fastest fraction set
                        if (checkFraction[0] < fastestFraction[0]) { 
                            fastestFraction[0] = checkFraction[0];   
                            fastestFraction[1] = checkFraction[1];
                        } 
                    }
                }
            }
            else {  //The rest of the races are too old
                break;
            }

        }
        return fastestFraction;
    }


/*
 * Constructor
 */
	public PastPerf (List<String> line) {
		for (int i=0; i<10; i++) {
			this.ppRaceDate[i]    = getInt(line.get(255 + i));
            this.ppTrack[i]       = line.get(275 + i);
			this.ppDistance[i]    = getInt(line.get(315 + i));
			this.ppSurface[i]     = line.get(325 + i);
            this.ppOdds[i]        = getDouble(line.get(515+i));
            this.ppPurse[i]       = getInt(line.get(555+i));
			this.pp1stCallPos[i]  = line.get(575 + i);
			this.pp2ndCallPos[i]  = line.get(585 + i);
			this.ppGateCallPos[i] = line.get(595 + i);
			this.ppStretchPos[i]  = line.get(605 + i);
			this.ppFinishPos[i]   = line.get(615 + i);
			this.pp1stCallBtn[i]  = getDouble(line.get(665 + i));
			this.pp2ndCallBtn[i]  = getDouble(line.get(685 + i));
			this.ppStretchBtn[i]  = getDouble(line.get(725 + i));
			this.ppFinishBtn[i]   = getDouble(line.get(745 + i));
            this.ppSpeedRating[i] = getInt(line.get(845 + i));  //Using BRIS speed rating
			this.pp2fFraction[i]  = getDouble(line.get(875 + i));
			this.pp4fFraction[i]  = getDouble(line.get(895 + i));
			this.pp6fFraction[i]  = getDouble(line.get(915 + i));
			this.pp8fFraction[i]  = getDouble(line.get(935 + i));
			this.pp10fFraction[i] = getDouble(line.get(945 + i));
			this.pp12fFraction[i] = getDouble(line.get(955 + i));
			this.pp14fFraction[i] = getDouble(line.get(965 + i));
			this.pp16fFraction[i] = getDouble(line.get(975 + i));
			this.ppFinalTime[i]   = getDouble(line.get(1035 + i));
			this.ppJockey[i]      = line.get(1065 + i);
			this.ppRaceType[i]    = line.get(1085 + i);
            this.ppHighClaimingPrice[i] = getInt(line.get(1211+i));
		}
	}
}
