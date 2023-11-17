package srv;

import jakarta.ws.rs.core.Application;
import srv.resources.*;

import java.util.HashSet;
import java.util.Set;

import utils.AzureProperties;

public class MainApplication extends Application
{
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> resources = new HashSet<Class<?>>();

	public MainApplication() {
		AzureProperties.setKeys();
		resources.add(AuthResource.class);
		resources.add(ControlResource.class);
		resources.add(HouseResource.class);
		resources.add(QuestionResource.class);
		resources.add(RentalResource.class);
		resources.add(UserResource.class);
		singletons.add( new MediaResource());
	}

	@Override
	public Set<Class<?>> getClasses() {
		return resources;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
