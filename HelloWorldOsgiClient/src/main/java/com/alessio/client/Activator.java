package com.alessio.client;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.alessio.model.Person;
import com.alessio.service.Greeting;
import com.alessio.servicedb.PersonRepository;

public class Activator implements BundleActivator, ServiceListener {
	BundleContext context;
	ServiceTracker<?, ?> helloTracker;
	ServiceTracker<?, ?> dbTracker;

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("bundle client start");
		this.context = context;
		
		// service tracker
		helloTracker = new ServiceTracker<Object, Object>(context, Greeting.class.getName(), null);
		helloTracker.open();
		Greeting greetingFromTracker = (Greeting) helloTracker.getService();
		greetingFromTracker.sayHello();

		// listener
		context.addServiceListener(this);
		
		// no service tracker
		ServiceReference<?> ref = context.getServiceReference(Greeting.class.getName());
		if (ref != null) {
			Greeting greeting = (Greeting) context.getService(ref);
			greeting.sayHello();
		} else {
			System.out.println("bundle client greeting ref null");
		}
		
		// db OSGI
		dbTracker = new ServiceTracker<Object, Object>(context, PersonRepository.class.getName(), null);
		dbTracker.open();
		PersonRepository repository = (PersonRepository) dbTracker.getService();
		repository.save(new Person(0, "Alessio", 26));
		repository.findAll().forEach(person -> System.out.println(person));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("bundle client stop");
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			System.out.println("Service Registered: "+event.getClass());
			break;
		case ServiceEvent.UNREGISTERING:
			System.out.println("Service Unregistering: "+event.getClass());
			break;
		default:
			break;
		}
	}

}
