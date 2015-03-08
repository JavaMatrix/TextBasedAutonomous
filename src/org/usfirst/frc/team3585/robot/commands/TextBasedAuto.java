package org.usfirst.frc.team3585.robot.commands;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.usfirst.frc.team3585.robot.subsystems.IStoppableSubsystem;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class TextBasedAuto extends CommandGroup {

	@SuppressWarnings("unchecked")
	public TextBasedAuto() {
		// To set up, let's create a detailed list of subsystems by name.
		Map<String, Subsystem> subsystemsByName = new HashMap<String, Subsystem>();
		try {
			// We're gonna reflect into the Scheduler class and get all
			// subsystems.
			// This is complicated stuff, don't worry if you don't understand.
			Set<Subsystem> subsystems;
			Field ssf = Scheduler.class.getField("subsystems");
			ssf.setAccessible(true);
			subsystems = (Set<Subsystem>) ssf.get(Scheduler.getInstance());
			for (Subsystem ss : subsystems) {
				if (ss instanceof IStoppableSubsystem) {
					subsystemsByName.put(ss.getName().toLowerCase(), ss);
				} else {
					System.out
							.println("[TextAuto] Skipping the "
									+ ss.getName()
									+ " because it doesn't implement IStoppableSubsystem.");
				}
			}
		} catch (IllegalArgumentException | NoSuchFieldException
				| SecurityException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}

		// Now we can read in the autonomous mode definition.
		InputStream autonomous = getClass()
				.getResourceAsStream("text/auto.txt");
		Scanner reader = new Scanner(autonomous);
		String line = "";
		int lineNum = 0;
		outerloop: while (reader.hasNextLine()) {
			lineNum++;
			line = reader.nextLine();
			boolean isSynchronous;
			if (line.startsWith("#") || line.startsWith("//")) {
				continue;
			}
			if (line.startsWith("{")) {
				isSynchronous = true;
				line = line.substring(1);
			}
			String[] tokens = line.split(" ");
			if (subsystemsByName.keySet().contains(tokens[0].toLowerCase())) {
				Subsystem sub = subsystemsByName.get(tokens[0].toLowerCase());
				boolean found = false;
				for (Method m : sub.getClass().getMethods()) {
					if (m.getName().equalsIgnoreCase(tokens[1])) {
						found = true;
						Object[] args = new Object[m.getParameterCount()];
						Class[] parameterTypes = m.getParameterTypes();
						for (int i = 0; i < args.length; i++) {
							Class type = parameterTypes[i];
							String strArg = tokens[i + 2];
							if (type.equals(byte.class)) {
								try {
									args[i] = Byte.parseByte(strArg.replace(
											",", ""));
								} catch (NumberFormatException e) {
									System.out
											.println("[TextAuto] Skipping line "
													+ lineNum
													+ ", "
													+ strArg
													+ " is not a valid number.");
									System.out
											.println("This value must also be between 0 and 255.");
									continue outerloop;
								}
							} else if (type.equals(short.class)) {
								try {
									args[i] = Short.parseShort(strArg.replace(
											",", ""));
								} catch (NumberFormatException e) {
									System.out
											.println("[TextAuto] Skipping line "
													+ lineNum
													+ ", "
													+ strArg
													+ " is not a valid short.");
									System.out
											.println("This value must also be between -32,768 and 32,767.");
									continue outerloop;
								}
							} else if (type.equals(int.class)) {
								try {
									args[i] = Integer.parseInt(strArg.replace(
											",", ""));
								} catch (NumberFormatException e) {
									System.out
											.println("[TextAuto] Skipping line "
													+ lineNum
													+ ", "
													+ strArg
													+ " is not a valid integer.");
									System.out
											.println("This value must also be between -2,147,483,648 and 2,147,483,647.");
									continue outerloop;
								}
							} else if (type.equals(long.class)) {
								try {
									args[i] = Long.parseLong(strArg.replace(
											",", ""));
								} catch (NumberFormatException e) {
									System.out
											.println("[TextAuto] Skipping line "
													+ lineNum
													+ ", "
													+ strArg
													+ " is not a valid long number.");
									System.out
											.println("This value must also be between -9,223,372,036,854,775,808 and 9,223,372,036,854,775,807.");
									continue outerloop;
								}
							} else if (type.equals(float.class)) {
								try {
									args[i] = Float.parseFloat(strArg.replace(
											",", ""));
								} catch (NumberFormatException e) {
									System.out
											.println("[TextAuto] Skipping line "
													+ lineNum
													+ ", "
													+ strArg
													+ " is not a valid floating-point number.");
									System.out
											.println("This value must also be between"
													+ Float.MIN_VALUE
													+ " and "
													+ Float.MAX_VALUE + ".");
									continue outerloop;
								}
							} else if (type.equals(double.class)) {
								try {
									args[i] = Double.parseDouble(strArg
											.replace(",", ""));
								} catch (NumberFormatException e) {
									System.out
											.println("[TextAuto] Skipping line "
													+ lineNum
													+ ", "
													+ strArg
													+ " is not a valid double precision floating-point number.");
									System.out
											.println("This value must also be between"
													+ Double.MIN_VALUE
													+ " and "
													+ Double.MAX_VALUE + ".");
									continue outerloop;
								}
							} else if (type.equals(boolean.class)) {
								args[i] = Boolean.parseBoolean(strArg);
							} else if (type.equals(char.class)) {
								args[i] = strArg.charAt(0);
							} else if (type.equals(String.class)) {
								args[i] = strArg;
							}
						}
					}
				}
				if (!found) {
					System.out.println("[TextAuto] Skipping line " + lineNum
							+ ", invalid action.");
					continue;
				}

			} else {
				System.out.println("[TextAuto] Skipping line " + lineNum
						+ ", invalid subsystem name.");
				continue;
			}
		}
	}
}
