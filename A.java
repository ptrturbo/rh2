import static utils.CSVUtils.getInt;
import utils.CSVUtils;
import utils.AlgoUtils;
import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;

public class A {

	public static void main (String[] args) {

		String csvFile = "/home/greg/hr/SAX0408.DRF";

		boolean newDay = true;
        boolean maiden = false;
        boolean turf   = false;
		int	currRace = 0;
        int distance = 0;
        int postPos = 0;
        int lStarts = 0;
        int today = 0;
        String raceType = "";
        String jockey = "";
        Raceday raceDay = null;
        double[] fastestFraction = new double[2];

        // Create the in-memory database and tables
        
        Connection conn = SQLite.createNewDatabase();
        SQLite.createHorseTable(conn);
        SQLite.createFlagsTable(conn);
        SQLite.createL10Table(conn);
        SQLite.createHistoryTable(conn);

        // Read the CSV file and load the database
		try {
			Scanner scanner = new Scanner (new File(csvFile));
			while (scanner.hasNext()) {
				List<String> line = CSVUtils.parseLine(scanner.nextLine());
                SQLite.insertHorse(conn, line);
                for (int index=0; index<10; index++) {
                    SQLite.insertL10(conn, line, index);
                }
            }
	    } 

		catch (FileNotFoundException e) {
			System.out.println ("File not found " + e.toString());
		}
        /*
         * New race day - print date and track once per data set
         */
	    raceDay = new Raceday (line);
        today = raceDay.getDate();
		System.out.println ("Racing on " + today + " at " + raceDay.getTrack());	

        int maxRace = SQLite.getMaxRace(conn);

	    Race race = new Race(line);
				/*
				 * New race - print race and distance once per race
				 */
				int raceNum = race.getRaceNumber();
				if (raceNum != currRace) { 
                    // After processing all horses for a race, do post processing tasks
                    if (raceNum != 1) { // No data at this point for race #1
                        postProcessRace(conn, currRace, postPos); //Perform post processing tasks on prior race
                    }
					currRace = raceNum;
                    maiden = race.isMaiden();
                    distance = race.getDistance();
                    raceType = race.getRaceType();

					System.out.print("  Race " + raceNum + "  " + distance + " yards");

                    turf = AlgoUtils.isTurf(race.getSurface());
				    if (turf) {
					    System.out.print ("  Turf race");
				    }

                    System.out.println(" ");
                }

                /*
                 * New horse
                 */
				Horse horse = new Horse(line);
                postPos = horse.getPostPos();
                lStarts = horse.getLStarts();
                jockey  = horse.getJockey();
                SQLite.insertHorse(conn, currRace, postPos, horse.getName(), horse.getJockey(), horse.getTrainer());

                System.out.print("    PostPos " + postPos);

                System.out.println();

                /*
                 * Past performances
                 */
				PastPerf pastPerf = new PastPerf(line);

                String lastRaceType      = pastPerf.getRaceType()[0];
                int    steppingUpInClass = AlgoUtils.steppingUpInClass(raceType, pastPerf.getRaceType()[0],
                                           race.getPurse(), pastPerf.getPurse()[0],
                                           race.getClaimingPrice(), pastPerf.getHighClaimingPrice()[0]);

                boolean canDoTodaysClass  = pastPerf.canDo(today, raceType, race.getPurse(), race.getClaimingPrice(), "Today");
                boolean canDoPastClass    = pastPerf.canDo(today, raceType, race.getPurse(), race.getClaimingPrice(), "Past");

                SQLite.insertFlags(conn, currRace, postPos, 
                        horse.getTurfBet(), 
                        horse.getTurfMoney(), 
                        pastPerf.noSignOfLife(),
                        pastPerf.backToDirt(race.getSurface()), 
                        pastPerf.shorteningUp(race.getDistance()), 
	                    pastPerf.badDebutPost(maiden, postPos),
	                    pastPerf.debutTooLong(maiden, distance),
	                    pastPerf.wellBacked(maiden),
	                    pastPerf.triedTheDist(maiden, distance),
	                    pastPerf.tooManyChancesToWin(maiden, lStarts),
	                    pastPerf.toughSurfaceForDebut(maiden, turf),
	                    pastPerf.fromTopClassToMaidens(maiden),
	                    pastPerf.fromStraightMdnToClaimersStrong(raceType),
	                    pastPerf.fromStraightMdnToClaimersWeak(raceType),
	                    horse.topTrainer(),
	                    horse.poorTrainer(),
	                    horse.topJockey(),
	                    horse.poorJockey(),
                        steppingUpInClass,
                        canDoTodaysClass,
                        canDoPastClass,
                        pastPerf.isSharp(today),
                        pastPerf.isDroppingInToLowerCircuit(raceDay.getTrack()),
                        pastPerf.isShippingToHigherCircuit(raceDay.getTrack()),
                        pastPerf.hasSpeedRatingTrend(today, race.getSurface(), distance),
                        pastPerf.getMaxSpeedRating(today, race.getSurface(), distance));

                fastestFraction = pastPerf.getFastestFractions (today, race.getSurface(), distance);

                SQLite.insertHistory(conn, currRace, postPos, 
                        pastPerf.getJockey()[0], fastestFraction[0], fastestFraction[1]);

                double[] closingKick = pastPerf.getBestClosingKick(today, race.getSurface(), distance); 
                if (closingKick[0] != 999.0) {
                    System.out.format("Closing Kick %.1f%n",  closingKick[0]);
                }
                if (closingKick[1] != 999.0) {
                    System.out.format("Normalized Closing Kick %.1f%n",  closingKick[1]);
                }

/*
                if (fastestFraction[0] == 999.0 && fastestFraction[1] == 999.0) {
                    System.out.println("NONE");
                }
                else {
                    System.out.format ("Fraction1 %.1f", fastestFraction[0]);
                    if (fastestFraction[1] != 999.0) {
                        System.out.format ("  Fraction2 %.1f", fastestFraction[1]);
                    }
                    System.out.println("");
                }
	            if (pastPerf.noSignOfLife()) {
		            System.out.println("      No Sign of Life!");
	            }
	            if (pastPerf.backToDirt(race.getSurface())) {
		            System.out.println("      Back to Dirt!");
	            }
        
	            if (pastPerf.shorteningUp(race.getDistance())) {
		            System.out.println("      Shortening Up!");
	            }
        
	            if (pastPerf.badDebutPost(maiden, postPos)) {
		            System.out.println("      Bad Debut Post!");
	            }
        
	            if (pastPerf.debutTooLong(maiden, distance)) {
		            System.out.println("      Debut Too Long!");
	            }
        
	            if (pastPerf.wellBacked(maiden)) {
		            System.out.println("      Well Backed!");
	            }
        
	            if (pastPerf.triedTheDist(maiden, distance)) {
		            System.out.println("      Tried the Distance!");
	            }
        
	            if (pastPerf.tooManyChancesToWin(maiden, lStarts)) {
		            System.out.println("      Too many chances to win!");
	            }
        
	            if (pastPerf.toughSurfaceForDebut(maiden, turf)) {
		            System.out.println("      Tough surface for debut!");
	            }
        
	            if (pastPerf.fromTopClassToMaidens(maiden)) {
		            System.out.println("      From top class to maidens!");
	            }
        
	            if (pastPerf.fromStraightMdnToClaimersStrong(raceType)) {
		            System.out.println("      From Straight Maiden to Claimers (Strong)!");
	            }
        
	            if (pastPerf.fromStraightMdnToClaimersWeak(raceType)) {
		            System.out.println("      From Straight Maiden to Claimers (Weak)!");
	            }
        
	            if (horse.topTrainer()) {
		            System.out.println("      Top Trainer!");
	            }
        
	            if (horse.poorTrainer()) {
		            System.out.println("      Poor Trainer!");
	            }
	            if (horse.topJockey()) {
		            System.out.println("      Top Jockey!");
	            }
	            if (horse.poorJockey()) {
		            System.out.println("      Poor Jockey!");
	            }
        
	            if (steppingUpInClass > 0) {
		            System.out.println("      Stepping Up in Class by " + steppingUpInClass + 
                                    "  Race Type=" + raceType + "  Prior Type=" + lastRaceType);
	            }
                else if (steppingUpInClass < 0) {
		            System.out.println("      Dropping Down in Class by " + steppingUpInClass + 
                                    "  Race Type=" + raceType + "  Prior Type=" + lastRaceType);
	            }
*/
                if (canDoTodaysClass) {
                    System.out.println("    Can do today's class");
                }

                if (canDoPastClass) {
                    System.out.println("    Can do past class");
                }
/*
                if (pastPerf.isSharp(today)) {
                    System.out.println("      Sharp!");
                }

                if (pastPerf.isDroppingInToLowerCircuit(raceDay.getTrack())) {
                    System.out.println("      Dropping Circuit - good");
                }

                if (pastPerf.isShippingToHigherCircuit(raceDay.getTrack())) {
                    System.out.println("      Higher Circuit - bad");
                }
*/
        
	        } //while scanner
            postProcessRace(conn, currRace, postPos); //Perform post processing tasks on final race
	    } //try

		catch (FileNotFoundException e) {
			System.out.println ("File not found " + e.toString());
		}
	} //main

