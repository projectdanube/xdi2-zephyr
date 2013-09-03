package xdi2.messaging.tests.target.impl.graph;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.zephyr.ZephyrGraphFactory;
import xdi2.core.impl.zephyr.util.ZephyrApi;

public class ZephyrGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static ZephyrGraphFactory graphFactory = new ZephyrGraphFactory();

	private static final Logger log = LoggerFactory.getLogger(ZephyrGraphMessagingTargetTest.class);

	public static final String URL = "http://127.0.0.1:10002/";
	public static final String TOKEN = "SECRET";

	static {

		graphFactory.setDataApi(URL);
		graphFactory.setOauthToken(TOKEN);

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {

				log.info("HTTP LOG: " + ZephyrGraphFactory.DEFAULT_ZEPHYR_API.getZephyrApiLog().size());
			}
		});
	}

	@Override
	protected void setUp() throws Exception {

		super.setUp();

		ZephyrApi.cleanup(URL, TOKEN);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();

		ZephyrApi.cleanup(URL, TOKEN);
	}

	public static void cleanup() {

		graphFactory.setDataApi(URL);
		graphFactory.setOauthToken(TOKEN);
	}

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}
}
