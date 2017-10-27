package com.davidecolombo.noip;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	@Option(name = "-settings", required = true)
	private String fileName;

	private void execute(String[] args) {
		int status = Noip.ERROR_CODE;
		try {
			new CmdLineParser(this).parseArgument(args);
			status = Noip.applyFromFile(fileName);
		} catch (CmdLineException e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("Status: " + status);
		System.exit(status);
	}

	/*
	 * Usage: -settings src/test/resources/settings.json
	 */
	public static void main(String[] args) {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		new Main().execute(args);
	}
}
