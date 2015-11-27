package rw.test.opath2.gwtw;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import rw.netcdf.FileFilterRegex;
import rw.opath2.netcdf.WindSourceNetcdf1;
import rw.opath2.wind.WindSource;

public class EcmwfConfig {

	public String serviceName;
	public int pressureLevel;
	public FileFilterRegex[] fileFilters;
	public String[] variableNames;
	public File path;

	public static EcmwfConfig get(String serviceName, int pressureLevel) {
		File netcdfPath = new File(System.getProperty("netcdf.dir"));
		EcmwfConfig config = new EcmwfConfig();
		config.serviceName = serviceName;
		config.pressureLevel = pressureLevel;
		config.path = new File(netcdfPath, serviceName);
		if ("sfc".equals(serviceName)) {
			config.fileFilters = new FileFilterRegex[] { new FileFilterRegex(".*165\\.128.*\\.grib"),
					new FileFilterRegex(".*166\\.128.*\\.grib") };
			config.variableNames = new String[] { "10_metre_U_wind_component_surface",
					"10_metre_V_wind_component_surface" };
		} else if ("pl".equals(serviceName)) {
			config.fileFilters = new FileFilterRegex[] { new FileFilterRegex(".*131\\.128.*\\.grib"),
					new FileFilterRegex(".*132\\.128.*\\.grib") };
			config.variableNames = new String[] { "U_velocity_isobaric", "V_velocity_isobaric" };
			config.path = new File(config.path, pressureLevel + "");
		}
		return config;
	}

	public WindSource createWindSource() throws Exception {
		return new WindSourceNetcdf1(path, fileFilters, 0, variableNames);
	}

	public WindSource createWindSource(Date startTimestamp, long maxDurationMillis) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		final String start = df.format(startTimestamp);
		final String end = df.format(new Date(startTimestamp.getTime() + maxDurationMillis));
		FileFilter monthFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String month = pathname.getName().substring(5, 5 + 6);
				return start.compareTo(month) <= 0 && end.compareTo(month) >= 0;
			}
		};
		FileFilter[] fileFilters2 = new FileFilter[fileFilters.length];
		for (int i = 0; i < fileFilters.length; i++)
			fileFilters2[i] = fileFilters[i].narrow(monthFilter);
		return new WindSourceNetcdf1(path, fileFilters2, 0, variableNames);
	}

}
