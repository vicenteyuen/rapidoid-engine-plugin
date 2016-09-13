/**
 * 
 */
package org.vsg.cusp.engine.rapidoid;

import org.vsg.cusp.core.ServEngine;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * @author Vicente Yuen
 *
 */
public class RapidoidEngineModule extends AbstractModule {
	

	@Override
	protected void configure() {

		this.bind( ServEngine.class ).annotatedWith( Names.named( RapidoidEngine.class.getName())).to( RapidoidEngine.class ).in(Scopes.SINGLETON);
	}
}
