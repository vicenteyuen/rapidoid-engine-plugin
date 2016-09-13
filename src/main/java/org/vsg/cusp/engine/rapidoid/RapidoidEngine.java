/**
 * 
 */
package org.vsg.cusp.engine.rapidoid;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;

import org.rapidoid.http.FastHttp;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.net.Server;
import org.rapidoid.setup.On;
import org.rapidoid.setup.OnRoute;
import org.rapidoid.setup.Setup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vsg.cusp.core.CountDownLatchAware;
import org.vsg.cusp.core.EngineCompLoaderService;
import org.vsg.cusp.core.LifecycleState;
import org.vsg.cusp.core.MethodParametersMetaInfo;
import org.vsg.cusp.core.MicroCompInjector;
import org.vsg.cusp.core.ServEngine;
import org.vsg.cusp.engine.rapidoid.specimpl.AsyncHttpRequestImpl;

import com.google.inject.Injector;

/**
 * @author ruanweibiao
 *
 */
public class RapidoidEngine implements ServEngine, 
	EngineCompLoaderService , Runnable ,  CountDownLatchAware {

	private static Logger logger = LoggerFactory.getLogger(RapidoidEngineModule.class);	


	@Override
	public void start() {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Engined is starting ... ");
		}

		Thread threadHook = new Thread(this);
		threadHook.start();
		
	}
	
	private Server serv;
	
	Setup setup = On.setup();		
	
	@Override
	public void run() {
		String host = arguments.get("host");
		int port = Integer.parseInt( arguments.get("port") );

		try {
			setState( LifecycleState.STARTING );
			setup.address(host);
			setup.port(port);
			
			serv = setup.listen();
			
			// --- bind http service ---
			logger.info("listen http port : [" + port + "].");
			countDownLatch.countDown();
			setState( LifecycleState.STARTED );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


	@Override
	public void stop() {
		try {
			setup.shutdown();

			
			if (null != serv) {
				// --- shutdown server ---
				if (serv.isActive()) {
					serv.shutdown();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
			
	}

	private Map<String, String> arguments;
	
	@Override
	public void init(Map<String, String> arguments) {
		// TODO Auto-generated method stub
		this.arguments = arguments;
	}

	private void implementForRestPath(Class<?> cls , Object inst ,  String contextPath, FastHttp http) {
		Path clsPathinst = cls.getAnnotation(Path.class);
		
		String basePath = "";
		if (null != clsPathinst) {
			basePath = clsPathinst.value();
		}
		
		try {
			List<String> restPathList = new Vector<String>();
			
			Method[] methods = cls.getMethods();
			
			for (Method method : methods) {
				
				Path methodPathInst = method.getAnnotation(Path.class);
				
				if ( methodPathInst != null ) {
					Collection<String> httpMethods = supportdHttpMethod(method);
					
					// --- scan method parameter ---
					
					MethodParametersMetaInfo mpMetaInfo = scanParameterMetaInfo(method);
					
					
					StringBuilder fullPath = new StringBuilder();
					if (contextPath == null || contextPath.equals("")) {
						
					} else {
						fullPath.append("/").append(contextPath );
					}
					fullPath.append( basePath ).append( methodPathInst.value() );
					restPathList.add( fullPath.toString() );
					
					// --- bind get method ---
					if (httpMethods.contains( HttpMethod.GET )) {
						// --- bind request ---
						OnRoute route = new OnRoute(http, setup.defaults(), org.rapidoid.util.Constants.GET, fullPath.toString());
						
						route.serve(new ReqHandler() {
							private static final long serialVersionUID = 3162459205800800468L;

							@Override
							public Object execute(Req req)
									throws Exception {
								
								AsyncHttpRequestImpl hreq = new AsyncHttpRequestImpl(req);
								
								FutureTask<AsyncHttpRequestImpl> ft = new FutureTask<AsyncHttpRequestImpl>(new Callable<AsyncHttpRequestImpl>() {

									@Override
									public AsyncHttpRequestImpl call() throws Exception {
										// TODO Auto-generated method stub
										injectParameterInstanceToMethod(mpMetaInfo , hreq , fullPath.toString());


										try {
											Object returnVal = method.invoke(inst , mpMetaInfo.getParams());
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										// --- close request ---
										hreq.close();
										
										return hreq;
									}

								});
								// --- add future task to manager ---
								
								// --- add future task to manager ----
								Thread runThread = new Thread(ft);
								runThread.start();								

								return ft;
								
							}
							
						});
						// --- call method ---

					}
					
					
				}
				
				// --- bind to rest api 
				
			}			

			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	
	private Map<String , String> parsePathParameter(String path , String pattern) {
		StringTokenizer patternToken = new StringTokenizer(pattern,"\\/");
		StringTokenizer pathToken = new StringTokenizer(path,"\\/");
		
		Map<String,String> pathParams = new LinkedHashMap<String,String>();

		while (patternToken.hasMoreElements()) {
			
			String patternVar = patternToken.nextToken();
			String pathVar = pathToken.nextToken();

			if (patternVar.indexOf("{") > -1 && patternVar.indexOf("}") > -1) {
				String paramName = patternVar.replace("{", "").replace("}", "");
				
				pathParams.put( paramName , pathVar);
			} else {
				// --- check the pattern value equat path value or not ---
				if (!patternVar.equals(pathVar)) {
					logger.warn( "patternVar=" + patternVar + " , " + "pathToken=" + pathVar );
				}
			}
	     }
		
		return pathParams;
	}
	
	
	private Collection<String> supportdHttpMethod(Method method) {
		Annotation[] annos = method.getAnnotations();
		
		Collection<String> supportedHpMethods = new Vector<String>();
		for (Annotation anno : annos) {
			if ( anno.annotationType().equals( GET.class ) ) {
				supportedHpMethods.add( HttpMethod.GET );
			}
		}
		return supportedHpMethods;
	}
	
	
	private MethodParametersMetaInfo scanParameterMetaInfo(Method method) {
		
		MethodParametersMetaInfo info = new MethodParametersMetaInfo();
		
		Parameter[] parameters = method.getParameters();

		
		info.setParamCls( method.getParameterTypes() );
		Object[] params = new Object[method.getParameterTypes().length];
		info.setParams( params );
		
		info.setParameters( parameters );
		
		return info;
	}
   
	private void injectParameterInstanceToMethod(MethodParametersMetaInfo info , AsyncHttpRequestImpl req , String patternPath) {
		
		Class<?>[] supportedClass = info.getParamCls();
		Object[] paramInsts = info.getParams();
		
		for (int i = 0 ; i < supportedClass.length ; i++) {
			Class<?> paramType = supportedClass[i];
			Object paramInst = paramInsts[i];
			
		
			if ( paramInst != null) {
				continue;
			}
			
			Req orgReq = req.getReq();
			
			Resp resp = orgReq.response();
			
			Map<String , String> pathParams = parsePathParameter(orgReq.path() , patternPath);			
			
			// --- parse any class type ---
			if (paramType.equals( AsyncResponse.class )) {
				// --- build async response implement ---
				AsyncResponse inst = req.getAsyncContext().getAsyncResponse();
				paramInsts[i] = inst;
			}
			else if (paramType.isPrimitive()) {
				java.io.Serializable value = null;				
				if (info.presentAnnotation(i , PathParam.class) ) {
					PathParam pp = info.receiveAnnotationInst(i, PathParam.class);
					value = pathParams.get( pp.value() );
				};
				if (paramType.getTypeName().equals("int")) {
					paramInsts[i] = value == null ? 0 : Integer.parseInt(value.toString());
				}


			}
			else if ( paramType instanceof java.io.Serializable ) {
				java.io.Serializable value = null;
				if (info.presentAnnotation(i , PathParam.class) ) {
					PathParam pp = info.receiveAnnotationInst(i, PathParam.class);
					value = pathParams.get( pp.value() );
				};
				
				if (paramType.equals(String.class)) {
					paramInsts[i] = value.toString();
				}
			}
			

		}
		
		
	}



	@Override
	public void doCompInject(MicroCompInjector microCompInjector) {
		FastHttp http = setup.http();		
		
		Injector injector = microCompInjector.getInjector();
		
		Collection<Class<?>> supportedCls = supportAnnotationClz(microCompInjector.getAnnotationMaps());
		
		for (Class<?> cls : supportedCls) {
			Object inst =  injector.getInstance( cls );
			implementForRestPath(cls , inst , microCompInjector.getContextPath() , http);
		}
	}
	
	private Collection<Class<?>> supportAnnotationClz(Map<Class<?>, Collection<Class<?>>>  annotationMap ) {
		
		Collection<Class<?>> result = new Vector<Class<?>>();
		
		Set<Entry<Class<?>, Collection<Class<?>>>> entryMapSet =   annotationMap.entrySet();
		
		for (Entry<Class<?>, Collection<Class<?>>> entry : entryMapSet) {
			 Collection<Class<?>> annotations =  entry.getValue();
			 
			 if (!annotations.contains( Path.class )) {
				 continue;
			 }
			 
			 result.add( entry.getKey() );
			
		}
		
		
		return result;
	}
	

    private volatile LifecycleState state = LifecycleState.NEW; 	
	
	@Override
	public LifecycleState getState() {
		return state;
	}

	

	@Override
	public void setState(LifecycleState newState) {
		// TODO Auto-generated method stub
		state = newState;
	}

	
	private CountDownLatch countDownLatch;


	@Override
	public void setCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}



	
	
	

}
