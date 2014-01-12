package fiddle.httpclient;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.HttpContext;

public class ExceptionHttpResponseBuilder {

	private static class ExceptionStatusLine implements StatusLine {

		private final String message;
		
		public ExceptionStatusLine(final Exception e) {
			this.message = recurseMessage(e);
		}
		
		private String recurseMessage(final Throwable e) {
			if(e.getMessage() == null && e.getCause() != null) {
				return recurseMessage(e.getCause());
			}
			else {
				return (e.getMessage() != null)?e.getMessage():"[null]";
			}
		}
		
		@Override
		public ProtocolVersion getProtocolVersion() {
			return new ProtocolVersion("HTTP", 1, 1);
		}
		
		@Override
		public String getReasonPhrase() {
			return message;
		}
		
		@Override
		public int getStatusCode() {
			return 500;
		}
	}

	public static HttpResponse failed(Exception e, HttpContext c) {
		return new DefaultHttpResponseFactory().newHttpResponse(new ExceptionStatusLine(e), c);
	}
}
