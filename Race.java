/*
 * The Race object contains track data fora a specific race,
 * such as distance, race number, and surface.
 */

import java.util.List;
import static utils.CSVUtils.getInt;

public class Race{

	private int raceNumber;
	private int distance;
    private int purse;
    private int claimingPrice;
	private String surface;
	private String raceType;
	

	public int getRaceNumber() {
		return raceNumber;
	}

	public int getDistance() {
		return distance;
	}

	public String getSurface() {
		return surface;
	}

	public String getRaceType() {
		return raceType;
	}

    public int getPurse() {
        return purse;
    }

    public int getClaimingPrice() {
        return claimingPrice;
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

	public Race (List<String> line) {
		this.raceNumber = getInt(line.get(2));
		this.distance   = getInt(line.get(5));
		this.surface    = line.get(6);
		this.raceType   = line.get(8);
        this.purse      = getInt(line.get(11));
        this.claimingPrice = getInt(line.get(12));
	}
}
