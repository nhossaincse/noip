package space.davidecolombo.noip;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import space.davidecolombo.noip.noip.NoIpUpdater;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;

@Slf4j
public class App {

	@Option(name = "-settings", aliases = {"-s"}, required = true)
	private String fileName;

	private static class SingletonHolder {
		public static final App instance = new App();
	}

	public static App getInstance() {
		return SingletonHolder.instance;
	}

	private App() {}

	public Integer update(String[] args) {
		int status = NoIpUpdater.ERROR_RETURN_CODE;
		try {
			new CmdLineParser(this).parseArgument(args);
			status = NoIpUpdater.updateFromIpify(fileName);
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
		System.exit(App.getInstance().update(args));
	}
}