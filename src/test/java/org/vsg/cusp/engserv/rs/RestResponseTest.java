package org.vsg.cusp.engserv.rs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.junit.Assert;
import org.junit.Test;


public class RestResponseTest {
	
	@Test
	public void test_ResponseBuilder() throws Exception {
		ResponseBuilder rb = Response.ok("hello world, VISON");
		rb.type( MediaType.TEXT_PLAIN );

		Response jaxrs = rb.build();
		
		
		MediaType mt = jaxrs.getMediaType();
		
		System.out.println(mt);
		
		
	}
	
	@Test
	public void test_MediaType_case01() throws Exception {
		ResponseBuilder rb = Response.ok("hello world, VISON");
		rb.type( MediaType.TEXT_PLAIN );

		Response jaxrs = rb.build();
		MediaType mt = jaxrs.getMediaType();
		
		Assert.assertEquals( "text/plain" , mt.toString());
	}
	

}
