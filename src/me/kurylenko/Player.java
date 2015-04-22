package me.kurylenko;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

@SuppressWarnings("serial")
public abstract class Player implements Serializable {

	public enum State {
		EMPTY, SHIP, KILL_ONE, KILL_ALL, MISS;
	};

	private State[][] myField = new State[10][10];
	protected State[][] enemyField = new State[10][10];
	private int shipCount = 0;
	private int deckCount = 0;

	public enum Direction {
		TOP, RIGHT, LEFT, BOTTOM;

		public static Direction getRandom() {
			return values()[(int) (Math.random() * values().length)];
		}
	};

	private char shipToChar(State state) {
		switch (state) {
		case EMPTY:
			return ' ';
		case SHIP:
			return 'X';
		case KILL_ONE:
		case KILL_ALL:
			return '*';
		case MISS:
			return 'O';
		}
		return '?';
	}
	
	protected int letterToNum(char letter) {
		int result = -1;
		if (Character.isLetter(letter)) {
			if (letter >= 'A' && letter <= 'Z')
				result = (int) (letter - 'A' + 1);
			if (letter >= 'a' && letter <= 'z')
				result = (int) (letter - 'a' + 1);
		}
		return result;
	}

	protected boolean isPerfect(State[][] field, int x, int y, State state) {
		if ((x + 1) <= 9 && field[y][x + 1] != /*State.EMPTY*/state)
			return false;

		if ((x - 1) >= 0 && field[y][x - 1] != /*State.EMPTY*/state)
			return false;

		if ((y + 1) <= 9 && field[y + 1][x] != /*State.EMPTY*/state)
			return false;

		if ((y - 1) >= 0 && field[y - 1][x] != /*State.EMPTY*/state)
			return false;

		return true;
	}

	protected boolean isGood(State[][] field, int x, int y, State state) {

		if (x <= 9 && x >= 0 && y <= 9 && y>=0 && field[y][x] != /*State.EMPTY*/state)
			return false;

		if (!isPerfect(field, x, y, state))
			return false;
		
		if ((x + 1) <= 9 && (y - 1) >= 0 && field[y - 1][x + 1] != /*State.EMPTY*/state)
			return false;
		
		if ((x - 1) >= 0 && (y + 1) <= 9 && field[y + 1][x - 1] != /*State.EMPTY*/state)
			return false;
		
		if ((y + 1) <= 9 && (x + 1) <= 9 && field[y + 1][x + 1] != /*State.EMPTY*/state)
			return false;

		if ((y - 1) >= 0 && (x - 1) >= 0 && field[y - 1][x - 1] != /*State.EMPTY*/state)
			return false;
		
		return true;
	}
	protected boolean checkPerfectDirection(State[][] field, Direction direction, int x, int y, int number) {
		if (direction == Direction.RIGHT && (x + number) <= 10) {
			for (int n = 0; n < number; ++n) {
				if (!isPerfect(field, x + n, y, State.EMPTY))
					return false;
			}
		}

		if (direction == Direction.LEFT && (x + 1 - number) >= 0) {
			for (int n = 0; n < number; ++n) {
				if (!isPerfect(field, x - n, y, State.EMPTY))
					return false;
			}
		}

		if (direction == Direction.TOP && (y + 1 - number) >= 0) {
			for (int n = 0; n < number; ++n) {
				if (!isPerfect(field, x, y - n, State.EMPTY))
					return false;
			}
		}

		if (direction == Direction.BOTTOM && (y + number) <= 10) {
			for (int n = 0; n < number; ++n) {
				if (!isPerfect(field, x, y + n, State.EMPTY))
					return false;
			}
		}

		return true;
	}