/*
 * Handle cases which require comparing across all horses in a race
 */
    public static void postProcessRace(Connection conn, int race, int maxPostPos) {
        jockeyChoice(conn, race, maxPostPos);
        speedRatingRank(conn, race, maxPostPos);
        loneF(conn, race, maxPostPos);
    }

/*
 * Get three best speed rated horses
 */
    public static void speedRatingRank(Connection conn, int race, int maxPostPos) {
        int[] speedRatings = new int[maxPostPos];
        int[] postPos = new int[maxPostPos];
        int tempr;
        int temph;

        speedRatings = SQLite.getBestSpeedRating(conn, race);
        for (int i=0; i<maxPostPos; i++) {
            postPos[i] = i+1;
        }
        
        //Sort the two lists
        for (int i=1; i<maxPostPos; i++) {
            for (int k=i; k>0 && speedRatings[k-1]>speedRatings[k]; k--) {
                tempr = speedRatings[k];
                temph = postPos[k];
                speedRatings[k] = speedRatings[k-1];
                postPos[k] = postPos[k-1];
                speedRatings[k-1] = tempr;
                postPos[k-1] = temph;
            }
        }

        //for (int i=maxPostPos-1; i>maxPostPos-4; i--) {
         //   System.out.println("Speed:" + speedRatings[i] + "  PostPos:" + postPos[i]);
        //}
    }


