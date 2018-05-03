/*
 * The Race object contains track data fora a specific race,
 * such as distance, race number, and surface.
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Race{

	private int distance;
    private int purse;
    private int claimingPrice;
    private int raceNum;
	private String surface;
	private String raceType;
    private Connection conn;
	
	public int distance() {
		return distance;
	}

	public String surface() {
		return surface;
	}

	public String raceType() {
		return raceType;
	}

    public int purse() {
        return purse;
    }

    public int raceNum() {
        return raceNum;
    }

    public int claimingPrice() {
        return claimingPrice;
    }

    public int maxPost() {
        return SQLite.getMaxPost(conn, raceNum);
    }

    public boolean isMaiden() {
        boolean maiden = false;
        if (raceType.equals("M") ||
            raceType.equals("MO") ||
            raceType.equals("S")) {
            maiden = true;
        }
        return maiden;
    }

    public boolean isTurf() {
        boolean turf = false;
        if (surface.toUpperCase().equals("T")) {
            turf = true;
        }
        return turf;
    }

/*
 * Get three best speed rated horses
 */
    public void speedRatingRank() {
        int[] speedRatings = new int[maxPost()];
        int[] postPos = new int[maxPost()];
        int tempr;
        int temph;

        speedRatings = SQLite.getBestSpeedRating(conn, raceNum);
        for (int i=0; i<maxPost(); i++) {
            postPos[i] = i+1;
        }
        
        //Sort the two lists
        for (int i=1; i<maxPost(); i++) {
            for (int k=i; k>0 && speedRatings[k-1]>speedRatings[k]; k--) {
                tempr = speedRatings[k];
                temph = postPos[k];
                speedRatings[k] = speedRatings[k-1];
                postPos[k] = postPos[k-1];
                speedRatings[k-1] = tempr;
                postPos[k-1] = temph;
            }
        }

        for (int i=maxPost()-1; i>maxPost()-4; i--) {
            System.out.println("Speed:" + speedRatings[i] + "  PostPos:" + postPos[i]);
            //SQLite.insertSpeedRatingRank(conn, raceNum, speedRatings, postPos);
        }
    }


/*
 * Get Jockey Choice Plus and Minus
 */
    public void jockeyChoice() {

        String[] currJockey  = new String[maxPost()];
        String[] priorJockey = new String[maxPost()];
        boolean jockeyChoicePlus;
        boolean jockeyChoiceMinus;
        int jockeyMatch = 0;

        //Get list of jockies for this race
        currJockey = SQLite.getCurrentJockies(conn, raceNum);

        //Get list of prior race jockies
        priorJockey = SQLite.getPriorJockies(conn, raceNum);
        
        for (int postPos=0; postPos<maxPost(); postPos++) {
            jockeyMatch = 0;
            
            // How many of today's horses has today's jockey ridden in prior races
            for (int j=0; j<maxPost(); j++) {
                if (currJockey[postPos].equals(priorJockey[j])) {
                    jockeyMatch++;
                }
            }

            if (jockeyMatch > 1) { //if had ridden 2 or more prior horses
                jockeyChoicePlus = false;
                jockeyChoiceMinus = false;

                if (currJockey[postPos].equals(priorJockey[postPos])) { // if today's jockey was this horse's prior jockey
                    jockeyChoicePlus = true;
                    System.out.println("Plus " + (postPos+1));
                    SQLite.updateJockeyChoice (conn, raceNum, postPos, jockeyChoicePlus, jockeyChoiceMinus);

                    for (int pPos=0; pPos<maxPost(); pPos++) { 
                        if (currJockey[postPos].equals(priorJockey[pPos]) && (postPos != pPos)) { 
                            jockeyChoiceMinus = true;
                    System.out.println("Minus " + (pPos+1));
                            SQLite.updateJockeyChoice (conn, raceNum, pPos, jockeyChoicePlus, jockeyChoiceMinus);
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
    public void loneF() {
        double[][] fractions = new double[2][maxPost()];
        double[] fastest = {100.0, 100.0};
        double[] second  = {100.0, 100.0};
        int validHorses = 0;
        int fastHorse = 0;;

        //Find the fastest and second fastest times
        fractions = SQLite.getFractions(conn, raceNum, maxPost());
        for (int postPos=0; postPos<maxPost(); postPos++) {
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

        if (validHorses >= maxPost()/2) {
            if (fastest[0] - second[0] >= 0.6) {
                System.out.format("Lone F - horse %d  %.1f  %.1f %n", (fastHorse+1), fastest[0], fastest[1]);
            }
        }
    } //loneF

    /*
     * Constructor
     */
	public Race (Connection conn, int raceNum) {
        String sql = "SELECT distance, surface, raceType, purse, claimingPrice " +
                     "FROM t_horse WHERE race = ? and postPos = 1"; //Same info for any horse in the race 

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, raceNum);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                this.distance      = rs.getInt("distance");
                this.surface       = rs.getString("surface");
                this.raceType      = rs.getString("raceType");
                this.purse         = rs.getInt("purse");
                this.claimingPrice = rs.getInt("claimingPrice");
            }
            this.raceNum = raceNum;
            this.conn    = conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
}
