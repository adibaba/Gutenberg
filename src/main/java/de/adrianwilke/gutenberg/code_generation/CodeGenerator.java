package de.adrianwilke.gutenberg.code_generation;

import java.io.File;
import java.util.Arrays;
import java.util.Map.Entry;

import de.adrianwilke.gutenberg.Gutenberg;
import de.adrianwilke.gutenberg.entities.DcFormat;

/**
 * Generates code as author of software is lazy.
 * 
 * @author Adrian Wilke
 */
public class CodeGenerator {

	public static String generateDcFormats() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Integer> entry : DcFormat.getDcFormats(false).entrySet()) {
			sb.append("public static String TYPE_CASE_INSENSITIVE_");
			sb.append(entry.getKey().toUpperCase().replace(".", "_").replace("-", "_"));
			sb.append(" = \"");
			sb.append(entry.getKey());
			sb.append("\";");
			sb.append(" // ");
			Integer count = entry.getValue();
			if (count > 1000) {
				sb.append(count / 1000);
				sb.append("K");
			} else if (count > 100) {
				sb.append(Math.round(count));
			} else {
				sb.append(count);
			}
			sb.append(System.lineSeparator());
		}
		// "zip"; // 136222
		// public static String TYPE_TXT = "txt"; // 88452
		return sb.toString();
	}

	public static void main(String[] args) {

		String tdbDirectory = null;

		if (args.length == 1) {
			tdbDirectory = args[0];
		} else {
			System.err.println("Please set Jena TDB directory");
			System.exit(1);
		}

		File directoryTdb = new File(tdbDirectory);
		if (!directoryTdb.isDirectory()
				|| !directoryTdb.canRead()
				|| !(Arrays.asList(directoryTdb.list())).contains("GOSP.dat")) {
			System.err.println("Error: Can not read Jena TDB directory: " + tdbDirectory);
			System.exit(1);
		}

		Gutenberg.getInstance(tdbDirectory);
		System.out.println("TDB directory: " + tdbDirectory);

		System.out.println(generateDcFormats());
	}

}
