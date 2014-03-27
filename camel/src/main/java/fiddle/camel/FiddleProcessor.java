package fiddle.camel;

import org.apache.camel.Processor;

public class FiddleProcessor implements Processor {

	
	
	public void process(org.apache.camel.Exchange ex) throws Exception {
		
		final String json = (String)ex.getIn().getBody();
		System.out.println(json);
	};
}
