package xdi2.core.impl.zephyr.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpLog extends ArrayList<String> {

	private static final long serialVersionUID = -6754894317139540409L;

	private int countGet;
	private int countPut;
	private int countDelete;

	public HttpLog() {

		super();

		this.countGet = 0;
		this.countPut = 0;
		this.countDelete = 0;
	}

	public void add(String method, String url) {

		if ("GET".equals(method)) this.countGet++;
		if ("PUT".equals(method)) this.countPut++;
		if ("DELETE".equals(method)) this.countDelete++;

		StringBuilder builder = new StringBuilder();

		builder.append(method + " " + url + "\n");
		for (String line : lines()) builder.append("     " + line + "\n");
		builder.append("\n");

		this.add(builder.toString());
	}

	@Override
	public void clear() {
		
		super.clear();
		
		this.countGet = 0;
		this.countPut = 0;
		this.countDelete = 0;
	}
	
	public int getCountGet() {

		return this.countGet;
	}

	public int getCountPut() {

		return this.countPut;
	}

	public int getCountDelete() {

		return this.countDelete;
	}

	private static List<String> lines() {

		List<String> skipMethods = Arrays.asList(new String[] { "lines", "add", "doGet", "doPut", "doDelete" });
		List<String> lines = new ArrayList<String> ();
		Exception ex = new Exception();

		for (StackTraceElement stackTraceElement : Arrays.asList(ex.getStackTrace())) {

			String className = stackTraceElement.getClassName();
			String methodName = stackTraceElement.getMethodName();

			if (skipMethods.contains(methodName)) continue;
			if (! className.startsWith("xdi2.core") && ! className.startsWith("xdi2.messaging")) continue;

			lines.add(stackTraceElement.toString());
		}

		return lines;
	}
}
