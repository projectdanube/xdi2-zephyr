package xdi2.messaging.tests.messagingtarget;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.zephyr.ZephyrGraphFactory;

public class ZephyrGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static ZephyrGraphFactory graphFactory = new ZephyrGraphFactory();

	private static final Logger log = LoggerFactory.getLogger(ZephyrGraphMessagingTargetTest.class);

	public static final String URL = "http://192.168.1.106:10002/";
	public static final String TOKEN = "SECRET";

	static {

		cleanup();

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {

				log.info("HTTP LOG: " + ZephyrGraphFactory.DEFAULT_ZEPHYR_API.getZephyrApiLog().size());
			}
		});
	}

	public static void cleanup() {

		graphFactory.setDataApi(URL);
		graphFactory.setOauthToken(TOKEN);

		try {

			ZephyrGraphFactory.DEFAULT_ZEPHYR_API.doDelete(URL + "/?token=" + TOKEN);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}
}
