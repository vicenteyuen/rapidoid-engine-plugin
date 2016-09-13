package org.vsg.cusp.engine.rapidoid.specimpl;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.vsg.cusp.engine.rapidoid.spi.AsyncContext;

public class AsyncHttpRequestImpl {

	protected ScheduledExecutorService asyncScheduler = Executors
			.newScheduledThreadPool(3);

	private Req req;

	protected HttpExecutionContext asynchronousContext;

	public AsyncHttpRequestImpl(Req req) {

		this.req = req;

		asynchronousContext = new HttpExecutionContext();

	}

	public Req getReq() {
		return req;
	}

	public void setReq(Req req) {
		this.req = req;
	}
	
	
	public void close() {
		if (null != this.req ) {
			this.req.done();
		}
	}

	public AsyncContext getAsyncContext() {
		return this.asynchronousContext;
	}

	public class HttpExecutionContext implements AsyncContext{

		protected volatile boolean done;

		protected volatile boolean cancelled;

		protected volatile boolean wasSuspended;

		protected RapidoidAsychronousResponse asynchronousResponse;
		
		
		public HttpExecutionContext() {
			asynchronousResponse = new RapidoidAsychronousResponse();
		}
		
		@Override
		public void complete() {
			
			// --- run and flush content ---
			
		}
		

		
		@Override
		public void setTimeout(long l) {
			// TODO Auto-generated method stub
			
		}



		private class RapidoidAsychronousResponse implements AsyncResponse {

			private Object responseLock = new Object();

			protected WeakReference<Thread> creatingThread = new WeakReference<Thread>(
					Thread.currentThread());

			protected ScheduledFuture timeoutFuture; // this is to get around
														// TCK tests that call
														// setTimeout in a
														// separate thread which
														// is illegal.

			protected TimeoutHandler timeoutHandler;

			@Override
			public boolean resume(Object entity) {
				
				synchronized (responseLock) {
					if (done) {
						return false;
					}
					if (cancelled) {
						return false;
					}
					AsyncContext asyncContext = getAsyncContext();
					try {
						return internalResume(entity);
					} finally {
						done = true;
						asyncContext.complete();
					}
				}
			}

			// --- handle --
			protected boolean internalResume(Object entity) {
				
				Resp resp = req.response();

				if (entity instanceof Response) {
					
					Response respEntity = (Response)entity;
					String bodyEntity = respEntity.getEntity().toString();
					resp.body( bodyEntity.getBytes() );
					
				}			
				else if (entity instanceof java.io.Serializable) {
					String typeName = entity.getClass().getTypeName();
					if ("java.lang.String".equals(typeName)) {
						resp.body( entity.toString().getBytes() );
					}
				} 
				return true;
			}

			@Override
			public boolean resume(Throwable response) {
				synchronized (responseLock) {
					if (done)
						return false;
					if (cancelled)
						return false;
					AsyncContext asyncContext = getAsyncContext();
					try {
						return internalResume(response);
					} catch (Exception unhandled) {
						return internalResume(Response.status(500).build());
					} finally {
						done = true;
						asyncContext.complete();
					}
				}
			}

			@Override
			public boolean cancel() {
				// LogMessages.LOGGER.debug(Messages.MESSAGES.cancel());
				synchronized (responseLock) {
					if (cancelled) {
						// LogMessages.LOGGER.debug(Messages.MESSAGES.alreadyCanceled());
						return true;
					}
					if (done) {
						// LogMessages.LOGGER.debug(Messages.MESSAGES.alreadyDone());
						return false;
					}
					done = true;
					cancelled = true;
					AsyncContext asyncContext = getAsyncContext();
					try {
						// LogMessages.LOGGER.debug(Messages.MESSAGES.cancellingWith503());
						return internalResume(Response.status(
								Response.Status.SERVICE_UNAVAILABLE).build());
					} finally {
						asyncContext.complete();
					}
				}
			}

			@Override
			public boolean cancel(int retryAfter) {
				synchronized (responseLock) {
					if (cancelled)
						return true;
					if (done)
						return false;
					done = true;
					cancelled = true;
					AsyncContext asyncContext = getAsyncContext();
					try {
						return internalResume(Response
								.status(Response.Status.SERVICE_UNAVAILABLE)
								.header(HttpHeaders.RETRY_AFTER, retryAfter)
								.build());
					} finally {
						asyncContext.complete();
					}
				}
			}

			@Override
			public boolean cancel(Date retryAfter) {
				synchronized (responseLock) {
					if (cancelled)
						return true;
					if (done)
						return false;
					done = true;
					cancelled = true;
					AsyncContext asyncContext = getAsyncContext();
					try {
						return internalResume(Response
								.status(Response.Status.SERVICE_UNAVAILABLE)
								.header(HttpHeaders.RETRY_AFTER, retryAfter)
								.build());
					} finally {
						asyncContext.complete();
					}
				}
			}

			@Override
			public boolean isSuspended() {
				// TODO Auto-generated method stub
				return !done && !cancelled;
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return cancelled;
			}

			@Override
			public boolean isDone() {
				// TODO Auto-generated method stub
				return done;
			}

			@Override
			public boolean setTimeout(long time, TimeUnit unit) {
				synchronized (responseLock) {
					if (done || cancelled)
						return false;
					Thread thread = creatingThread.get();
					if (thread != null && thread != Thread.currentThread()) {
						// this is to get around TCK tests that call setTimeout
						// in a separate thread which is illegal.
						if (timeoutFuture != null
								&& !timeoutFuture.cancel(false)) {
							return false;
						}
						Runnable task = new Runnable() {
							@Override
							public void run() {
								// LogMessages.LOGGER.debug(Messages.MESSAGES.scheduledTimeout());
								// handleTimeout();
							}
						};
						// LogMessages.LOGGER.debug(Messages.MESSAGES.schedulingTimeout());
						timeoutFuture = asyncScheduler.schedule(task, time,
								unit);
					} else {
						AsyncContext asyncContext = getAsyncContext();
						long l = unit.toMillis(time);
						asyncContext.setTimeout(l);
					}

				}
				return true;
			}

			@Override
			public void setTimeoutHandler(TimeoutHandler handler) {
				// TODO Auto-generated method stub
				this.timeoutHandler = handler;
			}

			@Override
			public Collection<Class<?>> register(Class<?> callback) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<Class<?>, Collection<Class<?>>> register(
					Class<?> callback, Class<?>... callbacks) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Collection<Class<?>> register(Object callback) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<Class<?>, Collection<Class<?>>> register(
					Object callback, Object... callbacks) {
				// TODO Auto-generated method stub
				return null;
			}

		}
		
		@Override
		public AsyncResponse getAsyncResponse() {
			return this.asynchronousResponse;
		}

		@Override
		public boolean isSuspended() {
			// TODO Auto-generated method stub
			return false;
		}


	}

}
