package com.rockwellcollins.atc.agree.tests;

import com.google.inject.Injector;

public class AgreeUiInjectorProvider extends AgreeInjectorProvider {

	@Override
	public Injector internalCreateInjector() {
		org.osate.xtext.aadl2.ui.internal.Aadl2Activator.getInstance().getInjector("org.osate.xtext.aadl2.Aadl2");
		com.rockwellcollins.atc.agree.ui.internal.AgreeActivator.getInstance()
				.getInjector("com.rockwellcollins.atc.agree.Agree");
		return super.internalCreateInjector();
	}

}
