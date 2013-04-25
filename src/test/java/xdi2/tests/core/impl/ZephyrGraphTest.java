package xdi2.tests.core.impl;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.zephyr.ZephyrGraphFactory;
import xdi2.tests.core.graph.AbstractGraphTest;

public class ZephyrGraphTest extends AbstractGraphTest {

	private static ZephyrGraphFactory graphFactory = new ZephyrGraphFactory();

	public static final String URL = "http://192.168.1.100:10002";
	public static final String TOKEN = "SECRET";

	static {

		cleanup();

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {

				System.err.println(ZephyrGraphFactory.DEFAULT_ZEPHYR_UTILS.getHttpLog().size());
			}
		});
	}

	public static void cleanup() {

		graphFactory.setDataApi(URL);
		graphFactory.setOauthToken(TOKEN);

		try {

			ZephyrGraphFactory.DEFAULT_ZEPHYR_UTILS.doDelete(URL + "/?token=" + TOKEN);
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