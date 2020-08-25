import uk.ac.warwick.dcs.maze.logic.IRobot;

public class Ex2 {
	private int explorerMode;
	private int pollRun = 0; //Incremented after each pass
	private RobotData rD = new RobotData(); //Data store for junctions
	
	public void exploreControl (IRobot robot) { //Control for explorer mode
		int direction = 0;
		int exits = nonwallExits(robot);
		switch (exits) { //Returns method based on exit number
			case 4: direction = junctionorcross(robot); break;
			case 3: direction = junctionorcross(robot); break;
			case 2: direction = corridor(robot); break;
			case 1: direction = deadend(robot); break;
		}
		robot.face(direction);
	}
	
	public void backtrackControl(IRobot robot) { //Control for backtrack mode
		int returnvalue = IRobot.AHEAD; //Initialise value to return a direction
		int exits = nonwallExits(robot); // Similar to exploreControl
		int passageexit = passageExits(robot); 
		
		if (exits > 2) { //When at a junction or crossroads.
			if (passageexit == 0) { //If all exits have been explored, robot must return through heading it first came through the junction
				
				int heading = robot.getHeading();
				int firstarrived = rD.searchJunction(robot);
				int correctHeading = 0;
				
				switch(firstarrived) { //Returns heading opposite to heading when robot first passed through
					case IRobot.NORTH: correctHeading = IRobot.SOUTH; break;
					case IRobot.SOUTH: correctHeading = IRobot.NORTH; break;
					case IRobot.EAST: correctHeading = IRobot.WEST; break;
					case IRobot.WEST: correctHeading = IRobot.EAST; break;
				}
				
				if (correctHeading == IRobot.NORTH) { //Switch statements with directions to turn depending on correctHeading
					switch (heading) {
						case IRobot.NORTH : returnvalue = IRobot.AHEAD; break;
						case IRobot.SOUTH : returnvalue = IRobot.BEHIND; break;
						case IRobot.EAST : returnvalue = IRobot.LEFT; break;
						case IRobot.WEST : returnvalue = IRobot.RIGHT; break;
					}
				}
				
				if (correctHeading == IRobot.SOUTH) {
					switch (heading) {
						case IRobot.NORTH : returnvalue = IRobot.BEHIND; break;
						case IRobot.SOUTH : returnvalue = IRobot.AHEAD; break;
						case IRobot.EAST : returnvalue = IRobot.RIGHT; break;
						case IRobot.WEST : returnvalue = IRobot.LEFT; break;
					}
				}
				
				if (correctHeading == IRobot.EAST) {
					switch (heading) {
						case IRobot.NORTH : returnvalue = IRobot.RIGHT; break;
						case IRobot.SOUTH : returnvalue = IRobot.LEFT; break;
						case IRobot.EAST : returnvalue = IRobot.AHEAD; break;
						case IRobot.WEST : returnvalue = IRobot.BEHIND; break;
					}
				}
				
				if (correctHeading == IRobot.WEST) {
					switch (heading) {
						case IRobot.NORTH : returnvalue = IRobot.LEFT; break;
						case IRobot.SOUTH : returnvalue = IRobot.RIGHT; break;
						case IRobot.EAST : returnvalue = IRobot.BEHIND; break;
						case IRobot.WEST : returnvalue = IRobot.AHEAD; break;
					}
				}
				robot.face(returnvalue);
			}
			else { //If there are unexplored passages in a junction, the robot switches to explorer mode
				explorerMode = 1;
				exploreControl(robot);
			}
		}
		else if (exits == 2) { //When in a corridor
			returnvalue = corridor(robot);
			robot.face(returnvalue);
		}
		else if (exits == 1) { //When at a deadend
			returnvalue = deadend(robot);
			robot.face(returnvalue);
		}
	}
	
	public void controlRobot(IRobot robot) {
		//On the first move of the first run of a new maze
		if ((robot.getRuns() == 0) && (pollRun == 0)) {
			rD = new RobotData(); //reset the data store
			explorerMode = 1; //Set intial mode to explorer
			pollRun++; //Increment pollRun so that the data is not
					   //reset each time the robot moves
		}
		if (explorerMode == 1) { //So explorer mode is engaged
			exploreControl(robot);
		}
		else { //So backtrack mode is engaged
			backtrackControl(robot); 
		}
	}
	
	private int nonwallExits (IRobot robot) { //Returns number of non wall exits
		int opennumber = 4;
		if (robot.look(IRobot.AHEAD) == IRobot.WALL) {opennumber -= 1;}
		if (robot.look(IRobot.LEFT) == IRobot.WALL) {opennumber -= 1;}
		if (robot.look(IRobot.RIGHT) == IRobot.WALL) {opennumber -= 1;}
		if (robot.look(IRobot.BEHIND) == IRobot.WALL) {opennumber -= 1;}
		return opennumber;
	}

