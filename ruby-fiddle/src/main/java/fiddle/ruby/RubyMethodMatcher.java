package fiddle.ruby;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import fiddle.api.Fiddle;

public class RubyMethodMatcher {

	private final String tagName;
	
	private final String tagMatchingPattern;

	private final static Pattern methodPattern = Pattern
			.compile("^\\s*def\\s+(\\w+)");

	private final Logger LOG = LoggerFactory.getLogger(RubyAnalyzer.class);

	public RubyMethodMatcher(final String tagName) {
		this.tagName = tagName;
		this.tagMatchingPattern = "\\s*\\#\\s*\\@" + tagName + "(\\s+.*){0,1}";
	}

	private Optional<String> findMethodInLine(final String line) {
		if (line.matches("\\s*def\\s+.*")) {
			final Matcher matcher = methodPattern.matcher(line);
			if (matcher.find()) {
				LOG.debug("found method in line, attempt to find method name...");
				if (matcher.groupCount() > 0 && null != matcher.group(1)) {
					LOG.debug("found method name {}", matcher.group(1));
					return Optional.of(matcher.group(1));
				}
			}
		}
		return Optional.absent();
	}

	private boolean findTagInLine(final String line) {
		return line.matches(tagMatchingPattern);
	}
	
	private boolean methodMatchHealthCheckConvention(final String method) {
		return tagName.toLowerCase().matches(method.toLowerCase());
	}

	public List<String> methodsFor(final Fiddle fiddle) {
		boolean foundTag = false;

		final List<String> foundMethods = new LinkedList<String>();

		for (final String line : Splitter.onPattern("\r?\n").split(
				fiddle.content)) {

			if (!foundTag) {
				foundTag = findTagInLine(line);
			}

			final Optional<String> method = findMethodInLine(line);
			
			if (foundTag) {
				LOG.debug("found {} tag, looking for method...", tagName);
				if (method.isPresent()) {
					foundMethods.add(method.get());
					foundTag = false;
				}
			}
			else {
				if(method.isPresent() && methodMatchHealthCheckConvention(method.get())) {
					foundMethods.add(method.get());
				}
			}
		}

		return ImmutableList.<String> builder().addAll(foundMethods).build();

	}

}
