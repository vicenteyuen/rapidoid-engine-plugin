/**
 * 
 */
package org.vsg.cusp.engine.rapidoid.specimpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.vsg.cusp.engine.rapidoid.utils.DateUtil;

/**
 * @author ruanweibiao
 *
 */
public class BuiltResponse extends Response {

	protected Object entity;
	protected int status = 200;
	protected MultivaluedMap metadata = new MultivaluedHashMap();
	protected Annotation[] annotations;
	protected Class entityClass;
	protected Type genericType;
	protected boolean isClosed;

	public BuiltResponse(int status, MultivaluedMap metadata, Object entity,
			Annotation[] entityAnnotations) {
		setEntity(entity);
		this.status = status;
		this.metadata = metadata;
		this.annotations = entityAnnotations;
	}

	public Class getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
	}

	public void setEntity(Object entity) {
		if (entity == null) {
			this.entity = null;
			this.genericType = null;
			this.entityClass = null;
		} else if (entity instanceof GenericEntity) {

			GenericEntity ge = (GenericEntity) entity;
			this.entity = ge.getEntity();
			this.genericType = ge.getType();
			this.entityClass = ge.getRawType();
		} else {
			this.entity = entity;
			this.entityClass = entity.getClass();
			this.genericType = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getStatus()
	 */
	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getStatusInfo()
	 */
	@Override
	public StatusType getStatusInfo() {
		StatusType statusType = Status.fromStatusCode(status);
		if (statusType == null) {
			statusType = new StatusType() {
				@Override
				public int getStatusCode() {
					return status;
				}

				@Override
				public Status.Family getFamily() {
					return Status.Family.familyOf(status);
				}

				@Override
				public String getReasonPhrase() {
					return "Unknown Code";
				}
			};
		}
		return statusType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getEntity()
	 */
	@Override
	public Object getEntity() {
		// TODO Auto-generated method stub
		return this.entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#readEntity(java.lang.Class)
	 */
	@Override
	public <T> T readEntity(Class<T> entityType) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#readEntity(javax.ws.rs.core.GenericType)
	 */
	@Override
	public <T> T readEntity(GenericType<T> entityType) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#readEntity(java.lang.Class,
	 * java.lang.annotation.Annotation[])
	 */
	@Override
	public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
		// TODO Auto-generated method stub

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#readEntity(javax.ws.rs.core.GenericType,
	 * java.lang.annotation.Annotation[])
	 */
	@Override
	public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#hasEntity()
	 */
	@Override
	public boolean hasEntity() {
		// TODO Auto-generated method stub
		return entity != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#bufferEntity()
	 */
	@Override
	public boolean bufferEntity() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#close()
	 */
	@Override
	public void close() {
		this.isClosed = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getMediaType()
	 */
	@Override
	public MediaType getMediaType() {
		Object obj = metadata.getFirst(HttpHeaders.CONTENT_TYPE);
		if (obj instanceof MediaType)
			return (MediaType) obj;
		if (obj == null) {
			return null;
		}
		String headerStr = toHeaderString(obj);

		return MediaType.valueOf(headerStr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getLanguage()
	 */
	@Override
	public Locale getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	protected String toHeaderString(Object header) {
		if (header instanceof String) {
			return (String) header;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getLength()
	 */
	@Override
	public int getLength() {
		Object obj = metadata.getFirst(HttpHeaders.CONTENT_LENGTH);
		if (obj == null)
			return -1;
		if (obj instanceof Integer)
			return (Integer) obj;
		return Integer.valueOf(toHeaderString(obj));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getAllowedMethods()
	 */
	@Override
	public Set<String> getAllowedMethods() {
		Set<String> allowedMethods = new HashSet<String>();
		List allowed = (List) metadata.get("Allow");
		if (allowed == null)
			return allowedMethods;
		for (Object header : allowed) {
			if (header != null && header instanceof String) {
				String[] list = ((String) header).split(",");
				for (String str : list) {
					if (!"".equals(str.trim())) {
						allowedMethods.add(str.trim().toUpperCase());
					}
				}
			} else {
				allowedMethods.add(toHeaderString(header).toUpperCase());
			}
		}

		return allowedMethods;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getCookies()
	 */
	@Override
	public Map<String, NewCookie> getCookies() {
		Map<String, NewCookie> cookies = new HashMap<String, NewCookie>();
		List list = (List) metadata.get(HttpHeaders.SET_COOKIE);
		if (list == null)
			return cookies;
		for (Object obj : list) {
			if (obj instanceof NewCookie) {
				NewCookie cookie = (NewCookie) obj;
				cookies.put(cookie.getName(), cookie);
			} else {
				String str = toHeaderString(obj);
				NewCookie cookie = NewCookie.valueOf(str);
				cookies.put(cookie.getName(), cookie);
			}
		}
		return cookies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getEntityTag()
	 */
	@Override
	public EntityTag getEntityTag() {
		Object d = metadata.getFirst(HttpHeaders.ETAG);
		if (d == null)
			return null;
		if (d instanceof EntityTag)
			return (EntityTag) d;
		return EntityTag.valueOf(toHeaderString(d));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getDate()
	 */
	@Override
	public Date getDate() {
		Object d = metadata.getFirst(HttpHeaders.DATE);
		if (d == null)
			return null;
		if (d instanceof Date)
			return (Date) d;
		return DateUtil.parseDate(d.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getLastModified()
	 */
	@Override
	public Date getLastModified() {
		Object d = metadata.getFirst(HttpHeaders.LAST_MODIFIED);
		if (d == null)
			return null;
		if (d instanceof Date)
			return (Date) d;
		return DateUtil.parseDate(d.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getLocation()
	 */
	@Override
	public URI getLocation() {
		Object uri = metadata.getFirst(HttpHeaders.LOCATION);
		if (uri == null)
			return null;
		if (uri instanceof URI)
			return (URI) uri;
		String str = null;
		if (uri instanceof String)
			str = (String) uri;
		else
			str = toHeaderString(uri);
		return URI.create(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getLinks()
	 */
	@Override
	public Set<Link> getLinks() {
		/*
		 * LinkHeaders linkHeaders = getLinkHeaders(); Set<Link> links = new
		 * HashSet<Link>(); links.addAll(linkHeaders.getLinks());
		 */
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#hasLink(java.lang.String)
	 */
	@Override
	public boolean hasLink(String relation) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getLink(java.lang.String)
	 */
	@Override
	public Link getLink(String relation) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getLinkBuilder(java.lang.String)
	 */
	@Override
	public Builder getLinkBuilder(String relation) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getMetadata()
	 */
	@Override
	public MultivaluedMap<String, Object> getMetadata() {
		// TODO Auto-generated method stub
		return metadata;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getStringHeaders()
	 */
	@Override
	public MultivaluedMap<String, String> getStringHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Response#getHeaderString(java.lang.String)
	 */
	@Override
	public String getHeaderString(String name) {
		List vals = (List) metadata.get(name);
		if (vals == null)
			return null;
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Object val : vals) {
			if (first)
				first = false;
			else
				builder.append(",");
			if (val == null)
				val = "";
			val = toHeaderString(val);
			if (val == null)
				val = "";
			builder.append(val);
		}
		return builder.toString();
	}

}
