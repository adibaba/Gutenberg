package de.adrianwilke.gutenberg.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import de.adrianwilke.gutenberg.exceptions.FileAccessRuntimeException;

/**
 * Reads text files.
 * 
 * @author Adrian Wilke
 */
public class TextFileAccessor {

	public static final String ISO_8859_1 = StandardCharsets.ISO_8859_1.name();
	public static final String UTF_8 = StandardCharsets.UTF_8.name();

	/**
	 * @throws FileAccessRuntimeException
	 * 
	 * @see java.nio.charset.StandardCharsets
	 */
	public static List<String> readFileToString(String filePath, String charsetName) {
		List<String> lines = new LinkedList<String>();

		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		try {
			File file = new File(filePath);
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charsetName);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}

		} catch (IOException e) {
			throw new FileAccessRuntimeException(e);
		} finally {
			try {
				fileInputStream.close();
				inputStreamReader.close();
				bufferedReader.close();
			} catch (IOException e) {
				throw new FileAccessRuntimeException(e);
			}
		}
		return lines;
	}

	/**
	 * @throws FileAccessRuntimeException
	 */
	public static void writeStringToFile(String string, String filePath) {
		try {
			PrintWriter out = new PrintWriter(filePath);
			out.println(string);
			out.close();
		} catch (FileNotFoundException e) {
			throw new FileAccessRuntimeException(e);
		}
	}
}