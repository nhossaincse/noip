package space.davidecolombo.noip;

import java.io.IOException;
import java.util.function.Function;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import lombok.extern.slf4j.Slf4j;
import space.davidecolombo.noip.noip.NoipUpdater;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

@Slf4j
public class Main implements Function<String[], Integer> {

	@Option(name = "-settings", required = true)
	private String fileName;

	private static class SingletonHolder {
		public static final Main instance = new Main();
	}

	public static Main getInstance() {
		return SingletonHolder.instance;
	}

	public static final int unknownError = -1;

	private Main() {}

	@Override
	public Integer apply(String[] args) {
		int status = unknownError;
		try {
			new CmdLineParser(this).parseArgument(args);
			status = NoipUpdater.updateByIpify(fileName);
		} catch (CmdLineException | IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.info("Status: " + status);
		return status;
	}

	/*
	 * Usage: -settings src/test/resources/settings.json
	 */
	public static void main(String[] args) {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		System.exit(Main.getInstance().apply(args));
	}
}
