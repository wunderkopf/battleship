package me.kurylenko;

import java.util.Random;

public class Bot extends Player  {

	private static final long serialVersionUID = 941695381246358405L;
	
	private boolean enableDebug = false;
	private boolean aggressiveStrategy = false;
	private int asX, arX;
	private int asY, arY;
	private Direction goodDirection;
	private Direction successfullDirection;

	public Bot(boolean enableDebug) {
		super(true);
		this.enableDebug = enableDebug;
	}

	public void draw() {
		if (enableDebug) {
			System.out.println();
			System.out.println("--- BOT ---");
			super.draw();
		}
	}
	
	private boolean aimAt(int x, int y) {
		if (this.enemyField[y][x] != State.EMPTY)
			return false;
		
		if ((x + 1) <= 9) {
			if (this.enemyField[y][x + 1] == State.KILL_ONE || this.enemyField[y][x + 1] == State.KILL_ALL)
				return false;
		}

		if ((x - 1) >= 0) {
			if (this.enemyField[y][x - 1] == State.KILL_ONE || this.enemyField[y][x - 1] == State.KILL_ALL)
				return false;
		}

		if ((y + 1) <= 9) {
			if (this.enemyField[y + 1][x] == State.KILL_ONE || this.enemyField[y + 1][x] == State.KILL_ALL)
				return false;
		}

		if ((y - 1) >= 0) {
			if (this.enemyField[y - 1][x] == State.KILL_ONE || this.enemyField[y - 1][x] == State.KILL_ALL)
				return false;
		}
		
		if ((x + 1) <= 9 && (y - 1) >= 0) {
			if (this.enemyField[y - 1][x + 1] == State.KILL_ONE || this.enemyField[y - 1][x + 1] == State.KILL_ALL)
				return false;
		}
		
		if ((x - 1) >= 0 && (y + 1) <= 9) {
			if (this.enemyField[y + 1][x - 1] == State.KILL_ONE || this.enemyField[y + 1][x - 1] == State.KILL_ALL)
				return false;
		}
		
		if ((y + 1) <= 9 && (x + 1) <= 9) {
			if (this.enemyField[y + 1][x + 1] == State.KILL_ONE || this.enemyField[y + 1][x + 1] == State.KILL_ALL)
				return false;
		}

		if ((y - 1) >= 0 && (x - 1) >= 0) {
			if (this.enemyField[y - 1][x - 1] == State.KILL_ONE || this.enemyField[y - 1][x - 1] == State.KILL_ALL)
				return false;
		}
			
		return true;
	}

	public boolean fire(Player enemy) {
		int x, y;
		Direction direction = null;

		while (true) {
			if (aggressiveStrategy) {
				got_xy:
					while (true) {
						if (isPerfect(this.enemyField, asX, asY, State.EMPTY))
							direction = Direction.getRandom();
						else if (goodDirection == null) {
							if ((successfullDirection == null || successfullDirection == Direction.RIGHT || successfullDirection == Direction.LEFT) && ((asX + 1) <= 9 && this.enemyField[asY][asX + 1] == State.EMPTY))
								direction = Direction.RIGHT;
							else if ((successfullDirection == null || successfullDirection == Direction.BOTTOM || successfullDirection == Direction.TOP) && ((asY + 1) <= 9 && this.enemyField[asY + 1][asX] == State.EMPTY))
								direction = Direction.BOTTOM;
							else if ((successfullDirection == null || successfullDirection == Direction.RIGHT || successfullDirection == Direction.LEFT) && ((asX - 1) >= 0 && this.enemyField[asY][asX - 1] == State.EMPTY))
								direction = Direction.LEFT;
							else if ((successfullDirection == null || successfullDirection == Direction.BOTTOM || successfullDirection == Direction.TOP) && ((asY - 1) >= 0 && this.enemyField[asY - 1][asX] == State.EMPTY))
								direction = Direction.TOP;
							else
								direction = Direction.getRandom();
						}
						else
							direction = goodDirection;

						switch (direction) {
						case LEFT:
							if ((asX - 1) >= 0) {
								x = asX - 1;
								y = asY;
								break got_xy;
							}
							else {
								x = arX;
								y = arY;
								asX = arX;
								asY = arY;
								successfullDirection = null;
								goodDirection = Direction.RIGHT;
							}
							break;
						case RIGHT:
							if ((asX + 1) <= 9) {
								x = asX + 1;
								y = asY;
								break got_xy;
							}
							else {
								x = arX;
								y = arY;
								asX = arX;
								asY = arY;
								successfullDirection = null;
								goodDirection = Direction.LEFT;
							}
							break;
						case TOP:
							if ((asY - 1) >= 0) {
								x = asX;
								y = asY - 1;
								break got_xy;
							}
							else {
								x = arX;
								y = arY;
								asX = arX;
								asY = arY;
								successfullDirection = null;
								goodDirection = Direction.BOTTOM;
							}
							break;
						case BOTTOM:
							if ((asY + 1) <= 9) {
								x = asX;
								y = asY + 1;
								break got_xy;
							}
							else {
								x = arX;
								y = arY;
								asX = arX;
								asY = arY;
								successfullDirection = null;
								goodDirection = Direction.TOP;
							}
							break;
						default:
							while (true) {
								Random random = new Random();
								x = random.nextInt(9);
								y = random.nextInt(9);

								//if (this.enemyField[y][x] == State.EMPTY)
								//if (isGood(this.enemyField, x, y, State.KILL_ONE) && isGood(this.enemyField, x, y, State.KILL_ALL))
								if (aimAt(x, y))
									break got_xy;
							}
						}
					}
			}
			else {
				while (true) {
					Random random = new Random();
					x = random.nextInt(9);
					y = random.nextInt(9);

					//if (this.enemyField[y][x] == State.EMPTY)
					//if (isGood(this.enemyField, x, y, State.KILL_ONE) && isGood(this.enemyField, x, y, State.KILL_ALL))
					if (aimAt(x, y))
						break;
				}
			}

			System.out.print("Bot move: X = " + ((char) ('A' + x)) + (y + 1));
			State result = enemy.boom(x, y);
			switch (result) {
			case KILL_ONE:
				System.out.println(". (Killed one)");
				break;
			case KILL_ALL:
				System.out.println(". (Killed all)");
				break;
			case MISS:
				System.out.println(". (Missed)");
				break;
			default:
				System.out.println();
			}
			
			if (result != State.EMPTY) {
				this.enemyField[y][x] = result;
				if (aggressiveStrategy && result == State.MISS) {
					x = arX;
					y = arY;
					asX = arX;
					asY = arY;
					goodDirection = null;
				}
				if (result == State.KILL_ONE) {
					if (!aggressiveStrategy) { // store first shot values
						arX = x;
						arY = y;
					}
					aggressiveStrategy = true;
					asX = x;
					asY = y;
					goodDirection = direction;
					successfullDirection = direction;
				}
				if (result == State.KILL_ALL) {
					aggressiveStrategy = false;
					asX = -1;
					asY = -1;
					goodDirection = null;
					successfullDirection = null;
				}
				break;
			}
		}
		return true;
	}
}
