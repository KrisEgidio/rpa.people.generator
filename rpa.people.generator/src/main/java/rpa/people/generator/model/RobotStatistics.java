package rpa.people.generator.model;

public class RobotStatistics {
	
	public static String executionStatus;

	public static Integer inputCount;
	public static Integer outputCount;
	public static Integer successOutput;
	public static Integer errorOutput;
	public static Integer warnOutput;

	public static String startDateTime;
	public static String endDateTime;

	public static void initializeItems() {
		inputCount = 0;
		outputCount = 0;
		successOutput = 0;
		errorOutput = 0;
		warnOutput = 0;
	}
}
