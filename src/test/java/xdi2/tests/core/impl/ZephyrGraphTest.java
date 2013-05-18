package xdi2.tests.core.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.zephyr.ZephyrGraphFactory;
import xdi2.tests.core.graph.AbstractGraphTest;

public class ZephyrGraphTest extends AbstractGraphTest {

	private static ZephyrGraphFactory graphFactory = new ZephyrGraphFactory();

	private static final Logger log = LoggerFactory.getLogger(ZephyrGraphTest.class);

	public static final String URL = "http://127.0.0.1:10002/";
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

	@Override
	protected Graph reopenGraph(Graph graph, String identifier) throws IOException {

		graph.close();

		return graphFactory.openGraph(identifier);
	}
}
