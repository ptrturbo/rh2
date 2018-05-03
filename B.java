import static utils.CSVUtils.getInt;
import utils.CSVUtils;
import utils.AlgoUtils;
import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;

public class B {

	public static void main (String[] args) {

		String csvFile = "/home/greg/hr/SAX0408.DRF";

		boolean newDay = true;
        int raceNum = 0;
        int maxRace = 0;
        int maxPost = 0;
        int maxHist = 0;
        int postPos = 0;
        int lStarts = 0;
        double[] fastestFraction = new double[2];

        // Create the in-memory database and tables
        
        Connection conn = SQLite.createNewDatabase();
        SQLite.createHorseTable(conn);
        SQLite.createFlagsTable(conn);
        SQLite.createLast10Table(conn);
        SQLite.createHistoryTable(conn);
        
        // Load the CSV file into the database
        SQLite.loadCSV(conn, csvFile);

        Raceday raceDay = new Raceday (conn);
        System.out.println ("Racing on " + raceDay.today() + " at " + raceDay.track());	

        // For every race
        for (raceNum=1; raceNum<=raceDay.maxRace(); raceNum++) {
            Race race = new Race(conn, raceNum);
            maxPost = race.maxPost();
            System.out.println("Race " + raceNum + "  max post " + maxPost);

            System.out.println("  Distance " + race.distance() + "  Surface " + race.surface() + "  Type " + race.raceType());
            if (race.isMaiden()) {System.out.println("    Maiden");}
            if (race.isTurf()) {System.out.println("    Turf");}

            // for every post position in the race
            for (postPos=1; postPos<=race.maxPost(); postPos++) {
                System.out.println("Post " + postPos);
                Horse horse = new Horse(conn, raceNum, postPos);

                PastPerf pp = new PastPerf(conn, raceNum, postPos);

                int    steppingUpInClass = AlgoUtils.steppingUpInClass(race.raceType(), pp.raceType()[0],
                                           race.purse(), pp.purse()[0],
                                           race.claimingPrice(), pp.highClaimingPrice()[0]);
                boolean canDoTodaysClass  = pp.canDo(raceDay.today(), race.raceType(), race.purse(), race.claimingPrice(), "Today");
                boolean canDoPastClass    = pp.canDo(raceDay.today(), race.raceType(), race.purse(), race.claimingPrice(), "Past");
                
                SQLite.insertFlags(conn, raceNum, postPos, 
                        horse.turfBet(), 
                        horse.turfMoney(), 
                        pp.noSignOfLife(),
                        pp.backToDirt(race.surface()), 
                        pp.shorteningUp(race.distance()), 
	                    pp.badDebutPost(race.isMaiden(), postPos),
	                    pp.debutTooLong(race.isMaiden(), race.distance()),
	                    pp.wellBacked(race.isMaiden()),
	                    pp.triedTheDist(race.isMaiden(), race.distance()),
	                    pp.tooManyChancesToWin(race.isMaiden(), horse.lStarts()),
	                    pp.toughSurfaceForDebut(race.isMaiden(), race.isTurf()),
	                    pp.fromTopClassToMaidens(race.isMaiden()),
	                    pp.fromStraightMdnToClaimersStrong(race.raceType()),
	                    pp.fromStraightMdnToClaimersWeak(race.raceType()),
	                    horse.topTrainer(),
	                    horse.poorTrainer(),
	                    horse.topJockey(),
	                    horse.poorJockey(),
                        steppingUpInClass,
                        canDoTodaysClass,
                        canDoPastClass,
                        pp.isSharp(raceDay.today()),
                        pp.isDroppingInToLowerCircuit(raceDay.track()),
                        pp.isShippingToHigherCircuit(raceDay.track()),
                        pp.hasSpeedRatingTrend(raceDay.today(), race.surface(), race.distance()),
                        pp.maxSpeedRating(raceDay.today(), race.surface(), race.distance()));

                fastestFraction = pp.getFastestFractions(raceDay.today(), race.surface(), race.distance());

                SQLite.insertHistory(conn, raceNum, postPos, pp.jockey()[0], fastestFraction[0], fastestFraction[1]);

                double[] closingKick = pp.getBestClosingKick(raceDay.today(), race.surface(), race.distance()); 
/*
                if (closingKick[0] != 999.0) {
                    System.out.format("Closing Kick %.1f%n",  closingKick[0]);
                }
                if (closingKick[1] != 999.0) {
                    System.out.format("Normalized Closing Kick %.1f%n",  closingKick[1]);
                }
                if (canDoTodaysClass) {
                    System.out.println("    Can do today's class");
                }

                if (canDoPastClass) {
                    System.out.println("    Can do past class");
                }

                System.out.println ("Turf Bet " + horse.turfBet());
                System.out.println ("Turf Money " + horse.turfMoney());
                if (pp.noSignOfLife()) {
                    System.out.println("No Sign of Life");
                }
                if (pp.backToDirt(race.surface())) {
                    System.out.println("Back to Dirt");
                }
                if (pp.shorteningUp(race.distance())) {
                    System.out.println("Shortening Up");
                }
                if (pp.badDebutPost(race.isMaiden(), postPos)) {
                    System.out.println("Bad Debut Post Up");
                }
                if (pp.debutTooLong(race.isMaiden(), race.distance())) {
                    System.out.println("Debut Too Long");
                }
                if (pp.wellBacked(race.isMaiden() )) {
                    System.out.println("Well Backed");
                }
                if (pp.triedTheDist(race.isMaiden(), race.distance() )) {
                    System.out.println("Tried the Distance");
                }
                if (pp.tooManyChancesToWin(race.isMaiden(), horse.lStarts() )) {
                    System.out.println("Too Many Chances to Win");
                }
                if (pp.toughSurfaceForDebut(race.isMaiden(), race.isTurf() )) {
                    System.out.println("Tough Surface for Debut");
                }
                if (pp.fromTopClassToMaidens(race.isMaiden() )) {
                    System.out.println("From Top Class to Maidens");
                }
                if (pp.fromStraightMdnToClaimersStrong(race.raceType() )) {
                    System.out.println("From Straight Maiden to Claimers Strong");
                }
                if (pp.fromStraightMdnToClaimersWeak(race.raceType() )) {
                    System.out.println("From Straight Maiden to Claimers Weak");
                }
                if (horse.topTrainer()) {
                    System.out.println("Top Trainer");
                }
                if (horse.poorTrainer()) {
                    System.out.println("Poor Trainer");
                }
                if (horse.topJockey()) {
                    System.out.println("Top Jockey");
                }
                if (horse.poorJockey()) {
                    System.out.println("Poor Jockey");
                }
	            if (steppingUpInClass > 0) {
		            System.out.println("Stepping Up in Class");
	            }
                else if (steppingUpInClass < 0) {
		            System.out.println("Dropping Down in Class");
	            }
                if (canDoTodaysClass) {
                    System.out.println("Can do today's class");
                }
                if (canDoPastClass) {
                    System.out.println("Can do past class");
                }
                if (pp.isSharp(raceDay.today())) {
                    System.out.println("Sharp");
                }
                if (pp.isDroppingInToLowerCircuit(raceDay.track())) {
                    System.out.println("Dropping into Lower Circuit");
                }
                if (pp.isShippingToHigherCircuit(raceDay.track())) {
                    System.out.println("Shipping to Higher Circuit");
                }
                if (pp.hasSpeedRatingTrend(raceDay.today(), race.surface(), race.distance())) {
                    System.out.println("Has Speed Rating Trend");
                }
                System.out.println("Max Speed Rating " + pp.maxSpeedRating(raceDay.today(), race.surface(), race.distance()));
*/

                

            } // for postPos ... maxPostPos

            race.speedRatingRank();

            race.jockeyChoice();

            race.loneF();

        } // for raceNum ... maxRace
    }
}
