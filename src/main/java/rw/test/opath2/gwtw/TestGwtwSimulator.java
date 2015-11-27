package rw.test.opath2.gwtw;

import junit.framework.TestCase;

public class TestGwtwSimulator extends TestCase {

	public void test() throws Exception {
		String[][] properties = new String[][] {
				new String[] { "isea.dir", "/data/eclipse-workspace/movebank/src/main/java/org/movebank/analysis/wind" },
				new String[] { "netcdf.dir", "/tmp/bart" } };
		for (String[] property : properties)
			System.setProperty(property[0], property[1]);
		String[] args = { "outFile=/tmp/bart/path.csv", "serviceName=pl", "pressureLevel=850", "flightSpeed=10",
				"stepSizeSeconds=3600", "gridResolution=8", "gridAllow2ndLevel=true", "startPointId=27130",
				"startTimestamp=2014-01-02 06:00:00.000", "endPointId=19067" };
		StringBuffer commandLine = new StringBuffer();
		commandLine.append("java");
		for (String[] property : properties)
			commandLine.append(" -D" + property[0] + "=" + property[1]);
		commandLine.append(" " + GwtwSimulator.class.getName());
		for (String arg : args)
			commandLine.append(" " + arg);
		System.out.println(commandLine);
		GwtwSimulator.main(args);
	}

}