	protected boolean checkDirection(State[][] field, Direction direction, int x, int y, int number) {
		if (direction == Direction.RIGHT && (x + number) <= 10) {
			for (int n = 0; n < number; ++n) {
				if (!isGood(field, x + n, y, State.EMPTY))
					return false;
			}
		}

		if (direction == Direction.LEFT && (x + 1 - number) >= 0) {
			for (int n = 0; n < number; ++n) {
				if (!isGood(field, x - n, y, State.EMPTY))
					return false;
			}
		}

		if (direction == Direction.TOP && (y + 1 - number) >= 0) {
			for (int n = 0; n < number; ++n) {
				if (!isGood(field, x, y - n, State.EMPTY))
					return false;
			}
		}

		if (direction == Direction.BOTTOM && (y + number) <= 10) {
			for (int n = 0; n < number; ++n) {
				if (!isGood(field, x, y + n, State.EMPTY))
					return false;
			}
		}

		return true;
	}

	protected boolean fillDirection(State[][] field, Direction direction, int x, int y, int number) {

		if (!checkDirection(field, direction, x, y, number))
			return false;

		if (direction == Direction.RIGHT && (x + number) <= 10) {
			for (int n = 0; n < number; ++n) {
				field[y][x + n] = State.SHIP;
			}
			return true;
		}

		if (direction == Direction.LEFT && (x + 1 - number) >= 0) {
			for (int n = 0; n < number; ++n) {
				field[y][x - n] = State.SHIP;
			}
			return true;
		}

		if (direction == Direction.TOP && (y + 1 - number) >= 0) {
			for (int n = 0; n < number; ++n) {
				field[y - n][x] = State.SHIP;
			}
			return true;
		}

		if (direction == Direction.BOTTOM && (y + number) <= 10) {
			for (int n = 0; n < number; ++n) {
				field[y + n][x] = State.SHIP;
			}
			return true;
		}

		return false;
	}

	private void Ship(int number, boolean randomShips) {
		assert (number > 0);

		if (randomShips) {
			Random random = new Random();
			while (true) {
				int x = random.nextInt(9);
				int y = random.nextInt(9);

				Direction direction = Direction.getRandom();
				if (fillDirection(this.myField, direction, x, y, number))
					break;
			}
		}
		else {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			while (true) {
				draw();
				System.out.println("Let's put " + number + " deck ship (examples: A1-D1, F6): ");
				String userReply = scanner.nextLine();
				String[] decks = userReply.split("-");
				
				int x = letterToNum(decks[0].charAt(0));
				if (x == -1) {
					System.out.println("Incorrect ship letter. Try again.");
					continue;
				}
				
				int y = -1;
				try {
					y = Integer.parseInt(decks[0].substring(1, decks[0].length()));
				} catch (NumberFormatException exception) {
					System.out.println("Incorrect ship number. Try again.");
					continue;
				}
				--y;
				--x;
				
				Direction direction = null;
				
				if (decks.length > 1) {
					int endX = letterToNum(decks[1].charAt(0));
					if (endX == -1) {
						System.out.println("Incorrect ship letter. Try again.");
						continue;
					}
					
					int endY = -1;
					try {
						endY = Integer.parseInt(decks[1].substring(1, decks[1].length()));
					} catch (NumberFormatException exception) {
						System.out.println("Incorrect ship number. Try again.");
						continue;
					}
					--endY;
					--endX;
					
					if (x == endX && y > endY) // TOP
						direction = Direction.TOP;
					else if (x < endX && y == endY) // RIGHT
						direction = Direction.RIGHT;
					else if (x == endX && y < endY) // BOTTOM
						direction = Direction.BOTTOM;
					else if (x > endX && y == endY) // LEFT
						direction = Direction.LEFT;
					else {
						System.out.println("Incorrect end ship. Try again.");
						continue;
					}
				}
				else
					direction = Direction.TOP; // for 1 deck ship - any direction
				
				if (fillDirection(this.myField, direction, x, y, number))
					break;
				else {
					System.out.println("Incorrect ship. Try again.");
					continue;
				}
			}
		}
	}

	private void Four(boolean random) {
		assert (shipCount == 0);
		Ship(4, random);
		++shipCount;
		deckCount = deckCount + 4;
	}

	private void Three(boolean random) {
		assert (shipCount > 0 && shipCount < 3);
		Ship(3, random);
		++shipCount;
		deckCount = deckCount + 3;
	}

	private void Two(boolean random) {
		assert (shipCount > 2 && shipCount < 7);
		Ship(2, random);
		++shipCount;
		deckCount = deckCount + 2;
	}

