package com.iksgmbh.helper;

import com.iksgmbh.data.Annotation;

/**
 * Creates a Annotation Object from a text line.
 * Valid lines are one of the following:
 * <ANNOTATION_PREFIX_IDENTIFICATOR><FirstPart><DEFAULT_PART_SEPARATOR><SecondPart><DEFAULT_COMMENT_IDENTIFICATOR><comment>
 * or to use spaces within a part
 * <ANNOTATION_PREFIX_IDENTIFICATOR><PART_BRACE_IDENTIFIER><FirstPart><PART_BRACE_IDENTIFIER><SecondPart><DEFAULT_COMMENT_IDENTIFICATOR><comment>
 *
 * @author Reik Oberrath
 */
public class AnnotationParser {

	public static final String DEFAULT_PART_SEPARATOR = " "; // not customizable
	public static final String DEFAULT_PART_BRACE_IDENTIFIER = "\"";
	public static final String DEFAULT_ANNOTATION_PREFIX_IDENTIFICATOR = "@";
	public static final String DEFAULT_COMMENT_IDENTIFICATOR = "#";

	public static final String ERROR = "PARSER ERROR: ";

	private static AnnotationParser annotationParserInstance;

	protected String annotationPrefixIdentificator;
	protected String commentIdentificator;
	protected String partBraceIdentifier;

	protected AnnotationParser(final String annotationPrefixIdentificator,
							   final String partBraceIdentifier,
			                   final String commentIdentificator) {
		this.annotationPrefixIdentificator = annotationPrefixIdentificator;
		this.partBraceIdentifier = partBraceIdentifier;
		this.commentIdentificator = commentIdentificator;
	}

	public static AnnotationParser getInstance(final String annotationPrefixIdentificator,
			                                   final String commentIdentificator) {
		annotationParserInstance = new AnnotationParser(annotationPrefixIdentificator,
				                                        DEFAULT_PART_BRACE_IDENTIFIER,
				                                        commentIdentificator);
		return annotationParserInstance;
	}

	public static AnnotationParser getInstance(final String annotationPrefixIdentificator) {
		annotationParserInstance = new AnnotationParser(annotationPrefixIdentificator,
														DEFAULT_PART_BRACE_IDENTIFIER,
				                                        DEFAULT_COMMENT_IDENTIFICATOR);
		return annotationParserInstance;
	}

	public static AnnotationParser getInstance() {
		annotationParserInstance = new AnnotationParser(DEFAULT_ANNOTATION_PREFIX_IDENTIFICATOR,
														DEFAULT_PART_BRACE_IDENTIFIER,
				                                        DEFAULT_COMMENT_IDENTIFICATOR);
		return annotationParserInstance;
	}

	/**
	 * Performs case internsitive prefix check
	 * @param line
	 * @return true if first
	 */
	public boolean hasCorrectPrefix(final String line) {
		// a prefix may have leading or trailing spaces !
		return line.trim().toLowerCase().startsWith(annotationPrefixIdentificator.trim().toLowerCase());
	}

	public Annotation doYourJob(String line) {
		Annotation toReturn = null;
		if (hasCorrectPrefix(line)) {
			line = cutPrefix(line);
			final AnnotationContentParts parts = getAnnotationContentParts(line);
			toReturn = new Annotation(parts.firstPart);
			toReturn.setAdditionalInfo(parts.secondPart);
		}
		return toReturn;
	}

	public AnnotationContentParts getAnnotationContentParts(final String line) {
		try {
			if (line.startsWith(partBraceIdentifier)) {
				return parseWithBraces(line);
			} else {
				return parseWithSeparator(line.trim());
			}
		} catch (RuntimeException e) {
			final AnnotationContentParts toReturn = new AnnotationContentParts();
			toReturn.firstPart = ERROR + e.getMessage();
			return toReturn;
		}
	}

	private AnnotationContentParts parseWithSeparator(final String line) {
		final AnnotationContentParts toReturn = new AnnotationContentParts();
		int pos = line.indexOf(DEFAULT_PART_SEPARATOR);
		if (pos == -1) {
			toReturn.firstPart = line;
		} else {
			toReturn.firstPart = line.substring(0, pos);
			toReturn.secondPart = line.substring(pos + 1).trim();
		}
		return toReturn;
	}

	private AnnotationContentParts parseWithBraces(final String line) {
		final AnnotationContentParts toReturn = new AnnotationContentParts();
		String tmpLine = line.substring(partBraceIdentifier.length());
		int pos = tmpLine.indexOf(partBraceIdentifier);
		if (pos == -1) {
			throwParseException(line);
		}
		toReturn.firstPart = tmpLine.substring(0, pos);
		pos = pos + partBraceIdentifier.length()
				  + DEFAULT_PART_SEPARATOR.length();
		if (tmpLine.length() > pos) {
			toReturn.secondPart = tmpLine.substring(pos);
		}
		return toReturn;
	}

	private void throwParseException(final String line) {
		throw new RuntimeException("Unable to parse annotated line content: " +
				                    "<" + line + ">");
	}

	protected String cutPrefix(String line) {
		// a prefix may have leading or trailing spaces
		line = line.trim().substring(annotationPrefixIdentificator.trim().length()).trim();
		final int pos = line.indexOf(commentIdentificator);
		if (pos == -1) {
			return line;
		}
		return line.substring(0, pos).trim();
	}

	public String getAnnotationPrefixIdentificator() {
		return annotationPrefixIdentificator;
	}

	public String getCommentIdentificator() {
		return commentIdentificator;
	}

	public boolean isCommentLine(final String line) {
		return line.trim().startsWith(commentIdentificator);
	}

	public class AnnotationContentParts {
		String firstPart;
		String secondPart;

		public String getFirstPart() {
			return firstPart;
		}

		public String getSecondPart() {
			return secondPart;
		}
	}
}