/*
 * Get Jockey Choice Plus and Minus
 */
    public static void jockeyChoice(Connection conn, int race, int maxPostPos) {

        String[] currJockey  = new String[maxPostPos];
        String[] priorJockey = new String[maxPostPos];
        boolean jockeyChoicePlus;
        boolean jockeyChoiceMinus;
        int jockeyMatch = 0;

        //Get list of jockies for this race
        currJockey = SQLite.getCurrentJockies(conn, race);

        //Get list of prior race jockies
        priorJockey = SQLite.getPriorJockies(conn, race);
        
        for (int postPos=0; postPos<maxPostPos; postPos++) {
            jockeyMatch = 0;
            
            // How many of today's horses has today's jockey ridden in prior races
            for (int j=0; j<maxPostPos; j++) {
                if (currJockey[postPos].equals(priorJockey[j])) {
                    jockeyMatch++;
                }
            }

            if (jockeyMatch > 1) { //if had ridden 2 or more prior horses
                jockeyChoicePlus = false;
                jockeyChoiceMinus = false;

                if (currJockey[postPos].equals(priorJockey[postPos])) { // if today's jockey was this horse's prior jockey
                    jockeyChoicePlus = true;
                    //System.out.println("Plus " + (postPos+1));
                    SQLite.updateJockeyChoice (conn, race, postPos, jockeyChoicePlus, jockeyChoiceMinus);

                    for (int pPos=0; pPos<maxPostPos; pPos++) { 
                        if (currJockey[postPos].equals(priorJockey[pPos]) && (postPos != pPos)) { 
                            jockeyChoiceMinus = true;
                    //System.out.println("Minus " + (pPos+1));
                            SQLite.updateJockeyChoice (conn, race, pPos, jockeyChoicePlus, jockeyChoiceMinus);
                        }
                    }
                }
            }
        }
    } //jockeyChoice

/*
 * Find any horse that has early speed calculations (fastest fractions)
 *  that are at least 0.6 seconds faster than any other horse in its race.
 *  Must have fractions for at least 50% of horses in the race.
 */
    public static void loneF(Connection conn, int race, int maxPostPos) {
        double[][] fractions = new double[2][maxPostPos];
        double[] fastest = {100.0, 100.0};
        double[] second  = {100.0, 100.0};
        int validHorses = 0;
        int fastHorse = 0;;

        //Find the fastest and second fastest times
        fractions = SQLite.getFractions(conn, race, maxPostPos);
        for (int postPos=0; postPos<maxPostPos; postPos++) {
            if (fractions[1][postPos] < 999.0) {
                validHorses++;
            }
            if (fractions[0][postPos] < fastest[0]) {
                for (int i=0; i<2; i++) {
                    second[i] = fastest[i];
                    fastest[i] = fractions[i][postPos];
                    fastHorse = i;
                }
            }
        }

        if (validHorses >= maxPostPos/2) {
            if (fastest[0] - second[0] >= 0.6) {
                System.out.format("Lone F - horse %d  %.1f  %.1f %n", (fastHorse+1), fastest[0], fastest[1]);
            }
        }
    } //loneF


} //class
