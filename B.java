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
        SQLite.createLast10Table(conn);
        SQLite.createHistoryTable(conn);
        
        // Load the CSV file into the database
        SQLite.loadCSV(conn, csvFile);

        raceDay = new Raceday (conn);
        System.out.println ("Racing on " + raceDay.getDate() + " at " + raceDay.getTrack());	

        int maxRace = SQLite.getMaxRace(conn);
        for (int race=1; race<=maxRace; race++) {
            int maxPost = SQLite.getMaxPost(conn, race);
            System.out.println("Race " + race + "  max post " + maxPost);
        }
    }
}
