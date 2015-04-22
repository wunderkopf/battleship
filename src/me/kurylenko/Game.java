package me.kurylenko;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Game {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		// TODO: get from command line
		boolean enableDebug = false;
		boolean tryAgain = true;

		while (tryAgain) {
			
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			System.out.println("Would you like to place ships randomly? (y/n)");
			String userReply = scanner.nextLine();
			boolean random = true;
			if (userReply.equalsIgnoreCase("n") || userReply.equalsIgnoreCase("no"))
				random = false;

			User user = new User(random);
			Bot bot = new Bot(enableDebug);

			while (true) {
				user.draw();
				System.out.print(String.format(String.format("%%0%dd", 55), 0).replace("0", "-"));
				System.out.println();
				bot.draw();

				// save <>, load <>, quit, exit, help
				if (!user.fire(bot)) {
					System.out.print("> ");
					userReply = scanner.nextLine();

					String[] command = userReply.split(" ");

					if (command.length == 1) { // quit, exit, help
						if (command[0].equalsIgnoreCase("exit") || command[0].equalsIgnoreCase("quit")) {
							tryAgain = false;
							break;
						}
						else if (command[0].equalsIgnoreCase("help")) {
							System.out.println("BEGIN HELP");
							System.out.println("save <FILENAME>; load <FILENAME>; quit; exit; help");
							System.out.println("END HELP");
						}
					}
					else if (command.length == 2) { // save, load
						if (command[0].equalsIgnoreCase("load")) {
							FileInputStream fiStream = new FileInputStream(command[1]);
							ObjectInputStream oiStream = new ObjectInputStream(fiStream);
							user = (User)oiStream.readObject();
							bot = (Bot)oiStream.readObject();
							oiStream.close();
						}
						else if (command[0].equalsIgnoreCase("save")) {
							FileOutputStream foStream = new FileOutputStream(command[1]);
							ObjectOutputStream ooStream = new ObjectOutputStream(foStream);
							ooStream.writeObject(user);
							ooStream.writeObject(bot);
							ooStream.close();
						}
					}
					continue;
				}

				if (user.win()) {
					System.out.println("You have won!");
					break;
				}

				bot.fire(user);
				if (bot.win()) {
					System.out.println("Bot has won!");
					user.draw();
					break;
				}
			}

			System.out.println("Play again? (y/n)");
			userReply = scanner.nextLine();

			if (userReply.equalsIgnoreCase("n") || userReply.equalsIgnoreCase("no"))
				tryAgain = false;
		}
	}
}
