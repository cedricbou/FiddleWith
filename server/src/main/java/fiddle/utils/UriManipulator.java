package fiddle.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;

public class UriManipulator {

	public final URI uri;
	
	private static Logger LOGGER = LoggerFactory.getLogger(UriManipulator.class);
	
	public UriManipulator(final URI uri) {
		this.uri = uri;
	}
	
	public UriManipulator enhanceQueryString(final Map<String, String> values) {
		try {
			if(uri.getQuery() != null) {
				return new UriManipulator(new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery() + "&" + buildQueryStringParameters(values), uri.getFragment()));
			}
			else {
				return new UriManipulator(new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), buildQueryStringParameters(values), uri.getFragment()));
			}
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String buildQueryStringParameters(final Map<String, String> values) {
		final Iterator<String> params = Iterators.transform(values.entrySet().iterator(), new Function<Entry<String, String>, String>() {
			@Override
			public String apply(final Entry<String, String> input) {
				return input.getKey() + "=" + urlEncode(input.getValue());
			}
		});
		
		return Joiner.on("&").join(params);
	}
	
	private static String urlEncode(final String toEncode) {
		if(null == toEncode) {
			return null;
		}
		
		try {
			return URLEncoder.encode(toEncode, "UTF-8");
		}
		catch(UnsupportedEncodingException e) {
			LOGGER.warn("failed to url encode the string (UTF-8), fallback to string without encoding : " + toEncode, e);
			return toEncode;
		}
	}
}
