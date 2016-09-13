package org.vsg.cusp.engine.rapidoid.specimpl;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant;

import org.vsg.cusp.engine.rapidoid.utils.HeaderHelper;
import org.vsg.cusp.engine.rapidoid.utils.HttpHeaderNames;

public class ResponseBuilderImpl extends ResponseBuilder {

	protected int status = -1;
	protected Annotation[] entityAnnotations;
	protected Object entity;

	private MultivaluedMap metadata = new MultivaluedHashMap();

	@Override
	public Response build() {
		if (status == -1 && entity == null) {
			status = 204;
		} else if (status == -1) {
			status = 200;
		}
		return new BuiltResponse(status, metadata, entity, entityAnnotations);
	}

	@Override
	public ResponseBuilder clone() {
		ResponseBuilderImpl impl = new ResponseBuilderImpl();
		impl.status = status;
		impl.entityAnnotations = entityAnnotations;
		impl.entity = entity;
		impl.metadata = metadata;
		return impl;
	}

	@Override
	public ResponseBuilder status(int status) {
		// TODO Auto-generated method stub
		this.status = status;
		return this;
	}

	@Override
	public ResponseBuilder entity(Object entity) {
		this.entity = entity;
		return this;
	}

	@Override
	public ResponseBuilder entity(Object entity, Annotation[] annotations) {
		// TODO Auto-generated method stub
		this.entity = entity;
		this.entityAnnotations = annotations;
		return this;
	}

	@Override
	public ResponseBuilder allow(String... methods) {
		if (methods == null) {
			return allow((Set<String>) null);
		}
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		for (String m : methods)
			set.add(m);
		return allow(set);
	}

	@Override
	public ResponseBuilder allow(Set<String> methods) {
		HeaderHelper.setAllow(this.metadata, methods);
		return this;
	}

	@Override
	public ResponseBuilder cacheControl(CacheControl cacheControl) {

		if (cacheControl == null) {
			metadata.remove(HttpHeaderNames.CACHE_CONTROL);
			return this;
		}
		metadata.putSingle(HttpHeaderNames.CACHE_CONTROL, cacheControl);
		return this;
	}

	@Override
	public ResponseBuilder encoding(String encoding) {

		if (encoding == null) {
			metadata.remove(HttpHeaders.CONTENT_ENCODING);
			return this;
		}
		metadata.putSingle(HttpHeaders.CONTENT_ENCODING, encoding);
		return this;
	}

	@Override
	public ResponseBuilder header(String name, Object value) {

		if (value == null) {
			metadata.remove(name);
			return this;
		}
		metadata.add(name, value);
		return this;
	}

	@Override
	public ResponseBuilder replaceAll(MultivaluedMap<String, Object> headers) {

		metadata.clear();
		if (headers == null)
			return this;
		metadata.putAll(headers);
		return this;
	}

	@Override
	public ResponseBuilder language(String language) {
		if (language == null) {
			metadata.remove(HttpHeaderNames.CONTENT_LANGUAGE);
			return this;
		}
		metadata.putSingle(HttpHeaderNames.CONTENT_LANGUAGE, language);
		return this;
	}

	@Override
	public ResponseBuilder language(Locale language) {
		if (language == null) {
			metadata.remove(HttpHeaderNames.CONTENT_LANGUAGE);
			return this;
		}
		metadata.putSingle(HttpHeaderNames.CONTENT_LANGUAGE, language);
		return this;
	}

	@Override
	public ResponseBuilder type(MediaType type) {
		if (type == null) {
			metadata.remove(HttpHeaderNames.CONTENT_TYPE);
			return this;
		}
		metadata.putSingle(HttpHeaderNames.CONTENT_TYPE, type);
		return this;
	}

	@Override
	public ResponseBuilder type(String type) {
		if (type == null) {
			metadata.remove(HttpHeaderNames.CONTENT_TYPE);
			return this;
		}
		metadata.putSingle(HttpHeaderNames.CONTENT_TYPE, type);
		return this;
	}

	@Override
	public ResponseBuilder variant(Variant variant) {
		// TODO Auto-generated method stub
		if (variant == null) {
			type((String) null);
			language((String) null);
			metadata.remove(HttpHeaderNames.CONTENT_ENCODING);
			return this;
		}
		type(variant.getMediaType());
		language(variant.getLanguage());
		if (variant.getEncoding() != null)
			metadata.putSingle(HttpHeaderNames.CONTENT_ENCODING,
					variant.getEncoding());
		else
			metadata.remove(HttpHeaderNames.CONTENT_ENCODING);
		return this;
	}

