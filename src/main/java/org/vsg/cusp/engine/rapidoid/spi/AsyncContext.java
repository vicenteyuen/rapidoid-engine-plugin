package org.vsg.cusp.engine.rapidoid.spi;

import javax.ws.rs.container.AsyncResponse;

public interface AsyncContext {

	boolean isSuspended();

	AsyncResponse getAsyncResponse();
	
	void complete();
	
	
	void setTimeout(long l);

}