	private int passageExits (IRobot robot) { //Returns number of passage exits
		int passagenumber = 0;
		if (robot.look(IRobot.AHEAD) == IRobot.PASSAGE) {passagenumber += 1;}
		if (robot.look(IRobot.LEFT) == IRobot.PASSAGE) {passagenumber += 1;}
		if (robot.look(IRobot.RIGHT) == IRobot.PASSAGE) {passagenumber += 1;}
		if (robot.look(IRobot.BEHIND) == IRobot.PASSAGE) {passagenumber += 1;}
		return passagenumber;
	}	
	
	private int beenbeforeExits (IRobot robot) { //Returns number of exits passed through before
		int beenbeforenumber = 0;
		if (robot.look(IRobot.AHEAD) == IRobot.BEENBEFORE) {beenbeforenumber += 1;}
		if (robot.look(IRobot.LEFT) == IRobot.BEENBEFORE) {beenbeforenumber += 1;}
		if (robot.look(IRobot.RIGHT) == IRobot.BEENBEFORE) {beenbeforenumber += 1;}
		if (robot.look(IRobot.BEHIND) == IRobot.BEENBEFORE) {beenbeforenumber += 1;}
		return beenbeforenumber;
	}
	
	private int deadend (IRobot robot)	//Chooses direction at a deadend
	{
		explorerMode = 0; //Robot set to backtrack from the deadend
        int direction;
		do{
			double randno = Math.random();
			if (randno < 0.25) {
				direction = IRobot.LEFT;
			}
			else if (randno < 0.5) {
				direction = IRobot.RIGHT;
			}
			else if (randno < 0.75) {
				direction = IRobot.AHEAD;
			}
			else {
				direction = IRobot.BEHIND;
			}
		} while (robot.look(direction) == IRobot.WALL);
		return direction;
	}
	
	private int corridor (IRobot robot) { //Chooses direction in corridors
		if (robot.look(IRobot.AHEAD) == IRobot.WALL) {
			if (robot.look(IRobot.LEFT) == IRobot.WALL) {
				return IRobot.RIGHT;
			}
			else {
				return IRobot.LEFT;
			}
		}
		else {
			return IRobot.AHEAD;
		}
	}
	
	private int junctionorcross (IRobot robot) { //Chooses direction for junctions and crossroads, also handles storing of junctions and backtracking once a junction is fully explored
		if (beenbeforeExits(robot) == 1) { //Records junction/crossroads and increments counter if this is the first pass through the junction
			rD.recordJunction(robot);
		}
		if (passageExits(robot) == 0) {
			explorerMode = 0; //Switch to backtrack mode if no passage exits
			backtrackControl(robot);
		}
		int juncdirection;
		if (passageExits(robot) != 0) {
			do{
				double randno = Math.random();
				if (randno < 0.25) {
					juncdirection = IRobot.LEFT;
				}
				else if (randno < 0.5) {
					juncdirection = IRobot.RIGHT;
				}
				else if (randno < 0.75) {
					juncdirection = IRobot.AHEAD;
				}
				else {
					juncdirection = IRobot.BEHIND;
				}
			}
			while (robot.look(juncdirection) != IRobot.PASSAGE);
		}
		else {
			do{
				double randno = Math.random();
				if (randno < 0.25) {
					juncdirection = IRobot.LEFT;
				}
				else if (randno < 0.5) {
					juncdirection = IRobot.RIGHT;
				}
				else if (randno < 0.75) {
					juncdirection = IRobot.AHEAD;
				}
				else {
					juncdirection = IRobot.BEHIND;
				}
			}
			while (robot.look(juncdirection) == IRobot.WALL);
		}
		return juncdirection;
	}
	
	public void reset() { //Resets JunctionCounter for new maze
		explorerMode = 1;
		rD.resetJunctionCounter();
	}
}

class RobotData {
	
	public static int junctionCounter; //No. of junctions stored
	private int [] arrivedHead = new int[10000];
	
	public void recordJunction(IRobot robot){ //Stores arrived heading and increments junctionCounter                
		arrivedHead[junctionCounter] = robot.getHeading();
        junctionCounter++;
    }
	
	public int searchJunction(IRobot robot){ //Decrements junctionCounter each time it is called, then returns arrived heading
		int arrivedHeading = 0;
        arrivedHeading = arrivedHead[junctionCounter - 1]; //Returns arrivedHeading for junction before last
        junctionCounter--; //JunctionCounter decremented so junction that has been passed by twice is not stored
        return arrivedHeading;
    }
	
	public void resetJunctionCounter() {// Resets JunctionCounter
		junctionCounter = 0; 
	}
	
}