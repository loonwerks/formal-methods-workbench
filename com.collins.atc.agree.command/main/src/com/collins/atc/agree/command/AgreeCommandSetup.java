package com.collins.atc.agree.command;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.osate.xtext.aadl2.properties.PropertiesStandaloneSetup;

import com.google.inject.Guice;
import com.google.inject.Injector;


@SuppressWarnings("all")
public class AgreeCommandSetup implements ISetup {

	@Override
	public Injector createInjectorAndDoEMFRegistration() {
		PropertiesStandaloneSetup.doSetup();

		Injector injector = createInjector();
		register(injector);
		return injector;
	}

	public Injector createInjector() {
		return Guice.createInjector(new AgreeCommandRuntimeModule());
	}

	public void register(Injector injector) {

		IResourceFactory resourceFactory = injector
				.getInstance(org.eclipse.xtext.resource.IResourceFactory.class);
		IResourceServiceProvider serviceProvider = injector
				.getInstance(org.eclipse.xtext.resource.IResourceServiceProvider.class);

		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("aadl", resourceFactory);
		org.eclipse.xtext.resource.IResourceServiceProvider.Registry.INSTANCE.getExtensionToFactoryMap().put("aadl",
				serviceProvider);

		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("aadl2", resourceFactory);
		org.eclipse.xtext.resource.IResourceServiceProvider.Registry.INSTANCE.getExtensionToFactoryMap().put("aadl2",
				serviceProvider);

	}
}