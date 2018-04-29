/*
 * Each horse has records for up to 10 prior races.
 * This will return arrays of the past performance data.
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.AlgoUtils;
import static utils.CSVUtils.getInt;
import static utils.CSVUtils.getDouble;
import org.apache.commons.lang3.StringUtils;

public class PastPerf {

    private Connection conn;
    private int      raceNum;
	private int      postPos;
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
	private String[] ppTrainer     = new String[10];
	private String[] ppRaceType    = new String[10];

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

/*
 * Has Raced Before
 */
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
	public PastPerf (Connection conn, int raceNum, int postPos) {
        String sql = "SELECT ppRaceDate, ppTrack, ppDistance, ppSurface, ppOdds, " +
                     "ppPurse, pp1stCallPos, pp2ndCallPos, ppGateCallPos, ppStretchPos, ppFinishPos, " +
                     "pp1stCallBtn, pp2ndCallBtn, ppStretchBtn, ppFinishBtn, ppSpeedRating, " +
                     "pp2fFraction, pp4fFraction, pp6fFraction, pp8fFraction, pp10fFraction, " +
                     "pp12fFraction, pp14fFraction, pp16fFraction, ppFinalTime, ppJockey, " +
                     "ppRaceType, ppHighClaimingPrice " +
                     "FROM t_last10 WHERE race = ? and postPos = ? and ppRace = ?"; 
        for (int ppRace=0; ppRace<10; ppRace++) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt (1, raceNum);
                pstmt.setInt (2, postPos);
                pstmt.setInt (3, ppRace);
                ResultSet rs = pstmt.executeQuery();
    
                while (rs.next()) {
                    this.ppTrainer[ppRace]           = rs.getString("trainer");
                    this.ppRaceDate[ppRace]          = rs.getInt("ppRaceDate");
                    this.ppTrack[ppRace]             = rs.getString("ppTrack");
                    this.ppDistance[ppRace]          = rs.getInt("ppDistance");
                    this.ppSurface[ppRace]           = rs.getString("ppSurface");
                    this.ppOdds[ppRace]              = rs.getDouble("ppOdds");
                    this.ppPurse[ppRace]             = rs.getInt("ppPurse");
                    this.pp1stCallPos[ppRace]        = rs.getString("pp1stCallPos");
                    this.pp2ndCallPos[ppRace]        = rs.getString("pp2ndCallPos");
                    this.ppGateCallPos[ppRace]       = rs.getString("ppGateCallPos");
                    this.ppStretchPos[ppRace]        = rs.getString("ppStretchCallPos");
                    this.ppFinishPos[ppRace]         = rs.getString("ppFinishPos");
                    this.pp1stCallBtn[ppRace]        = rs.getDouble("pp1stCallBtn");
                    this.pp2ndCallBtn[ppRace]        = rs.getDouble("pp2ndCallBtn");
                    this.ppStretchBtn[ppRace]        = rs.getDouble("ppStretchBtn");
                    this.ppFinishBtn[ppRace]         = rs.getDouble("ppFinishBtn");
                    this.ppSpeedRating[ppRace]       = rs.getInt("SpeedRating");
                    this.pp2fFraction[ppRace]        = rs.getDouble("pp2fFraction");
                    this.pp4fFraction[ppRace]        = rs.getDouble("pp4fFraction");
                    this.pp6fFraction[ppRace]        = rs.getDouble("pp6fFraction");
                    this.pp8fFraction[ppRace]        = rs.getDouble("pp8fFraction");
                    this.pp10fFraction[ppRace]       = rs.getDouble("pp10fFraction");
                    this.pp12fFraction[ppRace]       = rs.getDouble("pp12fFraction");
                    this.pp14fFraction[ppRace]       = rs.getDouble("pp14fFraction");
                    this.pp16fFraction[ppRace]       = rs.getDouble("pp16fFraction");
                    this.ppFinalTime[ppRace]         = rs.getDouble("ppFinalTime");
                    this.ppJockey[ppRace]            = rs.getString("Jockey");
                    this.ppRaceType[ppRace]          = rs.getString("ppRaceType");
                    this.ppHighClaimingPrice[ppRace] = rs.getInt("ppHighClaimingPrice");
                }
                this.conn    = conn;
                this.raceNum = raceNum;
                this.postPos = postPos;
    
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
	}
}
