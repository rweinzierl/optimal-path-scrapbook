package rw.test.opath2.gwtw;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;

import au.com.bytecode.opencsv.CSVReader;

public class MultiRunner {

	public static void main(String[] args) throws Exception {
		File commandFile = new File(args[0]);
		CSVReader r = new CSVReader(new FileReader(commandFile), ' ');
		String[] row;
		while ((row = r.readNext()) != null) {
			String className = row[0];
			String[] args0 = new String[row.length - 1];
			System.arraycopy(row, 1, args0, 0, args0.length);
			Method mainMethod = Class.forName(className).getMethod("main", args.getClass());
			mainMethod.invoke(null, (Object) args0);
		}
		r.close();
	}

}
