package fiddle.ruby;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.yammer.metrics.core.HealthCheck;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.resources.Resources;

public class RubyAnalyzer {

	private final RubyExecutor executor;
	
	private final static Pattern methodPattern = Pattern.compile("^\\s*def\\s+(\\w+)");
	
	private final Logger LOG = LoggerFactory.getLogger(RubyAnalyzer.class);
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	private static JsonNode EMPTY_NODE;
	
	static {
		try {
			EMPTY_NODE = MAPPER.readTree("{}");
		} catch(Exception e) {
			EMPTY_NODE = null;
		}
	}
	
	private static class RubyHealthCheck extends HealthCheck {
		
		private final RubyExecutor executor;
		
		private final FiddleId id;
		private final Fiddle fiddle;
		private final String method;
		
		public RubyHealthCheck(final RubyExecutor executor, final FiddleId id, final Fiddle fiddle, final String method) {
			super("fiddle/with/" + id.toString() + "/" + method);
			this.executor = executor;
			this.id = id;
			this.fiddle = fiddle;
			this.method = method;
		}
		
		@Override
		protected Result check() throws Exception {
			final Response response = executor.executeMethodIn(Resources.EMPTY, id, fiddle, method, EMPTY_NODE);
			if(response.getStatus() >= 200 && response.getStatus() < 300) {
				return Result.healthy();
			}
			else {
				return Result.unhealthy(response.getEntity().toString());
			}
		}
		
	}
	
	public RubyAnalyzer(final RubyExecutor executor) {
		this.executor = executor;
	}
	
	public ImmutableList<HealthCheck> healthchecks(final FiddleId id, final Fiddle fiddle) {
		LOG.debug("Looking for healthcheck in {}", id);
		
		boolean foundTag = false;
		
		final List<HealthCheck> healthchecks = new LinkedList<HealthCheck>();
		
		for(final String line : Splitter.onPattern("\r?\n").split(fiddle.content)) {
			if(!foundTag) {
				foundTag = line.matches("\\s*\\#\\s*\\@HealthCheck(\\s+.*){0,1}");
			}
			
			if(foundTag) {
				LOG.debug("found healthcheck tag, looking for method...");
				if(line.matches("\\s*def\\s+.*")) {
					final Matcher matcher = methodPattern.matcher(line);
					if(matcher.find()) {
						LOG.debug("found method, attempt to find method name...");
						if(matcher.groupCount() > 0 && null != matcher.group(1)) {
							LOG.debug("found method name {}", matcher.group(1));
							healthchecks.add(new RubyHealthCheck(executor, id, fiddle, matcher.group(1)));
						}
					}
					foundTag = false;
				}
			}
		}
		
		return ImmutableList.<HealthCheck>builder().addAll(healthchecks).build();
	}
}
