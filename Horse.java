/*
 * The Horse object contains data specific to a horse for a particular race
 */

import java.util.List;
import static utils.CSVUtils.getInt;

public class Horse {

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

	public int getPostPos() {
		return postPos;
	}

	public String getTrainer() {
		return trainer;
	}

	public int getTrainerSts() {
		return trainerSts;
	}

	public int getTrainerWins() {
		return trainerWins;
	}

	public String getJockey() {
		return jockey;
	}

	public int getJockeySts() {
		return jockeySts;
	}

	public int getJockeyWins() {
		return jockeyWins;
	}

	public String getName() {
		return name;
	}

	public int getTStarts() {
		return tStarts;
	}

	public int getTWins() {
		return tWins;
	}

	public int getTPlaces() {
		return tPlaces;
	}

	public int getTShows() {
		return tShows;
	}

	public int getLStarts() {
		return lStarts;
	}

/*
 * Rate the quality of the horse for turf races
 */ 
    public String getTurfBet () {
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
    public String getTurfMoney () {
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
	public Horse (List<String> line) {
		this.postPos     = getInt(line.get(3));
		this.trainer     = line.get(27);
		this.trainerSts  = getInt(line.get(1146)); 
		this.trainerWins = getInt(line.get(1147)); 
		this.jockey      = line.get(32);
		this.jockeySts   = getInt(line.get(1156)); //Current year. Could use current meet (field 35) or prior year (1162) instead
		this.jockeyWins  = getInt(line.get(1157)); //Same for Wins
		this.name        = line.get(44);
		this.tStarts     = getInt(line.get(74));
		this.tWins       = getInt(line.get(75));
		this.tPlaces     = getInt(line.get(76));
		this.tShows      = getInt(line.get(77));
		this.lStarts     = getInt(line.get(96));
	}
}
