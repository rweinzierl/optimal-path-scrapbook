package rw.test.opath2.gwtw;

import java.io.File;

class ConfigurationBase1 extends ConfigurationBase {

	protected String serviceName;
	protected int pressureLevel;
	protected double flightSpeed;
	protected double stepSizeSeconds;
	protected int maxDays;
	protected File outFile;

	public ConfigurationBase1(String[] args) {
		super(args);
		outFile = getFileValue(argMap, "outFile");
		serviceName = getValue(argMap, "serviceName");
		pressureLevel = getIntValue(argMap, "pressureLevel", "-1");
		flightSpeed = getDoubleValue(argMap, "flightSpeed", "10");
		stepSizeSeconds = getDoubleValue(argMap, "stepSizeSeconds", "3600");
		maxDays = getIntValue(argMap, "maxDays", "366");
	}

}
