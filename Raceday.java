/*
 * One race day object per day, 
 * containing the date and the name of the race track
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Raceday{

	private String track;
	private int    date;
    private Connection conn;

	public String track() {
		return track;
	}

	public int today() {
		return date;
	}

    public int maxRace() {
        return SQLite.getMaxRace(conn);
    }

    public Raceday (Connection conn) {
        String sql = "SELECT date, track FROM t_horse WHERE race = 1 and postPos = 1"; //Same track and day for any horse and race

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                this.date = rs.getInt("date");
                this.track = rs.getString("track");
            }
            this.conn = conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
