package org.vsg.cusp.core.rapidoid.eventrest;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.Response;

import org.rapidoid.http.Resp;

public class AsyncResponseImpl implements AsyncResponse {

	private Resp resp;
	
	public Resp getResp() {
		return resp;
	}

	public void setResp(Resp resp) {
		this.resp = resp;
	}

	@Override
	public boolean resume(Object response) {
		// TODO Auto-generated method stub
		Response res = null;
		if (response instanceof Response) {
			res = (Response)response;
		}
		
		// --- check text plain 

		if (res.getMediaType().equals( javax.ws.rs.core.MediaType.TEXT_PLAIN) ) {
			this.resp.contentType( org.rapidoid.commons.MediaType.TEXT_PLAIN_UTF8 );
		}
		Object entity = res.getEntity();
		
		outputContent(entity);

		
		return true;
	}
	
	
	private void outputContent(Object entity) {
		if (entity instanceof java.lang.String) {
			this.resp.body( entity.toString().getBytes() );
		}
	}
	

	@Override
	public boolean resume(Throwable response) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cancel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cancel(int retryAfter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cancel(Date retryAfter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSuspended() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setTimeout(long time, TimeUnit unit) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTimeoutHandler(TimeoutHandler handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Class<?>> register(Class<?> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<?>, Collection<Class<?>>> register(Class<?> callback,
			Class<?>... callbacks) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<?>> register(Object callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<?>, Collection<Class<?>>> register(Object callback,
			Object... callbacks) {
		// TODO Auto-generated method stub
		return null;
	}

}
