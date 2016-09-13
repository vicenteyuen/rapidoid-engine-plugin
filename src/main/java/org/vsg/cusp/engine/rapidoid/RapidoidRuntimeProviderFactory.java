/**
 * 
 */
package org.vsg.cusp.engine.rapidoid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vsg.cusp.engine.rapidoid.plugins.delegates.MediaTypeHeaderDelegate;
import org.vsg.cusp.engine.rapidoid.specimpl.ResponseBuilderImpl;

/**
 * @author Vicente Yuen
 *
 */
public class RapidoidRuntimeProviderFactory extends RuntimeDelegate {

	protected RapidoidRuntimeProviderFactory parent;

	private static Logger logger = LoggerFactory.getLogger(RapidoidRuntimeProviderFactory.class);

	protected Map<Class<?>, HeaderDelegate> headerDelegates = new ConcurrentHashMap<Class<?>, HeaderDelegate>();
	
	
	public RapidoidRuntimeProviderFactory() {
		initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.RuntimeDelegate#createUriBuilder()
	 */
	@Override
	public UriBuilder createUriBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.RuntimeDelegate#createResponseBuilder()
	 */
	@Override
	public ResponseBuilder createResponseBuilder() {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("create response builder : ");
		}

		return new ResponseBuilderImpl();
	}
	
	protected void initialize() {
		
	      addHeaderDelegate(MediaType.class, new MediaTypeHeaderDelegate());
	      /*
	      addHeaderDelegate(NewCookie.class, new NewCookieHeaderDelegate());
	      addHeaderDelegate(Cookie.class, new CookieHeaderDelegate());
	      addHeaderDelegate(URI.class, new UriHeaderDelegate());
	      addHeaderDelegate(EntityTag.class, new EntityTagDelegate());
	      addHeaderDelegate(CacheControl.class, new CacheControlDelegate());
	   
	      //addHeaderDelegate(Locale.class, new LocaleDelegate());
	      //addHeaderDelegate(LinkHeader.class, new LinkHeaderDelegate());
	      addHeaderDelegate(javax.ws.rs.core.Link.class, new LinkDelegate());
	      addHeaderDelegate(Date.class, new DateDelegate());
	      */		
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.RuntimeDelegate#createVariantListBuilder()
	 */
	@Override
	public VariantListBuilder createVariantListBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.RuntimeDelegate#createEndpoint(javax.ws.rs.core.
	 * Application , java.lang.Class)
	 */
	@Override
	public <T> T createEndpoint(Application application, Class<T> endpointType)
			throws IllegalArgumentException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)
	 */
	@Override
	public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> tClass) throws IllegalArgumentException {
		if (headerDelegates == null && parent != null) {
			return parent.createHeaderDelegate(tClass);
		}

		Class<?> clazz = tClass;
		while (clazz != null) {
			HeaderDelegate<T> delegate = headerDelegates.get(clazz);
			if (delegate != null) {
				return delegate;
			}
			delegate = createHeaderDelegateFromInterfaces(clazz.getInterfaces());
			if (delegate != null) {
				return delegate;
			}
			clazz = clazz.getSuperclass();
		}

		return createHeaderDelegateFromInterfaces(tClass.getInterfaces());
	}

	protected <T> HeaderDelegate<T> createHeaderDelegateFromInterfaces(Class<?>[] interfaces) {
		HeaderDelegate<T> delegate = null;
		for (int i = 0; i < interfaces.length; i++) {
			delegate = headerDelegates.get(interfaces[i]);
			if (delegate != null) {
				return delegate;
			}
			delegate = createHeaderDelegateFromInterfaces(interfaces[i].getInterfaces());
			if (delegate != null) {
				return delegate;
			}
		}
		return null;
	}

	public void addHeaderDelegate(Class clazz, HeaderDelegate header) {
		if (headerDelegates == null) {
			headerDelegates = new ConcurrentHashMap<Class<?>, HeaderDelegate>();
			headerDelegates.putAll(parent.getHeaderDelegates());
		}
		headerDelegates.put(clazz, header);
	}

	protected Map<Class<?>, HeaderDelegate> getHeaderDelegates() {
		if (headerDelegates == null && parent != null)
			return parent.getHeaderDelegates();
		return headerDelegates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.RuntimeDelegate#createLinkBuilder()
	 */
	@Override
	public Builder createLinkBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

}
