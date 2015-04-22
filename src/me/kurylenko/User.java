package me.kurylenko;

import java.util.Scanner;

public class User extends Player {

	private static final long serialVersionUID = 5801878139241103674L;
	
	public User(boolean random) {
		super(random);
	}

	public void draw() {
		System.out.println();
		System.out.println("--- YOU ---");
		super.draw();
	}

	@SuppressWarnings("resource")
	public boolean fire(Player enemy) {
		while (true) {
			System.out.println("Your move (example: 'A1' or ':command'): ");

			Scanner scanner = new Scanner(System.in);
			String userReply = scanner.nextLine();
			if (userReply.isEmpty()) {
				System.out.println("Incorrect input. Try again.");
				continue;
			}

			char letter = userReply.charAt(0);
			if (!Character.isLetter(letter)) {
				if (letter == ':') // it should be command
					return false;
				else {
					System.out.println("First character in not a letter. Try again.");
					continue;
				}
			}

			int xPos = -1;
			if (letter >= 'A' && letter <= 'Z')
				xPos = (int) (letter - 'A' + 1);
			if (letter >= 'a' && letter <= 'z')
				xPos = (int) (letter - 'a' + 1);

			if (xPos > 10) {
				System.out.println("Incorrect letter. Try again.");
				continue;
			}

			int yPos = -1;
			try {
				yPos = Integer.parseInt(userReply.substring(1,
						userReply.length()));
			} catch (NumberFormatException exception) {
				System.out.println("Incorrect number. Try again.");
				continue;
			}

			if (yPos > 10) {
				System.out.println("Incorrect number. Try again.");
				continue;
			}

			--yPos;
			--xPos;

			State result = enemy.boom(xPos, yPos);

			switch (result) {
			case KILL_ONE:
				System.out.println("Killed one!!!");
				break;
			case KILL_ALL:
				System.out.println("Killed all!!!");
				break;
			case MISS:
				System.out.println("Missed!!!");
				break;
			default:
				assert (false);
			}

			this.enemyField[yPos][xPos] = result;
			break;
		}
		return true;
	}
}
