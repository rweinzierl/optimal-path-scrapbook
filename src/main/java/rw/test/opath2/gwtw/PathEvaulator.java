package rw.test.opath2.gwtw;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import rw.opath2.core.NodeLngLat;
import rw.opath2.netcdf.NoDataException;
import rw.opath2.wind.TravelTimeCalculatorFactoryWind0;
import rw.opath2.wind.WindSource;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class PathEvaulator implements Closeable {

	public static final Logger logger = Logger.getLogger(PathEvaulator.class.getName());

	private TravelTimeCalculatorFactoryWind0<NodeLngLat> evaluator2;

	public PathEvaulator(Configuration config) throws Exception {
		if (System.getProperty("netcdf.dir") == null)
			throw new RuntimeException("property " + "netcdf.dir" + " not set ");
		WindSource windSource = EcmwfConfig.get(config.serviceName, config.pressureLevel).createWindSource(
				config.startTimestamp, 1000L * 60 * 60 * 24 * config.maxDays);
		evaluator2 = new TravelTimeCalculatorFactoryWind0<>(config.flightSpeed, config.stepSizeSeconds, windSource);
	}

	private static class Coordinates implements NodeLngLat {
		double timestamp;
		double lng;
		double lat;

		@Override
		public double getLng() {
			return lng;
		}

		@Override
		public double getLat() {
			return lat;
		}
	}

	@SuppressWarnings("unchecked")
	private List<Coordinates> readTrack(File file) throws Exception {
		logger.info("Reading track from " + file.getAbsolutePath());
		FileReader r = new FileReader(file);
		CSVReader cr = new CSVReader(r, ',');
		List<String[]> records = cr.readAll();
		cr.close();
		records = records.subList(1, records.size());
		List<Coordinates> track = new ArrayList<Coordinates>();
		for (String[] record : records) {
			Coordinates coordinates = new Coordinates();
			coordinates.lng = Double.parseDouble(record[0]);
			coordinates.lat = Double.parseDouble(record[1]);
			track.add(coordinates);
		}
		return track;
	}

	private void writeTrack(List<Coordinates> track, File file) throws Exception {
		logger.info("Writing track to " + file.getAbsolutePath());
		FileWriter w = new FileWriter(file);
		CSVWriter cw = new CSVWriter(w);
		cw.writeNext(new String[] { "lon", "lat", "timestamp-millis-since-1970" });
		for (Coordinates coordinates : track) {
			cw.writeNext(new String[] { coordinates.getLng() + "", coordinates.getLat() + "",
					((long) coordinates.timestamp) + "" });
		}
		cw.close();
	}

	void run(Configuration config) throws Exception {
		List<Coordinates> track = readTrack(config.inFile);
		for (Coordinates coordinates : track)
			coordinates.timestamp = -1;
		track.get(0).timestamp = config.startTimestamp.getTime();
		try {
			for (int i = 1; i < track.size(); i++) {
				double timestamp = track.get(i - 1).timestamp;
				track.get(i).timestamp = evaluator2.getArrivalTimestamp(track.get(i - 1), track.get(i), timestamp);
			}
		} catch (NoDataException e) {
		}
		writeTrack(track, config.outFile);
	}

	@Override
	public void close() throws IOException {
	}

	static class Configuration extends ConfigurationBase1 {

		Date startTimestamp;
		protected File inFile;

		public Configuration(String[] args) throws Exception {
			super(args);
			inFile = getFileValue(argMap, "inFile");
			startTimestamp = getDateValue(argMap, "startTimestamp");
		}
	}

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		Configuration config = new Configuration(args);
		PathEvaulator eval = new PathEvaulator(config);
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000.0 + " seconds");
		eval.run(config);
		eval.close();
		end = System.currentTimeMillis();
		System.out.println((end - start) / 1000.0 + " seconds");
	}

}
