package org.vsg.cusp.engine.rapidoid.plugins.delegates;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import org.vsg.cusp.engine.rapidoid.utils.HeaderParameterParser;

public class MediaTypeHeaderDelegate implements HeaderDelegate {

	public Object fromString(String type) throws IllegalArgumentException {
		if (type == null)
			throw new IllegalArgumentException("media tyle is not null. ");
		return parse(type);
	}

	public static MediaType parse(String type) {
		int typeIndex = type.indexOf('/');
		int paramIndex = type.indexOf(';');
		String major = null;
		String subtype = null;
		if (typeIndex < 0) // possible "*"
		{
			major = type;
			if (paramIndex > -1) {
				major = major.substring(0, paramIndex);
			}
			if (!MediaType.MEDIA_TYPE_WILDCARD.equals(major)) {
				throw new IllegalArgumentException("parse mediatyle fail. ");
			}
			subtype = MediaType.MEDIA_TYPE_WILDCARD;
		} else {
			major = type.substring(0, typeIndex);
			if (paramIndex > -1) {
				subtype = type.substring(typeIndex + 1, paramIndex);
			} else {
				subtype = type.substring(typeIndex + 1);
			}
		}
		if (major.length() < 1 || subtype.length() < 1) {
			throw new IllegalArgumentException("fail to parse media type");
		}
		if (!isValid(major) || !isValid(subtype)) {
			throw new IllegalArgumentException("fail to parse media type");
		}
		String params = null;
		if (paramIndex > -1)
			params = type.substring(paramIndex + 1);
		if (params != null && !params.equals("")) {
			HashMap<String, String> typeParams = new HashMap<String, String>();

			int start = 0;

			while (start < params.length()) {
				start = HeaderParameterParser.setParam(typeParams, params, start);
			}
			return new MediaType(major, subtype, typeParams);
		} else {
			return new MediaType(major, subtype);
		}
	}

	private static final char[] quotedChars = "()<>@,;:\\\"/[]?= \t\r\n".toCharArray();

	public static boolean quoted(String str) {
		for (char c : str.toCharArray()) {
			for (char q : quotedChars)
				if (c == q)
					return true;
		}
		return false;
	}

	protected static boolean isValid(String str) {
		if (str == null || str.length() == 0)
			return false;
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
			case '/':
			case '\\':
			case '?':
			case ':':
			case '<':
			case '>':
			case ';':
			case '(':
			case ')':
			case '@':
			case ',':
			case '[':
			case ']':
			case '=':
				return false;
			default:
				break;
			}
		}
		return true;
	}

	public String toString(Object o) {
		if (o == null)
			throw new IllegalArgumentException("parameter is not null.");
		MediaType type = (MediaType) o;
		StringBuffer buf = new StringBuffer();

		buf.append(type.getType().toLowerCase()).append("/").append(type.getSubtype().toLowerCase());
		if (type.getParameters() == null || type.getParameters().size() == 0)
			return buf.toString();
		for (String name : type.getParameters().keySet()) {
			buf.append(';').append(name).append('=');
			String val = type.getParameters().get(name);
			if (quoted(val))
				buf.append('"').append(val).append('"');
			else
				buf.append(val);
		}
		return buf.toString();
	}

}
