package xdi2.core.impl.zephyr.util;

import java.util.ArrayList;

public class ZephyrCacheLog extends ArrayList<String> {

	private static final long serialVersionUID = -6754894317139540409L;

	private int countGet;
	private int countPut;
	private int countRemove;
	private int countHit;
	private int countMiss;

	public ZephyrCacheLog() {

		super();

		this.countGet = 0;
		this.countPut = 0;
		this.countRemove = 0;
		this.countHit = 0;
		this.countMiss = 0;
	}

	public void add(String operation, String graphContextNodePath) {

		if ("get()".equals(operation)) this.countGet++;
		if ("put()".equals(operation)) this.countPut++;
		if ("remove()".equals(operation)) this.countRemove++;

		StringBuilder builder = new StringBuilder();

		builder.append(operation + " " + graphContextNodePath + "\n");

		this.add(builder.toString());
	}

	public void hit() {

		this.countHit++;
	}

	public void miss() {

		this.countMiss++;
	}

	@Override
	public void clear() {

		super.clear();

		this.countGet = 0;
		this.countPut = 0;
		this.countRemove = 0;
		this.countHit = 0;
		this.countMiss = 0;
	}

	public int getCountGet() {

		return this.countGet;
	}

	public int getCountPut() {

		return this.countPut;
	}

	public int getCountRemove() {

		return this.countRemove;
	}

	public int getCountHit() {

		return this.countHit;
	}

	public int getCountMiss() {

		return this.countMiss;
	}
}
