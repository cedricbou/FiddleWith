package resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResourceJsonUtils {

	public static String autoJson(MultivaluedMap<String, String> map) {
		String json = "{";

		final Set<String> keys = map.keySet();
		int j = 0;

		for (final String key : keys) {
			json += "\"" + key.trim() + "\":";

			final List<String> values = map.get(key);

			if (values.size() > 1) {
				json += "[";

				int i = 0; // Why are there no native map, grep, flat, filter or
							// flatMap in Java :'(

				for (final String value : values) {
					json += asJsonString(value);

					if (++i < values.size()) {
						json += ",";
					}
				}

				json += "]";
			} else {
				json += asJsonString(map.getFirst(key));
			}

			if (++j < keys.size()) {
				json += ",";
			}
		}

		json += "}";

		return json;
	}

	public static String asJsonString(final String str) {
		// TODO : check if json with Jackson ?
		// TODO : try to detect numeric and boolean when not json
		final String trimmed = str.trim();
		if ((trimmed.startsWith("[") && trimmed.endsWith("]"))
				|| (trimmed.startsWith("{") && trimmed.endsWith("}"))
				|| (trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
			return trimmed;
		} else {
			if (trimmed.matches("\\d+") || trimmed.matches("\\d*\\.\\d+")) {
				return trimmed;
			} else if (trimmed.equals("true") || trimmed.equals("false")) {
				return trimmed;
			} else {
				return "\"" + str + "\"";
			}
		}
	}

	public static JsonNode jsonToNode(final String json) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readTree(json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Map jsonToMap(final String json) {
		try {
			return new ObjectMapper().readValue(json, HashMap.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
