import uk.ac.warwick.dcs.maze.logic.IRobot;
//Does not work
public class Ex3 {
	private int explorerMode;
	private int pollRun = 0; //Incremented after each pass
	private RobotDataEx3 rD = new RobotDataEx3(); //Data store for junctions
	
	public void exploreControl (IRobot robot) { //Control for explorer mode
		System.out.println("Entered exploreControl");
		int direction = 0;
		int exits = nonwallExits(robot);
		int pexits = passageExits(robot);
		switch (exits) { //Returns method based on exit number
			case 4: direction = junctionorcross(robot); break;
			case 3: direction = junctionorcross(robot); break;
			case 2: direction = corridor(robot); break;
			case 1: direction = deadend(robot); break;
		}
		robot.face(direction);
	}
	
	public void controlRobot(IRobot robot) {
		System.out.println("Entered controlRobot");
		// On the first move of the first run of a new maze
		if ((robot.getRuns() == 0) && (pollRun == 0)) {
			rD = new RobotDataEx3(); //reset the data store
			explorerMode = 1; //Set intial mode to explorer
			pollRun++; // Increment pollRun so that the data is not
					   // reset each time the robot moves
		}
		exploreControl(robot);
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
	
	private int lookHeading (IRobot robot, int heading) {
	  int returnvalue = robot.look(IRobot.AHEAD);
	  int robotheading = robot.getHeading(); //to take the heading of the robot to compare with the requested heading in switch statements
	  
	  if (heading == IRobot.NORTH) //If statement for each requested heading
		 switch (robotheading){
			 case IRobot.NORTH : returnvalue = robot.look(IRobot.AHEAD); break;
			 case IRobot.SOUTH : returnvalue = robot.look(IRobot.BEHIND); break;
			 case IRobot.EAST : returnvalue = robot.look(IRobot.LEFT); break;
			 case IRobot.WEST : returnvalue = robot.look(IRobot.RIGHT); break;
		 } //switch statement returning the type of block that is to north of the robot, regardless of the heading of the robot
		 
	  if (heading == IRobot.SOUTH)
		 switch (robotheading){
			 case IRobot.NORTH : returnvalue = robot.look(IRobot.BEHIND); break;
			 case IRobot.SOUTH : returnvalue = robot.look(IRobot.AHEAD); break;
			 case IRobot.EAST : returnvalue = robot.look(IRobot.RIGHT); break;
			 case IRobot.WEST : returnvalue = robot.look(IRobot.LEFT); break;
		 }
	  if (heading == IRobot.EAST) 
		 switch (robotheading){
			 case IRobot.NORTH : returnvalue = robot.look(IRobot.RIGHT); break;
			 case IRobot.SOUTH : returnvalue = robot.look(IRobot.LEFT); break;
			 case IRobot.EAST : returnvalue = robot.look(IRobot.AHEAD); break;
			 case IRobot.WEST : returnvalue = robot.look(IRobot.BEHIND); break;
		 }
	  if (heading == IRobot.WEST)
		 switch (robotheading){
			 case IRobot.NORTH : returnvalue = robot.look(IRobot.LEFT); break;
			 case IRobot.SOUTH : returnvalue = robot.look(IRobot.RIGHT); break;
			 case IRobot.EAST : returnvalue = robot.look(IRobot.BEHIND); break;
			 case IRobot.WEST : returnvalue = robot.look(IRobot.AHEAD); break;
		 }
		 
		 return returnvalue;
  } //returns the blocks surrounding the robot in absolute directions
	
	private int leastMarks (IRobot robot) {
		
		int returnvalue = 0;
		int arrivedpathMarks = rD.searchJunction(robot, robot.getLocation().x, robot.getLocation().y, (robot.getHeading()-2));

		
		int A = rD.searchJunction(robot, robot.getLocation().x, robot.getLocation().y, IRobot.NORTH);
		int B = rD.searchJunction(robot, robot.getLocation().x, robot.getLocation().y, IRobot.SOUTH);
		int C = rD.searchJunction(robot, robot.getLocation().x, robot.getLocation().y, IRobot.EAST);
		int D = rD.searchJunction(robot, robot.getLocation().x, robot.getLocation().y, IRobot.WEST);
		
		if (A==B && B==C && C==D) {
			double rand = Math.random();
			if (rand<0.25) {
				returnvalue = IRobot.NORTH;
			}
			else if (rand<0.5) {
				returnvalue = IRobot.SOUTH;
			}
			else if (rand<0.75) {
				returnvalue = IRobot.EAST;
			}
			else {
				returnvalue = IRobot.WEST;
			}
		}
		else if (A == B && B == C) {
			if (D<C) {
				returnvalue = IRobot.WEST;
			}
			else {
				double rand = Math.random();
				if (rand < 0.33) {
					returnvalue = IRobot.NORTH;
				}
				else if (rand < 0.66) {
					returnvalue = IRobot.SOUTH;
				}
				else {
					returnvalue = IRobot.EAST;
				}
			}
		}
		else if (A == B && B == D) {
			if (C<D) {
				returnvalue = IRobot.EAST;
			}
			else {
				double rand = Math.random();
				if (rand < 0.33) {
					returnvalue = IRobot.NORTH;
				}
				else if (rand < 0.66) {
					returnvalue = IRobot.SOUTH;
				}
				else {
					returnvalue = IRobot.WEST;
				}
			}
		}
		else if (A == C && C == D) {
			if (B<D) {
				returnvalue = IRobot.SOUTH;
			}
			else {
				double rand = Math.random();
				if (rand < 0.33) {
					returnvalue = IRobot.NORTH;
				}
				else if (rand < 0.66) {
					returnvalue = IRobot.WEST;
				}
				else {
					returnvalue = IRobot.EAST;
				}
			}
		}
		else if (B == C && C == D) {
			if (A<D) {
				returnvalue = IRobot.NORTH;
			}
			else {
				double rand = Math.random();
				if (rand < 0.33) {
					returnvalue = IRobot.WEST;
				}
				else if (rand < 0.66) {
					returnvalue = IRobot.SOUTH;
				}
				else {
					returnvalue = IRobot.EAST;
				}
			}
		}
		else if (A == B && C == D) {
			if (A<C) {
				double rand = Math.random();
				if (rand < 0.5) {
					returnvalue = IRobot.NORTH;
				}
				else {
					returnvalue = IRobot.SOUTH;
				}
			}
			else {
				double rand = Math.random();
				if (rand < 0.5) {
					returnvalue = IRobot.WEST;
				}
				else {
					returnvalue = IRobot.EAST;
				}
			}
		}
		else if (A == C && B == D) {
			if (A<B) {
				double rand = Math.random();
				if (rand < 0.5) {
					returnvalue = IRobot.NORTH;
				}
				else {
					returnvalue = IRobot.EAST;
				}
			}
			else {
				double rand = Math.random();
				if (rand < 0.5) {
					returnvalue = IRobot.SOUTH;
				}
				else {
					returnvalue = IRobot.WEST;
				}
			}
		}
		else if (A == D && C == B) {
			if (A<C) {
				double rand = Math.random();
				if (rand < 0.5) {
					returnvalue = IRobot.NORTH;
				}
				else {
					returnvalue = IRobot.WEST;
				}
			}
			else {
				double rand = Math.random();
				if (rand < 0.5) {
					returnvalue = IRobot.EAST;
				}
				else {
					returnvalue = IRobot.SOUTH;
				}
			}
		}
		else if (A == B) {
			if (C<D) {
				if (C<A) {
					returnvalue = IRobot.EAST;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.NORTH;
					}
					else {
						returnvalue = IRobot.SOUTH;
					}
				}
			}
			else {
				if(D<A) {
					returnvalue = IRobot.WEST;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.NORTH;
					}
					else {
						returnvalue = IRobot.SOUTH;
					}
				}
			}
		}
		else if (C == D) {
			if (A<B) {
				if (A<C) {
					return A;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.EAST;
					}
					else {
						returnvalue = IRobot.WEST;
					}
				}
			}
			else {
				if (B<C) {
					return B;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.EAST;
					}
					else {
						returnvalue = IRobot.WEST;
					}
				}
			}
		}
		else if (A == C) {
			if (B<D) {
				if (B<A) {
					returnvalue = IRobot.SOUTH;
				}
				else  {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.NORTH;
					}
					else {
						returnvalue = IRobot.EAST;
					}
				}
			}
			else {
				if (D<A) {
					returnvalue = IRobot.WEST;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.NORTH;
					}
					else {
						returnvalue = IRobot.EAST;
					}
				}
			}
		}
		else if (B == D) {
			if (A<C) {
				if (A<B) {
					returnvalue = IRobot.NORTH;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.WEST;
					}
					else {
						returnvalue = IRobot.SOUTH;
					}
				}
			}
			else {
				if (C<B) {
					returnvalue = IRobot.EAST;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.WEST;
					}
					else {
						returnvalue = IRobot.SOUTH;
					}
				}
			}
		}
		else if (A == D) {
			if (B<C) {
				if (B<A) {
					returnvalue = IRobot.SOUTH;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.NORTH;
					}
					else {
						returnvalue = IRobot.WEST;
					}
				}
			}
			else {
				if (C<A) {
					returnvalue = IRobot.EAST;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.NORTH;
					}
					else {
						returnvalue = IRobot.WEST;
					}
				}
			}
		}
		else if (C == B) {
			if (A<D) {
				if (A<C) {
					returnvalue = IRobot.NORTH;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.EAST;
					}
					else {
						returnvalue = IRobot.SOUTH;
					}
				}
			}
			else {
				if (D<C) {
					returnvalue = IRobot.WEST;
				}
				else {
					double rand = Math.random();
					if (rand < 0.5) {
						returnvalue = IRobot.EAST;
					}
					else {
						returnvalue = IRobot.SOUTH;
					}
				}
			}
		}
		return returnvalue;
	}	
	
	private int deadend (IRobot robot) { //Chooses direction at deadend
		int direction = 0;
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
		System.out.println("Working on corridor");
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
	
	private int junctionorcross (IRobot robot) { //chooses direction for junctions and crossroads
		System.out.println("Working on junc or cross direction");
		int least = 0;
		int correctHeading = 0;
		int correctDirection = 0;
		int opparrivedHeading = 0;
		int arrivedHeading = robot.getHeading();
		if (robot.getHeading() == IRobot.NORTH) {
			opparrivedHeading = IRobot.SOUTH;
		}
		if (robot.getHeading() == IRobot.SOUTH) {
			opparrivedHeading = IRobot.NORTH;
		}
		if (robot.getHeading() == IRobot.EAST) {
			opparrivedHeading = IRobot.WEST;
		}
		if (robot.getHeading() == IRobot.WEST) {
			opparrivedHeading = IRobot.EAST;
		}
		boolean choose = false;
		if (beenbeforeExits(robot) == 1) {
			rD.recordJunction(robot.getLocation().x, robot.getLocation().y);
			do {
				double rand = Math.random();
				if (rand<0.25) {
					correctHeading = IRobot.NORTH;
				}
				else if (rand<0.5) {
					correctHeading = IRobot.SOUTH;
				}
				else if (rand<0.75) {
					correctHeading = IRobot.EAST;
				}
				else {
					correctHeading = IRobot.WEST;
				}
			} while (correctHeading == arrivedHeading || lookHeading(robot, correctHeading) == IRobot.WALL);
			rD.recordMarks(robot, correctHeading, rD.junctionCounter);
			rD.recordMarks(robot, arrivedHeading, rD.junctionCounter);
		}
		if (beenbeforeExits(robot) != 1) {
			System.out.println("working on already seen junction");
			int arrivedMarks = rD.searchJunction(robot, robot.getLocation().x, robot.getLocation().y, opparrivedHeading);
			int i = rD.searchJunctioni(robot, robot.getLocation().x, robot.getLocation().y);
			if (arrivedMarks == 2) {
				System.out.println("arrivedMarks is 2");
				correctHeading = leastMarks(robot);
				rD.recordMarks(robot, correctHeading, i);
				System.out.println(correctHeading);
			}
			if (arrivedMarks == 1) {
				System.out.println("arrivedMarks is 1");
				correctHeading = opparrivedHeading;
				rD.recordMarks(robot, correctHeading, i);
				System.out.println(correctHeading);
			}
		}
		if (correctHeading == IRobot.NORTH) { //Switch statements with directions to turn depending on correctHeading
			switch (arrivedHeading) {
				case IRobot.NORTH : correctDirection = IRobot.AHEAD; break;
				case IRobot.SOUTH : correctDirection = IRobot.BEHIND; break;
				case IRobot.EAST : correctDirection = IRobot.LEFT; break;
				case IRobot.WEST : correctDirection = IRobot.RIGHT; break;
			}
		}
			
		if (correctHeading == IRobot.SOUTH) {
			switch (arrivedHeading) {
				case IRobot.NORTH : correctDirection = IRobot.BEHIND; break;
				case IRobot.SOUTH : correctDirection = IRobot.AHEAD; break;
				case IRobot.EAST : correctDirection = IRobot.RIGHT; break;
				case IRobot.WEST : correctDirection = IRobot.LEFT; break;
			}
		}
				
		if (correctHeading == IRobot.EAST) {
			switch (arrivedHeading) {
				case IRobot.NORTH : correctDirection = IRobot.RIGHT; break;
				case IRobot.SOUTH : correctDirection = IRobot.LEFT; break;
				case IRobot.EAST : correctDirection = IRobot.AHEAD; break;
				case IRobot.WEST : correctDirection = IRobot.BEHIND; break;
			}
		}
				
		if (correctHeading == IRobot.WEST) {
			switch (arrivedHeading) {
				case IRobot.NORTH : correctDirection = IRobot.LEFT; break;
				case IRobot.SOUTH : correctDirection = IRobot.RIGHT; break;
				case IRobot.EAST : correctDirection = IRobot.BEHIND; break;
				case IRobot.WEST : correctDirection = IRobot.AHEAD; break;
			}
		}
		System.out.println(correctDirection);
		return correctDirection;
	}
	
	public void reset() { //resets JunctionCounter for new maze
		System.out.println("Resetting");
		explorerMode = 1;
		rD.resetJunctionCounter();
	}
}

class MarkRecorder {
	
	int marknumberN;
	int marknumberS;
	int marknumberW;
	int marknumberE;
	
}	

class JunctionRecorderEx3 {
	
	private int juncX;
	private int juncY;
	private int arrived;

	
	int getX(){ //Returns X coordinate
		return juncX;
	}
	
	int getY(){//Return Y coordinate
		return juncY;
	}
	
	int getArrived() {//Returns heading arrived from
		return arrived;
	}
	
	JunctionRecorderEx3(int x, int y) { //Constructor that takes in x, y and arrived values
		juncX = x;
		juncY = y;	
	}
}