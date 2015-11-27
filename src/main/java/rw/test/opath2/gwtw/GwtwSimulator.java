package rw.test.opath2.gwtw;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import rw.opath2.core.BreakCondition;
import rw.opath2.core.BreakConditionUtil;
import rw.opath2.core.PathFinder;
import rw.opath2.core.Topology;
import rw.opath2.grid.isea.IseaReader;
import rw.opath2.grid.isea.NodeIsea;
import rw.opath2.io.NodeExternalizer;
import rw.opath2.io.NodeWriterCsv;
import rw.opath2.io.NodeWriterCsvUtil;
import rw.opath2.io.PathBackReader;
import rw.opath2.io.PathReader;
import rw.opath2.io.PathWriter;
import rw.opath2.standard.BreakConditionTargetNode;
import rw.opath2.standard.NodeWithTimestamp;
import rw.opath2.wind.TravelTimeCalculatorFactoryWind0;
import rw.opath2.wind.WindSource;

public class GwtwSimulator implements Closeable {
	static class Configuration extends ConfigurationBase1 {

		int gridResolution;
		boolean gridAllow2ndLevel;
		long startPointId;
		Date startTimestamp;
		Long endPointId;

		public Configuration(String[] args) throws Exception {
			super(args);
			gridResolution = getIntValue(argMap, "gridResolution", "9");
			gridAllow2ndLevel = getBooleanValue(argMap, "gridAllow2ndLevel", "true");
			startPointId = getLongValue(argMap, "startPointId");
			startTimestamp = getDateValue(argMap, "startTimestamp");
			endPointId = getLongValue2(argMap, "endPointId");
		}

	}

	WindSource r;
	List<NodeIsea> nodes;
	Topology<NodeIsea> topology = NodeIsea.TOPOLOGY;

	public GwtwSimulator(Configuration config) throws Exception {
		checkProperties("isea.dir", "netcdf.dir");
		r = EcmwfConfig.get(config.serviceName, config.pressureLevel).createWindSource(config.startTimestamp,
				1000L * 60 * 60 * 24 * config.maxDays);
		nodes = IseaReader.getGridCells(config.gridResolution);
		if (config.gridAllow2ndLevel)
			NodeIsea.addSecondOrderNeighbours(nodes);
	}

	NodeIsea findNode(List<NodeIsea> nodes, long number) {
		for (NodeIsea node : nodes)
			if (node.number == number)
				return node;
		return null;
	}

	void checkProperties(String... names) {
		for (String name : names)
			if (System.getProperty(name) == null)
				throw new RuntimeException("property " + name + " not set ");
	}

	// set properties "isea.dir" and "netcdf.dir"
	public void run(Configuration config) throws Exception {
		NodeIsea startNode = findNode(nodes, config.startPointId);
		NodeIsea endNode;
		if (config.endPointId != null)
			endNode = findNode(nodes, config.endPointId);
		else
			endNode = null;
		TravelTimeCalculatorFactoryWind0<NodeIsea> cf = new TravelTimeCalculatorFactoryWind0<>(config.flightSpeed,
				config.stepSizeSeconds, r);
		File filePathsTmp = new File(config.outFile.getPath() + ".tmp");
		NodeExternalizer<NodeIsea> externalizer = NodeIsea.EXTERNALIZER;
		BreakCondition<NodeIsea> breakCondition;
		if (endNode != null)
			breakCondition = new BreakConditionTargetNode<NodeIsea>(endNode);
		else
			breakCondition = BreakConditionUtil.defaultBreakCondition();
		final PathWriter<NodeIsea> pathWriter = new PathWriter<NodeIsea>(externalizer, filePathsTmp);
		PathFinder<NodeIsea> pathFinder = new PathFinder<>(topology, cf, pathWriter, breakCondition);
		pathFinder.init(startNode, config.startTimestamp.getTime());
		pathFinder.run();
		pathWriter.close();
		NodeWriterCsv<NodeIsea> csvWriter = NodeIsea.CSV_WRITER;
		FileWriter w = new FileWriter(config.outFile);
		if (endNode != null) {
			PathBackReader<NodeIsea> pathReader = new PathBackReader<NodeIsea>(filePathsTmp, externalizer);
			List<NodeWithTimestamp<NodeIsea>> path = pathReader.findPathToTarget(endNode);
			pathReader.close();
			NodeWriterCsvUtil.writePath(path, csvWriter, w);
			//KmlPathVisualizer.toKml(path, new File(config.outFile.getPath() + ".kml"), true);
		} else {
			PathReader<NodeIsea> pr = new PathReader<>(filePathsTmp, externalizer);
			NodeWriterCsvUtil.writePath(pr, csvWriter, w);
			pr.close();
		}
		w.close();
		filePathsTmp.delete();
	}

	@Override
	public void close() throws IOException {
		r.close();
	}

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		Configuration config = new Configuration(args);
		GwtwSimulator sim = new GwtwSimulator(config);
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000.0 + " seconds");
		sim.run(config);
		sim.close();
		end = System.currentTimeMillis();
		System.out.println((end - start) / 1000.0 + " seconds");
	}

}
