package com.davidecolombo.noip;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.davidecolombo.noip.noip.NoipUpdater;

import lombok.extern.slf4j.Slf4j;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

@Slf4j
public class Main {

	@Option(name = "-settings", required = true)
	private String fileName;

	private void execute(String[] args) {
		int status = NoipUpdater.ERROR_CODE;
		try {
			new CmdLineParser(this).parseArgument(args);
			status = NoipUpdater.updateByIpify(new File(fileName));
		} catch (CmdLineException e) {
			logger.error(e.getLocalizedMessage(), e);
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
