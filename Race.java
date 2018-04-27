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
