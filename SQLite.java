import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Scanner;
import utils.CSVUtils;
import static utils.CSVUtils.getInt;
import static utils.CSVUtils.getDouble;

public class SQLite {

    /*
     * Build the database
     */
    public static Connection createNewDatabase() {
        
        Connection conn = null;
        String url = "jdbc:sqlite::memory:";

        try {
            conn = DriverManager.getConnection(url);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    /*
     * Create the Horse table
     */
    public static void createHorseTable(Connection conn) {
        
        String sql = "CREATE TABLE t_horse (\n"
            + " id integer PRIMARY KEY,\n"
            + " track text,\n"
            + " date text,\n"
            + " race integer,\n"
            + " postPos integer,\n"
            + " distance integer,\n"
            + " surface text,\n"
            + " raceType text,\n"
            + " purse integer,\n"
            + " claimingPrice integer,\n"
            + " trainer text,\n"
            + " trainerSts integer,\n"
            + " trainerWins integer,\n"
            + " jockey text,\n"
            + " jockeySts integer,\n"
            + " jockeyWins integer,\n"
            + " name text, \n"
            + " tStarts integer, \n"
            + " tWins integer, \n"
            + " tPlaces integer, \n"
            + " tShows integer, \n"
            + " lStarts integer, \n"
            + " lWins integer, \n"
            + " currYrJockeySts integer,\n"
            + " currYrJockeyWins integer,\n"
            + " highClaimingPrice integer\n"
            + ");";
            

        try {
            Statement  stmt = conn.createStatement();
            stmt.execute(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * Create the Last 10 races table
     */
    public static void createLast10Table(Connection conn) {
        
        String sql = "CREATE TABLE t_last10 (\n"
            + " id integer PRIMARY KEY,\n"
            + " ppRace integer,\n"  
            + " race integer,\n"
            + " todaysPostPos integer,\n"
            + " date integer,\n"
            + " track text,\n"
            + " distance integer,\n"
            + " surface text,\n"
            + " postPos integer,\n"
            + " odds real,\n"
            + " claimingPrice integer,\n"
            + " purse integer,\n"
            + " pp1stCallPos text,\n"
            + " pp2ndCallPos text,\n"
            + " gateCallPos text,\n"
            + " stretchPos text,\n"
            + " finishPos text,\n"
            + " pp1stCallBtn real,\n"
            + " pp2ndCallBtn real,\n"
            + " stretchBtn real,\n"
            + " finishBtn real,\n"
            + " speedRating integer,\n"
            + " pp2fFraction real,\n"
            + " pp4fFraction real,\n"
            + " pp6fFraction real,\n"
            + " pp8fFraction real,\n"
            + " pp10fFraction real,\n"
            + " pp12fFraction real,\n"
            + " pp14fFraction real,\n"
            + " pp16fFraction real,\n"
            + " finalTime real,\n"
            + " jockey text,\n"
            + " raceType text,\n"
            + " highClaimingPrice integer\n"
            + ");";

        try {
            Statement  stmt = conn.createStatement();
            stmt.execute(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * Create the Flags table
     */
    public static void createFlagsTable(Connection conn) {
        
        String sql = "CREATE TABLE t_flags (\n"
            + " id integer PRIMARY KEY,\n"
            + " race integer,\n"
            + " postPos integer,\n"
            + " turfBet text,\n"
            + " turfMoney text,\n"
            + " noSignOfLife integer,\n"
            + " backToDirt integer,\n"
            + " shorteningUp integer,\n"
            + " badDebutPost integer,\n"
            + " debutTooLong integer,\n"
            + " wellBacked integer,\n"
            + " triedTheDistance integer,\n"
            + " tooManyChancesToWin integer,\n"
            + " toughSurfaceForDebut integer,\n"
            + " fromTopClassToMaidens integer,\n"
            + " fromStraigtMaidenToClaimersStrong integer,\n"
            + " fromStraigtMaidenToClaimersWeak integer,\n"
            + " topTrainer integer,\n"
            + " poorTrainer integer,\n"
            + " topJockey integer,\n"
            + " poorJockey integer,\n"
            + " steppingUpInClass integer,\n"
            + " canDoTodaysClass integer,\n"
            + " canDoPastClass integer,\n"
            + " sharp integer,\n"
            + " droppingInToLowerCircuit integer,\n"
            + " shippingToHigherCircuit integer,\n"
            + " speedRatingTrend integer,\n"
            + " maxSpeedRating integer,\n"
            + " jockeyChoicePlus integer,\n"
            + " jockeyChoiceMinus integer\n"
            + ");";

        try {
            Statement  stmt = conn.createStatement();
            stmt.execute(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * Create the History table
     */
    public static void createHistoryTable(Connection conn) {
        
        String sql = "CREATE TABLE t_history (\n"
            + " id integer PRIMARY KEY,\n"
            + " race integer,\n"
            + " postPos integer,\n"
            + " priorJockey text,\n"
            + " fastestFraction1 double,\n"
            + " fastestFraction2 double\n"
            + ");";
            

        try {
            Statement  stmt = conn.createStatement();
            stmt.execute(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * Read the CSV file and load the database
     */
    public static void loadCSV (Connection conn, String csvFile) {
		try {
			Scanner scanner = new Scanner (new File(csvFile));
			while (scanner.hasNext()) {
				List<String> line = CSVUtils.parseLine(scanner.nextLine());
                insertHorse(conn, line);
                for (int index=0; index<10; index++) {
                    insertLast10(conn, line, index);
                }
            }
	    } 

		catch (FileNotFoundException e) {
			System.out.println ("File not found " + e.toString());
		}
    }

    /*
     * Insert data into the Horse table
     */
    public static void insertHorse (Connection conn, List<String> line) {
        String sql = "INSERT INTO t_horse (id, track, date, race, postPos, distance, " + 
                     "surface, raceType, purse, claimingPrice, trainer, " +
                     "trainerSts, trainerWins, jockey, jockeySts, jockeyWins, " +
                     "name, tStarts, tWins, tPlaces, tShows, lStarts, " +
                     "lWins, currYrJockeySts, currYrJockeyWins, highClaimingPrice) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt   (1, getInt(line.get(2)) * 100 + getInt(line.get(3))); //race*100 + postPos
            pstmt.setString(2, line.get(0));             //track
            pstmt.setInt   (3, getInt(line.get(1)));     //date
            pstmt.setInt   (4, getInt(line.get(2)));     //race 
            pstmt.setInt   (5, getInt(line.get(3)));     //postPos
            pstmt.setInt   (6, Math.abs(getInt(line.get(5))));     //distance
            pstmt.setString(7, line.get(6));             //surface);
            pstmt.setString(8, line.get(8));             //raceType
            pstmt.setInt   (9, getInt(line.get(11)));    //purse
            pstmt.setInt   (10, getInt(line.get(12)));   //claimingPrice
            pstmt.setString(11, line.get(27));           //trainer
            pstmt.setInt   (12, getInt(line.get(28)));   //trainerSts
            pstmt.setInt   (13, getInt(line.get(29)));   //trainerWins
            pstmt.setString(14, line.get(32));           //jockey
            pstmt.setInt   (15, getInt(line.get(34)));   //jockeySts);
            pstmt.setInt   (16, getInt(line.get(35)));   //jockeyWins
            pstmt.setString(17, line.get(44));           //name
            pstmt.setInt   (18, getInt(line.get(74)));   //tStarts
            pstmt.setInt   (19, getInt(line.get(75)));   //tWins
            pstmt.setInt   (20, getInt(line.get(76)));   //tPlaces
            pstmt.setInt   (21, getInt(line.get(77)));   //tShows
            pstmt.setInt   (22, getInt(line.get(96)));   //lStarts
            pstmt.setInt   (23, getInt(line.get(97)));   //lWins
            pstmt.setInt   (24, getInt(line.get(1156))); //currYrJockeySts
            pstmt.setInt   (25, getInt(line.get(1157))); //currYrJockeyWins
            pstmt.setInt   (26, getInt(line.get(1211))); //highClaimingPrice
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * Insert data into the Flags table
     */
    public static void insertFlags (Connection conn, int race, int postPos, 
            String turfBet, String turfMoney, boolean noSignOfLife, boolean backToDirt,
            boolean shorteningUp, boolean badDebutPost, boolean debutTooLong, boolean wellBacked,
            boolean triedTheDistance, boolean tooManyChancesToWin, boolean toughSurfaceForDebut, 
            boolean fromTopClassToMaidens, boolean fromStraigtMaidenToClaimersStrong, boolean fromStraigtMaidenToClaimersWeak,
            boolean topTrainer, boolean poorTrainer, boolean topJockey, boolean poorJockey,
            int steppingUpInClass, boolean canDoTodaysClass, boolean canDoPastClass, boolean sharp, boolean droppingInToLowerCircuit,
            boolean shippingToHigherCircuit, boolean speedRatingTrend, int maxSpeedRating) { 
        String sql = "INSERT INTO t_flags (id, race, postPos, turfBet, turfMoney, noSignOfLife, backToDirt, " +
                     "shorteningUp, badDebutPost, debutTooLong, wellBacked, " +
                     "triedTheDistance, tooManyChancesToWin, toughSurfaceForDebut, " +
                     "fromTopClassToMaidens, fromStraigtMaidenToClaimersStrong, fromStraigtMaidenToClaimersWeak, " +
                     "topTrainer, poorTrainer, topJockey, poorJockey, " +
                     "steppingUpInClass, canDoTodaysClass, canDoPastClass, sharp, droppingInToLowerCircuit, " +
                     "shippingToHigherCircuit, speedRatingTrend, maxSpeedRating, jockeyChoicePlus, jockeyChoiceMinus) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, race * 100 + postPos);
            pstmt.setInt(2, race);
            pstmt.setInt(3, postPos);
            pstmt.setString(4, turfBet);
            pstmt.setString(5, turfMoney);
            pstmt.setInt(6, boolToInt(noSignOfLife));
            pstmt.setInt(7, boolToInt(backToDirt));
            pstmt.setInt(8, boolToInt(shorteningUp));
            pstmt.setInt(9, boolToInt(badDebutPost));
            pstmt.setInt(10, boolToInt(debutTooLong));
            pstmt.setInt(11, boolToInt(wellBacked));
            pstmt.setInt(12, boolToInt(triedTheDistance));
            pstmt.setInt(13, boolToInt(tooManyChancesToWin));
            pstmt.setInt(14, boolToInt(toughSurfaceForDebut));
            pstmt.setInt(15, boolToInt(fromTopClassToMaidens));
            pstmt.setInt(16, boolToInt(fromStraigtMaidenToClaimersStrong));
            pstmt.setInt(17, boolToInt(fromStraigtMaidenToClaimersWeak));
            pstmt.setInt(18, boolToInt(topTrainer));
            pstmt.setInt(19, boolToInt(poorTrainer));
            pstmt.setInt(20, boolToInt(topJockey));
            pstmt.setInt(21, boolToInt(poorJockey));
            pstmt.setInt(22, steppingUpInClass);
            pstmt.setInt(23, boolToInt(canDoTodaysClass));
            pstmt.setInt(24, boolToInt(canDoPastClass));
            pstmt.setInt(25, boolToInt(sharp));
            pstmt.setInt(26, boolToInt(droppingInToLowerCircuit));
            pstmt.setInt(27, boolToInt(shippingToHigherCircuit));
            pstmt.setInt(28, boolToInt(speedRatingTrend));
            pstmt.setInt(29, maxSpeedRating);
            pstmt.setInt(30, 0);
            pstmt.setInt(31, 0);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * Insert data into the Last 10 table
     */
    public static void insertLast10 (Connection conn, List<String> line, int ppRace) {
        String sql = "INSERT INTO t_last10 (id, ppRace, race, todaysPostPos, date, " +
                     "track, distance, surface, postPos, odds, claimingPrice, purse, " +
                     "pp1stCallPos, pp2ndCallPos, gateCallPos, stretchPos, finishPos, " +
                     "pp1stCallBtn, pp2ndCallBtn, stretchBtn, finishBtn, speedRating, " +
                     "pp2fFraction, pp4fFraction, pp6fFraction, pp8fFraction, pp10fFraction, " +
                     "pp12fFraction, pp14fFraction, pp16fFraction, finalTime, jockey, raceType, " +
                     "highClaimingPrice) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt   (1, getInt(line.get(2)) * 1000 + getInt(line.get(3))*10 + ppRace); //race*1000 + postPos*100 + index
            pstmt.setInt   (2, ppRace);                           //which past race/index
            pstmt.setInt   (3, getInt(line.get(2)));              //race 
            pstmt.setInt   (4, getInt(line.get(3)));              //today's postPos
            pstmt.setInt   (5, getInt(line.get(255+ppRace)));     //date
            pstmt.setString(6, line.get(275+ppRace));             //track
            pstmt.setInt   (7, Math.abs(getInt(line.get(315+ppRace))));     //distance
            pstmt.setString(8, line.get(325+ppRace));             //surface);
            pstmt.setInt   (9, getInt(line.get(355+ppRace)));     //postPos
            pstmt.setDouble(10, getDouble(line.get(515+ppRace))); //odds
            pstmt.setInt   (11, getInt(line.get(545+ppRace)));    //claimingPrice
            pstmt.setInt   (12, getInt(line.get(555+ppRace)));    //purse
            pstmt.setString(13, line.get(575+ppRace));            //pp1stCallPos
            pstmt.setString(14, line.get(585+ppRace));            //pp2ndCallPos
            pstmt.setString(15, line.get(595+ppRace));            //gateCallPos
            pstmt.setString(16, line.get(605+ppRace));            //stretchPos
            pstmt.setString(17, line.get(615+ppRace));            //finishPos
            pstmt.setString(18, line.get(665+ppRace));            //pp1stCallBtn
            pstmt.setString(19, line.get(685+ppRace));            //pp2ndCallBtn
            pstmt.setString(20, line.get(725+ppRace));            //stretchBtn
            pstmt.setString(21, line.get(745+ppRace));            //finishBtn
            pstmt.setInt   (22, getInt(line.get(845+ppRace)));    //speedRating
            pstmt.setDouble(23, getDouble(line.get(875+ppRace))); //pp2fFraction
            pstmt.setDouble(24, getDouble(line.get(895+ppRace))); //pp4fFraction
            pstmt.setDouble(25, getDouble(line.get(915+ppRace))); //pp6fFraction
            pstmt.setDouble(26, getDouble(line.get(935+ppRace))); //pp8fFraction
            pstmt.setDouble(27, getDouble(line.get(945+ppRace))); //pp10fFraction
            pstmt.setDouble(28, getDouble(line.get(955+ppRace))); //pp12fFraction
            pstmt.setDouble(29, getDouble(line.get(965+ppRace))); //pp14fFraction
            pstmt.setDouble(30, getDouble(line.get(975+ppRace))); //pp16fFraction
            pstmt.setDouble(31, getDouble(line.get(1035+ppRace)));//finalTime
            pstmt.setString(32, line.get(1065+ppRace));           //jockey
            pstmt.setString(33, line.get(1085+ppRace));           //raceType
            pstmt.setInt   (34, getInt(line.get(1211+ppRace)));   //highClaimingPrice
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /*
     * Insert data into the History table
     */
    public static void insertHistory (Connection conn, int race, int postPos, 
            String priorJockey, double fastestFraction1,  double fastestFraction2) {
        String sql = "INSERT INTO t_history (id, race, postPos, priorJockey, fastestFraction1, fastestFraction2) " +
                     "VALUES (?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt   (1, race * 100 + postPos);
            pstmt.setInt   (2, race);
            pstmt.setInt   (3, postPos);
            pstmt.setString(4, priorJockey);
            pstmt.setDouble(5, fastestFraction1);
            pstmt.setDouble(6, fastestFraction2);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /*
     * Update Jockey Choice
     */
    public static void updateJockeyChoice (Connection conn, int race, int postPos, boolean jockeyChoicePlus, boolean jockeyChoiceMinus) {
        String sql = "UPDATE t_flags SET jockeyChoicePlus = ?, jockeyChoiceMinus = ? WHERE id=?"; 

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, boolToInt(jockeyChoicePlus));
            pstmt.setInt(2, boolToInt(jockeyChoiceMinus));
            pstmt.setInt(3, race * 100 + postPos);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String[] getCurrentJockies (Connection conn, int race) {
        String[] currJockies = new String[20];
        int i = 0;

        String sql = "SELECT jockey FROM t_horse WHERE race = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt (1, race);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                currJockies[i++] = rs.getString("jockey");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return currJockies;
    }

    public static double[][] getFractions (Connection conn, int race, int maxPostPos) {
        double[][] fractions = new double[2][maxPostPos];

        String sql = "SELECT postPos, fastestFraction1, fastestFraction2 FROM t_history WHERE race = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt (1, race);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                fractions[0][rs.getInt("postPos")-1] = rs.getDouble("fastestFraction1");
                fractions[1][rs.getInt("postPos")-1] = rs.getDouble("fastestFraction2");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return fractions;
    }

    public static String[] getPriorJockies (Connection conn, int race) {
        String[] priorJockies = new String[20];
        int i = 0;

        String sql = "SELECT priorJockey FROM t_history WHERE race = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt (1, race);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                priorJockies[i++] = rs.getString("priorJockey");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return priorJockies;
    }

    public static int[] getBestSpeedRating (Connection conn, int race) {
        int[] bestRatings = new int[20];
        int i = 0;

        String sql = "SELECT maxSpeedRating FROM t_flags WHERE race = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt (1, race);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bestRatings[i++] = rs.getInt("maxSpeedRating");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return bestRatings;
    }

    public static void getHorse (Connection conn, int race, int postPos) {
        String sql = "SELECT name, backToDirt, shorteningUp FROM t_horses WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt (1, race * 100 + postPos);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println ("The horse in post position " + postPos + " in race " + race + " is " + rs.getString("name"));
                System.out.println ("backToDirt=" + intToBool(rs.getInt("backToDirt")) + "  shorteningUp=" + intToBool(rs.getInt("shorteningUp")));

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static int getMaxRace (Connection conn) {
        String sql = "SELECT MAX(race) FROM t_horse";
        int maxRace = 0;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                maxRace = rs.getInt("MAX(race)");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return maxRace;
    }

    public static int getMaxPost (Connection conn, int race) {
        String sql = "SELECT MAX(postPos) FROM t_horse WHERE race = ?";
        int maxPost = 0;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt (1, race);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                maxPost = rs.getInt("MAX(postPos)");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return maxPost;
    }

    public static int getMaxHist (Connection conn, int race, int postPos) {
        String sql = "SELECT distance FROM t_last10 WHERE race = ? and todaysPostPos = ?";
        int maxHist = 0;
        int distance = 0;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt (1, race);
            pstmt.setInt (2, postPos);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                distance = rs.getInt("distance");
                if (distance != 0) {
                    maxHist++;
                }
                else {
                    break;
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return maxHist;
    }

    /*
     * The database doesn't support boolean, so convert between boolean and int
     */
    private static int boolToInt (boolean boolVal) {
        int intVal = boolVal? 1 : 0;
        return intVal;
    }

    private static boolean intToBool (int intVal) {
        boolean boolVal = (intVal == 1)? true : false;
        return boolVal;
    }

}
