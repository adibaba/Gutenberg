package de.adrianwilke.gutenberg.content_analyzer;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.adrianwilke.gutenberg.filesystem.TextFileAccessor;

/**
 * Represents text file contents.
 * 
 * @author Adrian Wilke
 */
public class TextFile {

	private Integer contentEndIndex;
	private Integer contentStartIndex;
	private String fileCharsetName;
	private String filePath;
	private List<String> lines;
	private Map<Integer, List<TextPart>> sections;
	private List<TextPart> textParts;
	private List<TextPart> textPartsContent;

	TextFile(String filePath, String charsetName) {
		this.filePath = filePath;
		this.fileCharsetName = charsetName;
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
		if (endIndex > getLines().size() - 1) {
			endIndex = getLines().size() - 1;
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
			sb.append(getLines().get(i));
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	public List<String> getLines() {
		if (lines == null) {
			lines = TextFileAccessor.readFileToString(filePath, fileCharsetName);
		}
		return lines;
	}

	public String getLinesToString() {
		StringBuilder sb = new StringBuilder();
		for (String line : getLines()) {
			sb.append(line);
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	/**
	 * Gets sets of non-empty lines. Lines are trimmed for comparison.
	 * 
	 * Uses {@link TextPart#linesToTextParts(List)}.
	 */
	public List<TextPart> getPartsRaw() {
		if (textParts == null) {
			textParts = TextPart.linesToTextParts(getLines());
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
	public List<TextPart> getPartsCutted() {
		if (textPartsContent == null) {

			boolean startAndEndCutted = new Cutter().setBoundaries(this);
			if (startAndEndCutted) {
				int startIndex = 0;
				int endIndex = getPartsRaw().size() - 1;
				for (int i = 0; i < getPartsRaw().size(); i++) {
					TextPart part = getPartsRaw().get(i);
					if (getContentStartIndex() >= part.getStartIndex()) {
						startIndex = i;
					}
					if (getContentEndIndex() >= part.getEndIndex()) {
						endIndex = i;
					}
				}
				textPartsContent = getPartsRaw().subList(startIndex, endIndex + 1);

			} else {
				System.err.println("Warning: Trying to creating parts of content without context information in "
						+ TextFile.class.getName());
				return getPartsRaw();
			}

		}
		return textPartsContent;
	}

	/**
	 * Get parts depending on distance (a.k.a. empty lines)
	 * 
	 * Uses {@link TextPart#textPartsToSections(List)}.
	 */
	public Map<Integer, List<TextPart>> getSections() {
		if (sections == null) {
			sections = TextPart.textPartsToSections(getPartsCutted());
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

		if (lines != null) {
			sb.append(", ");
			for (int i = 0; i < 5 - String.valueOf(getLines().size()).length(); i++) {
				sb.append(" ");
			}
			sb.append(getLines().size());
			sb.append(" lines");
		}

		if (textParts != null) {
			sb.append(", ");

			for (int i = 0; i < 4 - String.valueOf(getPartsCutted().size()).length(); i++) {
				sb.append(" ");
			}
			sb.append(getPartsCutted().size());

			sb.append("/");
			for (int i = 0; i < 4 - String.valueOf(getPartsRaw().size()).length(); i++) {
				sb.append(" ");
			}
			sb.append(getPartsRaw().size());

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