	@Override
	public ResponseBuilder contentLocation(URI location) {
		// TODO Auto-generated method stub
		if (location == null) {
			metadata.remove(HttpHeaderNames.CONTENT_LOCATION);
			return this;
		}
		/*
		 * if (!location.isAbsolute() &&
		 * ResteasyProviderFactory.getContextData(HttpRequest.class) != null) {
		 * String path = location.toString(); if (path.startsWith("/")) path =
		 * path.substring(1); URI baseUri =
		 * ResteasyProviderFactory.getContextData
		 * (HttpRequest.class).getUri().getBaseUri(); location =
		 * baseUri.resolve(path); }
		 */
		metadata.putSingle(HttpHeaderNames.CONTENT_LOCATION, location);
		return this;
	}

	@Override
	public ResponseBuilder cookie(NewCookie... cookies) {
		if (cookies == null) {
			metadata.remove(HttpHeaderNames.SET_COOKIE);
			return this;
		}
		for (NewCookie cookie : cookies) {
			metadata.add(HttpHeaderNames.SET_COOKIE, cookie);
		}
		return this;
	}

	@Override
	public ResponseBuilder expires(Date expires) {
		// TODO Auto-generated method stub
		if (expires == null) {
			metadata.remove(HttpHeaderNames.EXPIRES);
			return this;
		}
		metadata.putSingle(HttpHeaderNames.EXPIRES, getDateFormatRFC822()
				.format(expires));
		return this;
	}

	public static SimpleDateFormat getDateFormatRFC822() {
		SimpleDateFormat dateFormatRFC822 = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormatRFC822.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormatRFC822;
	}

	@Override
	public ResponseBuilder lastModified(Date lastModified) {
		// TODO Auto-generated method stub
		if (lastModified == null) {
			metadata.remove(HttpHeaderNames.LAST_MODIFIED);
			return this;
		}
		metadata.putSingle(HttpHeaderNames.LAST_MODIFIED, lastModified);
		return this;
	}

	@Override
	public ResponseBuilder location(URI location) {
		if (location == null) {
			metadata.remove(HttpHeaderNames.LOCATION);
			return this;
		}
		metadata.putSingle(HttpHeaderNames.LOCATION, location);
		return this;
	}

	@Override
	public ResponseBuilder tag(EntityTag tag) {
		if (tag == null) {
			metadata.remove(HttpHeaderNames.ETAG);
			return this;
		}
		metadata.putSingle(HttpHeaderNames.ETAG, tag);
		return this;
	}

	@Override
	public ResponseBuilder tag(String tag) {
		if (tag == null) {
			metadata.remove(HttpHeaderNames.ETAG);
			return this;
		}
		metadata.putSingle(HttpHeaderNames.ETAG, tag);
		return this;
	}

	@Override
	public ResponseBuilder variants(Variant... variants) {
		// TODO Auto-generated method stub
		return this.variants(Arrays.asList(variants));
	}

	@Override
	public ResponseBuilder variants(List<Variant> variants) {
		// TODO Auto-generated method stub
		if (variants == null) {
			metadata.remove(HttpHeaderNames.VARY);
			return this;
		}
		String vary = createVaryHeader(variants);
		metadata.putSingle(HttpHeaderNames.VARY, vary);

		return this;
	}

	public static String createVaryHeader(List<Variant> variants) {
		boolean accept = false;
		boolean acceptLanguage = false;
		boolean acceptEncoding = false;

		for (Variant variant : variants) {
			if (variant.getMediaType() != null)
				accept = true;
			if (variant.getLanguage() != null)
				acceptLanguage = true;
			if (variant.getEncoding() != null)
				acceptEncoding = true;
		}

		String vary = null;
		if (accept)
			vary = HttpHeaderNames.ACCEPT;
		if (acceptLanguage) {
			if (vary == null)
				vary = HttpHeaderNames.ACCEPT_LANGUAGE;
			else
				vary += ", " + HttpHeaderNames.ACCEPT_LANGUAGE;
		}
		if (acceptEncoding) {
			if (vary == null)
				vary = HttpHeaderNames.ACCEPT_ENCODING;
			else
				vary += ", " + HttpHeaderNames.ACCEPT_ENCODING;
		}
		return vary;
	}

	@Override
	public ResponseBuilder links(Link... links) {
		if (links == null) {
			metadata.remove(HttpHeaders.LINK);
			return this;
		}
		for (Link link : links) {
			metadata.add(HttpHeaders.LINK, link);
		}
		return this;
	}

	@Override
	public ResponseBuilder link(URI uri, String rel) {
		Link link = Link.fromUri(uri).rel(rel).build();
		metadata.add(HttpHeaders.LINK, link);
		return this;
	}

	@Override
	public ResponseBuilder link(String uri, String rel) {
		Link link = Link.fromUri(uri).rel(rel).build();
		metadata.add(HttpHeaders.LINK, link);
		return this;
	}

}
