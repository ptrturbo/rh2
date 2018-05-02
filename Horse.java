/*
 * The Horse object contains data specific to a horse for a particular race
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Horse {

    private Connection conn;
	private int postPos;
	private String trainer;
	private int trainerSts;
	private int trainerWins;
	private String jockey;
	private int jockeySts;
	private int jockeyWins;
	private String name;
	private int tStarts;
	private int tWins;
	private int tPlaces;
	private int tShows;
	private int lStarts;
    private int raceNum;

	public String trainer() {
		return trainer;
	}

	public int trainerSts() {
		return trainerSts;
	}

	public int trainerWins() {
		return trainerWins;
	}

	public String jockey() {
		return jockey;
	}

	public int jockeySts() {
		return jockeySts;
	}

	public int jockeyWins() {
		return jockeyWins;
	}

	public String name() {
		return name;
	}

	public int tStarts() {
		return tStarts;
	}

	public int tWins() {
		return tWins;
	}

	public int tPlaces() {
		return tPlaces;
	}

	public int tShows() {
		return tShows;
	}

	public int lStarts() {
		return lStarts;
	}

    public int maxHist () {
        return SQLite.getMaxHist(conn, raceNum, postPos);
    }

/*
 * Rate the quality of the horse for turf races
 */ 
    public String turfBet () {
	   String tWhat = "void";
       double winRatio = 0;
       double itmRatio = 0;

       if (tStarts !=0) {
           winRatio = (double)(tWins/tStarts);
           itmRatio = (double)((tWins + tPlaces + tShows) / tStarts);
       }

	   // If no prior turf races, the bet is unknown
	   if (tStarts == 0) {
		   tWhat = "T-Unk";
	   }
	   // If it has a 15% or better win ratio, definitely yes
	   else	if (winRatio >= 0.15) {
		    tWhat = "T-Yes";
	   }

	   // If it has 1, 2, or 3 races and has finished at least once in the money, probably yes
	   else if (tStarts < 4) {
		  if (itmRatio >= 0.5) {
		   	tWhat = "T-ProbYes";
		  }
		  // If it has raced 1-4 times but not finished in the money, probably no
		  else {
		   	tWhat = "T-ProbNo";
		  }
	   }
	   // If it has 4 or more turf races 
	   else {
		   tWhat = "T-No";
	   }

	   // If we missed something, print out the data for debugging
	   if (tWhat.equals("void")) { System.out.println("==> Starts:" + tStarts + " Wins:" + tWins + " Places:" + tPlaces + " Shows:" + tShows);}
	   return tWhat;
    }

/*
 * How much in-the-money is this horse in a turf race
 */
    public String turfMoney () {
	    String inTheMoney = "T-InThe$No";
	    double itmRate;
	    if (tStarts > 0) { // Don't divide by zero
		    itmRate = (double)(tWins + tPlaces + tShows) / tStarts;
	    	if (itmRate >= 0.5) {
		        inTheMoney = "T-InThe$Yes";
		    }
		else if (itmRate >= 0.33) {
		    inTheMoney = "T-InThe$Weak";
		    }
	    }
	    return inTheMoney;
    }

/*
 * Top Trainer
 */
    public boolean topTrainer () {
	    boolean result = false;
	    if (trainerSts>0 && (double)trainerWins/(double)trainerSts >= 0.18) {
		    result = true;
	    }
	    return result;
    }

/*
 * Poor Trainer
 */
    public boolean poorTrainer () {
	    boolean result = false;
	    if (trainerSts>0 && (double)trainerWins/(double)trainerSts <= 0.05) {
		    result = true;
	    }
	    return result;
    }

/*
 * Jockey Win Ratio
 */
    public double jockeyWinRatio() {
	    double ratio = 0.0;
	    if (jockeySts>0) {
            ratio = (double)jockeyWins/(double)jockeySts; 
	    }
	    return ratio;
    }

/*
 * Top Jockey
 */
    public boolean topJockey () {
	    boolean result = false;
	    if (jockeyWinRatio() >= 0.18) {
		    result = true;
	    }
	    return result;
    }

/*
 * Poor Jockey
 */
    public boolean poorJockey () {
	    boolean result = false;
	    if (jockeyWinRatio() <= 0.05) {
		    result = true;
	    }
	    return result;
    }

/*
 * Horse constructor
 */
    public Horse (Connection conn, int raceNum, int postPos) {
        String sql = "SELECT trainer, trainerSts, trainerWins, jockey, jockeySts, jockeyWins, " +
                     "name, tStarts, tWins, tPlaces, tShows, lstarts " +
                     "FROM t_horse WHERE race = ? and postPos = ?"; 

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt (1, raceNum);
            pstmt.setInt (2, postPos);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                this.trainer     = rs.getString("trainer");
		        this.trainerSts  = rs.getInt("trainerSts");
		        this.trainerWins = rs.getInt("trainerWins");
		        this.jockey      = rs.getString("jockey");
		        this.jockeySts   = rs.getInt("jockeySts"); 
		        this.jockeyWins  = rs.getInt("jockeyWins");
		        this.name        = rs.getString("name");
		        this.tStarts     = rs.getInt("tStarts");
		        this.tWins       = rs.getInt("tWins");
		        this.tPlaces     = rs.getInt("tPlaces");
		        this.tShows      = rs.getInt("tShows");
		        this.lStarts     = rs.getInt("lStarts");
            }
            this.conn    = conn;
            this.raceNum = raceNum;
            this.postPos = postPos;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
