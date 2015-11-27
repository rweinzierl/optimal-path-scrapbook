package rw.test.opath2.gwtw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GwtwFileSynchronizer {

	static File sourceDir = new File("/ptmp/mpb/rowein/raster/ecmwf2/interim_full_daily");
	static File targetDir = new File("/ptmp/mpb/rowein/gradle-projects/bart/netcdf");

	public static void main(String[] args) throws Exception {
		String[] pressureLevels = { "700", "850", "925" };
		for (int year = 1990; year <= 2011; year++)
			for (int month = 0; month < 12; month++) {
				String m = (month + 1) + "";
				if (m.length() == 1)
					m = "0" + m;
				String ym = year + m;
				processFile("sfc", ym + "/" + "data-" + ym + "-165.128.grib", null);
				processFile("sfc", ym + "/" + "data-" + ym + "-166.128.grib", null);
				for (String pressureLevel : pressureLevels) {
					processFile("pl", ym + "/" + "data-" + ym + "-131.128-" + pressureLevel + ".grib", pressureLevel);
					processFile("pl", ym + "/" + "data-" + ym + "-132.128-" + pressureLevel + ".grib", pressureLevel);
				}
			}
	}

	protected static void processFile(String type, String fileName, String subDir) throws IOException {
		Path sourceFile = new File(sourceDir, type + "/" + fileName).toPath();
		Path targetFile = new File(targetDir, subDir != null ? (type + "/" + subDir + "/" + fileName)
				: (type + "/" + fileName)).toPath();
		if (Files.exists(sourceFile)) {
			if (!Files.exists(targetFile) || Files.size(sourceFile) != Files.size(targetFile)) {
				System.out.println("copying file " + sourceFile + " to " + targetFile);
				targetFile.getParent().toFile().mkdirs();
				Files.copy(sourceFile, targetFile);
			}
		} else
			System.out.println("missing file " + sourceFile);
	}

}
