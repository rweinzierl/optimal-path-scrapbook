package rw.test.opath2.gwtw;

import junit.framework.TestCase;

public class TestPathEvaluator extends TestCase {

	String[][] properties = new String[][] {
			new String[] { "isea.dir", "/data/eclipse-workspace/movebank/src/main/java/org/movebank/analysis/wind" },
			new String[] { "netcdf.dir", "/tmp/bart" } };

	String[] args0 = { "outFile=/tmp/bart/lng-lat-out.csv", "inFile=/tmp/bart/lng-lat.csv", "serviceName=pl",
			"pressureLevel=850", "flightSpeed=10", "stepSizeSeconds=3600", "startTimestamp=2014-01-02 06:00:00.000" };

	String[] args1 = { "outFile=/tmp/fileBartOut", "inFile=/tmp/fileBart", "serviceName=sfc", "flightSpeed=10",
			"stepSizeSeconds=3600", "maxDays=30", "startTimestamp=1993-04-01 11:03:31.410" };

	protected void setSystemProperties() {
		for (String[] property : properties)
			System.setProperty(property[0], property[1]);
	}

	@Override
	protected void setUp() throws Exception {
		setSystemProperties();
	}

	public void test() throws Exception {
		StringBuffer commandLine = new StringBuffer();
		commandLine.append("java");
		for (String[] property : properties)
			commandLine.append(" -D" + property[0] + "=" + property[1]);
		commandLine.append(" " + PathEvaulator.class.getName());
		for (String arg : args0)
			commandLine.append(" " + arg);
		System.out.println(commandLine);
		PathEvaulator.main(args0);
	}

	public void test2() throws Exception {
		long start = System.currentTimeMillis();
		PathEvaulator.Configuration config = new PathEvaulator.Configuration(args0);
		PathEvaulator eval = new PathEvaulator(config);
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000.0 + " seconds");
		for (int i = 0; i < 5; i++) {
			eval.run(config);
			end = System.currentTimeMillis();
			System.out.println((end - start) / 1000.0 + " seconds");
		}
		eval.close();
	}

	public void test3() throws Exception {
		PathEvaulator.Configuration config = new PathEvaulator.Configuration(args1);
		PathEvaulator eval = new PathEvaulator(config);
		eval.run(config);
		eval.close();
	}

}
