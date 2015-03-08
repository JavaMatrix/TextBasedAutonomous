package org.usfirst.frc.team3585.robot.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.usfirst.frc.team3585.robot.subsystems.IStoppableSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class TextBasedCommand extends Command {
	IStoppableSubsystem system;
	Method method;
	Object[] arguments;
	int line;
	boolean hardFail = false;

	public TextBasedCommand(IStoppableSubsystem system, Method method,
			Object[] args, double time, int line) {
		setTimeout(time);
		this.system = system;
		this.method = method;
		this.arguments = args;
		this.line = line;
	}

	@Override
	protected void initialize() {

	}

	@Override
	protected void execute() {
		try {
			method.invoke(system, arguments);
		} catch (IllegalAccessException e) {
			System.out.println("[TextAuto] Couldn't execute "
					+ method.getName() + ", try making it public.");
			hardFail = true;
		} catch (IllegalArgumentException e) {
			System.out.println("[TextAuto] The arguments given on line " + line
					+ " are invalid. Check them and try again.");
			hardFail = true;
		} catch (InvocationTargetException e) {
			System.out
					.println("[TextAuto] The action given on line "
							+ line
							+ " can't be applied to the object given. Check it and try again.");
			hardFail = true;
		}
	}

	@Override
	protected boolean isFinished() {
		return isTimedOut() || hardFail;
	}

	@Override
	protected void end() {
		system.stop();
	}

	@Override
	protected void interrupted() {
		system.stop();
	}

}
