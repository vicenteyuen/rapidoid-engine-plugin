package org.vsg.cusp.engine.rapidoid;

import org.rapidoid.http.FastHttp;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.net.Server;
import org.rapidoid.setup.On;
import org.rapidoid.setup.OnRoute;
import org.rapidoid.setup.Setup;



public class ServerExampleCallbackTest {

	
	
	
	public static void main(String[] args) {
		
		String host = "0.0.0.0";
		int port  = 8100;
		
		Setup setup = On.setup();
		
		setup.address(host);
		setup.port(port);
		
		Server serv = setup.listen();
		
		FastHttp http = setup.http();
		
		OnRoute route = new OnRoute(http, setup.defaults(), org.rapidoid.util.Constants.GET, "/test");
		
		

		route.serve(new ReqHandler() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3162459205800800468L;

			@Override
			public Object execute(Req req)
					throws Exception {
				Req r = req.async();

				r.response().body("hello vison 1234".getBytes());

				
				return r.done();
			}
			
		});		

	}


	

}
