package space.davidecolombo.noip.utils;

import java.util.regex.Pattern;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IpUtils {

	private static final Pattern IPV4_PATTERN =
			Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	public static boolean isIPv4Address(@NonNull final String ip) {
		return IPV4_PATTERN.matcher(ip).matches();
	}
}
