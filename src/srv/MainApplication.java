package srv;

import jakarta.ws.rs.core.Application;
import srv.resources.*;

import java.util.HashSet;
import java.util.Set;

import db.CosmosDB;
import utils.AzureKeys;

public class MainApplication extends Application
{
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> resources = new HashSet<Class<?>>();

	public MainApplication() {
		AzureKeys.setKeys();
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
