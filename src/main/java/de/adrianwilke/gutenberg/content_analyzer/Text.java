package de.adrianwilke.gutenberg.content_analyzer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.adrianwilke.gutenberg.io.TextFileAccessor;

/**
 * Represents text contents.
 * 
 * @author Adrian Wilke
 */
public class Text {

	private Integer contentEndIndex;
	private Integer contentStartIndex;
	private String fileCharsetName;
	private String filePath;
	private List<String> linesCleaned;
	private List<String> linesTrimmed;
	private Map<Integer, List<TextPart>> sections;
	private List<TextPart> textParts;
	private List<TextPart> textPartsCut;

	/**
	 * Gets text from file.
	 */
	Text(String filePath, String charsetName) {
		this.filePath = filePath;
		this.fileCharsetName = charsetName;
	}

	private String cleanLine(String line) {
		line = line.replace("[Illustration]", "");
		return new String(line);
	}

	/**
	 * @return null, if not set
	 */
	public Integer getContentEndIndex() {
		return contentEndIndex;
	}

	/**
	 * @return null, if not set
	 */
	public Integer getContentEndLineNumber() {
		if (contentEndIndex == null) {
			return null;
		} else {
			return contentEndIndex + 1;
		}
	}

	/**
	 * @return null, if not set
	 */
	public Integer getContentStartIndex() {
		return contentStartIndex;
	}

	/**
	 * @return null, if not set
	 */
	public Integer getContentStartLineNumber() {
		if (contentStartIndex == null) {
			return null;
		} else {
			return contentStartIndex + 1;
		}
	}

	public String getContext(int lineNumber, int range) {
		StringBuilder sb = new StringBuilder();
		int lineIndex = lineNumber - 1;

		int startIndex = lineIndex - range;
		if (startIndex < 0) {
			startIndex = 0;
		}
		int endIndex = lineIndex + range;
		if (endIndex > getLinesTrimmed().size() - 1) {
			endIndex = getLinesTrimmed().size() - 1;
		}

		int endLineNumberLength = String.valueOf(endIndex).length();
		for (int i = startIndex; i <= endIndex; i++) {
			for (int j = 0; j < endLineNumberLength - String.valueOf(i).length(); j++) {
				sb.append(" ");
			}
			if (i == lineIndex) {
				sb.append(" ");
			}
			sb.append(i + 1);
			sb.append(" ");
			sb.append(getLinesTrimmed().get(i));
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	// TODO: Never used
	public List<String> getLinesCleaned() {
		if (linesCleaned == null) {
			linesCleaned = new LinkedList<String>();
			for (String line : linesTrimmed) {
				linesCleaned.add(cleanLine(line));
			}
		}
		return linesCleaned;
	}

	public String getLinesToString() {
		StringBuilder sb = new StringBuilder();
		for (String line : getLinesTrimmed()) {
			sb.append(line);
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	public List<String> getLinesTrimmed() {
		if (linesTrimmed == null) {
			linesTrimmed = TextFileAccessor.readFileToString(filePath, fileCharsetName, true);
		}
		return linesTrimmed;
	}

	/**
	 * Gets sets of non-empty lines. Lines are trimmed for comparison.
	 * 
	 * Uses {@link TextPart#linesToTextParts(List)}.
	 */
	public List<TextPart> getParts() {
		if (textParts == null) {
			textParts = TextPart.linesToTextParts(getLinesTrimmed());
		}
		return textParts;
	}

	/**
	 * Tries to cut non-content at top and bottom of file.
	 * 
	 * On success, the trimmed parts are returned.
	 * 
	 * Otherwise, a warning is displayed and unchanged text parts are returned.
	 */
	public List<TextPart> getPartsCut() {
		if (textPartsCut == null) {

			boolean startAndEndCutted = new Cutter().setBoundaries(this);
			if (startAndEndCutted) {
				int startIndex = 0;
				int endIndex = getParts().size() - 1;
				for (int i = 0; i < getParts().size(); i++) {
					TextPart part = getParts().get(i);
					if (getContentStartIndex() >= part.getStartIndex()) {
						startIndex = i;
					}
					if (getContentEndIndex() >= part.getEndIndex()) {
						endIndex = i;
					}
				}

				// TODO: Produced NPE
				// textPartsCut =
				// TextPart.linesToTextParts(getLinesCleaned()).subList(startIndex, endIndex +
				// 1);
				textPartsCut = getParts().subList(startIndex, endIndex + 1);

			} else {
				System.err.println("Warning: Trying to creating parts of content without context information in "
						+ Text.class.getName());
				return getParts();
			}

		}
		return textPartsCut;
	}

	/**
	 * Get parts depending on distance (a.k.a. empty lines)
	 * 
	 * Uses {@link TextPart#textPartsToSections(List)}.
	 */
	public Map<Integer, List<TextPart>> getSections() {
		if (sections == null) {
			sections = TextPart.textPartsToSections(getPartsCut());
		}
		return sections;
	}

	public void setContentEndIndex(Integer contentEndIndex) {
		this.contentEndIndex = contentEndIndex;
	}

	public void setContentStartIndex(Integer contentStartIndex) {
		this.contentStartIndex = contentStartIndex;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		File file = new File(filePath);
		for (int i = 0; i < 11 - file.getName().length(); i++) {
			sb.append(" ");
		}
		sb.append(file.getName());

		if (getContentStartLineNumber() != null && getContentEndLineNumber() != null) {
			sb.append(",  content: [");
			sb.append(getContentStartLineNumber());
			sb.append(",");
			sb.append(getContentEndLineNumber());
			sb.append("]");
		}

		if (linesTrimmed != null) {
			sb.append(", ");
			for (int i = 0; i < 5 - String.valueOf(getLinesTrimmed().size()).length(); i++) {
				sb.append(" ");
			}
			sb.append(getLinesTrimmed().size());
			sb.append(" lines");
		}

		if (textParts != null) {
			sb.append(", ");

			for (int i = 0; i < 4 - String.valueOf(getPartsCut().size()).length(); i++) {
				sb.append(" ");
			}
			sb.append(getPartsCut().size());

			sb.append("/");
			for (int i = 0; i < 4 - String.valueOf(getParts().size()).length(); i++) {
				sb.append(" ");
			}
			sb.append(getParts().size());

			sb.append(" parts");
		}

		if (file.exists()) {
			long kibibytes = file.length() / 1024;
			sb.append(",  size: ");
			for (int i = 0; i < 4 - String.valueOf(kibibytes).length(); i++) {
				sb.append(" ");
			}
			sb.append(kibibytes);
			sb.append(" KiB");

		}

		return sb.toString();
	}
}