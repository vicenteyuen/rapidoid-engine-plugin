package org.vsg.cusp.engine;

import java.util.LinkedHashMap;
import java.util.Map;

import org.vsg.cusp.engine.rapidoid.RapidoidEngineModule;


public class RapidoidHttpServEngineApplicationTest {
	
	private static RapidoidEngineModule engine = new RapidoidEngineModule();
	

	public void startServer() throws Exception {
		Map<String,String> arguments = new LinkedHashMap<String,String>();
		arguments.put("host", "localhost");
		arguments.put("port", "8100");
		
		/*
		engine.init(arguments);

		
		engine.start();
		*/
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RapidoidHttpServEngineApplicationTest testCase = new RapidoidHttpServEngineApplicationTest();
		
		try {
			testCase.startServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
