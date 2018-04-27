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
		int	currRace = 0;
        int postPos = 0;
        int lStarts = 0;
        int today = 0;
        String jockey = "";
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
        System.out.println ("Racing on " + raceDay.getDate() + " at " + raceDay.getTrack());	

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
                Horse horse = new Horse(conn, raceNum, postPos);

                for (int histRace=1; histRace<=horse.maxHist(); histRace++) {
                    PastPerf pp = new PastPerf(conn, raceNum, postPos, histRace);
                } // for histRace ... maxHist

            } // for postPos ... maxPostPos

        } // for raceNum ... maxRace
    }
}
