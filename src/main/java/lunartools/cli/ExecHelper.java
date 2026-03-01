package lunartools.cli;

import java.util.ArrayList;

public class ExecHelper {

	public static String[] createCmdArray(String command, String parameterPattern, String... parameters) {
		String[] patternArray=parameterPattern.split(" ");

		ArrayList<String> commandArray=new ArrayList<>();
		commandArray.add(command);
		int parameterIndex=0;
		for(String pattern:patternArray) {
			if(pattern.equalsIgnoreCase("%s")) {
				if(parameterIndex==parameters.length) {
					throw new ArrayIndexOutOfBoundsException(parameterIndex);
				}
				commandArray.add(parameters[parameterIndex++]);
			}else {
				commandArray.add(pattern);
			}
		}
		return commandArray.toArray(new String[0]);
	}

}