	private void One(boolean random) {
		assert (shipCount > 5 && shipCount < 11);
		Ship(1, random);
		++shipCount;
		deckCount = deckCount + 1;
	}

	public Player(boolean random) {
		for (State[] row : this.myField)
			Arrays.fill(row, State.EMPTY);

		for (State[] row : this.enemyField)
			Arrays.fill(row, State.EMPTY);

		Four(random);
		Three(random);
		Three(random);
		Two(random);
		Two(random);
		Two(random);
		One(random);
		One(random);
		One(random);
		One(random);
	}

	public void draw() {
		System.out.println();

		// HEADER begin
		System.out.print("  ");
		for (int y = 0; y < 10; ++y) {
			System.out.print((char) ('A' + y));
			System.out.print(" ");
		}
		System.out.print("\t\t");
		System.out.print("  ");
		for (int y = 0; y < 10; ++y) {
			System.out.print((char) ('A' + y));
			System.out.print(" ");
		}

		System.out.println();
		System.out.print("+ ");
		for (int y = 0; y < 10; ++y) {
			System.out.print("-");
			System.out.print(" ");
		}
		System.out.print("+");
		System.out.print("\t\t");
		System.out.print("+ ");
		for (int y = 0; y < 10; ++y) {
			System.out.print("-");
			System.out.print(" ");
		}
		System.out.println("+");
		// HEADER end

		for (int y = 0; y < 10; ++y) {

			if ((y + 1) == 10)
				System.out.print("X ");
			else
				System.out.print((y + 1) + " ");

			for (int x = 0; x < 10; ++x) {
				System.out.print(shipToChar(this.myField[y][x]) + " ");
			}

			System.out.print(" \t\t");
			if ((y + 1) == 10)
				System.out.print("X ");
			else
				System.out.print((y + 1) + " ");

			for (int x = 0; x < 10; ++x) {
				System.out.print(shipToChar(this.enemyField[y][x]) + " ");
			}

			System.out.println();
		}

		System.out.println();
	}

	public abstract boolean fire(Player enemy);

	private boolean destroyed(int x, int y) {
		int i = 1;
		while ((x + i) <= 9) {
			// RIGHT
			if (this.myField[y][x + i] == State.SHIP) 
				return false;
			else if (this.myField[y][x + i] == State.EMPTY || this.myField[y][x + i] == State.MISS)
				break;
			++i;
		}

		i = 1;
		while ((x - i) >= 0) {
			// LEFT
			if (this.myField[y][x - i] == State.SHIP)
				return false;
			else if (this.myField[y][x - i] == State.EMPTY || this.myField[y][x - i] == State.MISS)
				break;
			++i;
		}

		i = 1;
		while ((y + i) <= 9) {
			// BOTTOM
			if (this.myField[y + i][x] == State.SHIP)
				return false;
			else if (this.myField[y + i][x] == State.EMPTY || this.myField[y + i][x] == State.MISS)
				break;
			++i;
		}

		i = 1;
		while ((y - i) >= 0) {
			// TOP
			if (this.myField[y - i][x] == State.SHIP)
				return false;
			else if (this.myField[y - i][x] == State.EMPTY || this.myField[y - i][x] == State.MISS)
				break;
			++i;
		}

		return true;
	}

	protected State boom(int x, int y) {

		switch (this.myField[y][x]) {
		case EMPTY: // empty
			this.myField[y][x] = State.MISS;
			return State.MISS;
		case SHIP: // ship
			this.myField[y][x] = State.KILL_ONE;
			if (destroyed(x, y)) {
				this.myField[y][x] = State.KILL_ALL;
				return State.KILL_ALL;
			}
			return State.KILL_ONE;
		default:
			break;
		}

		return State.EMPTY;
	}

	public boolean win() {
		int count = 0;
		for (State[] rowData : this.enemyField) {
			for (State cellData : rowData) {
				if (cellData == State.KILL_ONE || cellData == State.KILL_ALL)
					++count;
			}
		}
		return (count == this.deckCount);
	}
}
