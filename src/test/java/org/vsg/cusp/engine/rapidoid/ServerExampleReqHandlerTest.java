package org.vsg.cusp.engine.rapidoid;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.rapidoid.http.FastHttp;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.net.Server;
import org.rapidoid.setup.On;
import org.rapidoid.setup.OnRoute;
import org.rapidoid.setup.Setup;



public class ServerExampleReqHandlerTest {

	
	
	
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
				
				FutureTask ft = new FutureTask(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						// TODO Auto-generated method stub
						req.response().body("hello word , my baby.".getBytes());
						req.done();
						return null;
					}

				});
				
				Thread runThread = new Thread(ft);
				runThread.start();


				
				return ft;
			}
			
		});		

	}


	

}
