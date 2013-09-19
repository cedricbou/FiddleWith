package domain.ruby.http;

import java.io.IOException;

import domain.http.ConfiguredFiddleHttpClient;
import domain.http.FiddleHttpResponse;

public class JRubyConfiguredHttp {

	private final ConfiguredFiddleHttpClient http;
	
	public JRubyConfiguredHttp(final ConfiguredFiddleHttpClient http) {
		this.http = http;
	}
	
	public FiddleHttpResponse post(final Object entity) {
		try {
			return http.post(entity);
		}
		catch( IOException e) {
			throw new RuntimeException(e);
		}
	}

	public FiddleHttpResponse post() {
		try {
			return http.post();
		}
		catch( IOException e) {
			throw new RuntimeException(e);
		}
	}

	public FiddleHttpResponse get(final Object entity) {
		try {
			return http.get(entity);
		}
		catch( IOException e) {
			throw new RuntimeException(e);
		}
	}

	public FiddleHttpResponse get() {
		try {
			return http.get();
		}
		catch( IOException e) {
			throw new RuntimeException(e);
		}
	}
}
