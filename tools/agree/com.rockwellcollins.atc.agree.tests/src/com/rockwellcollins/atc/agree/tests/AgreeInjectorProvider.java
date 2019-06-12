package com.rockwellcollins.atc.agree.tests;

import com.google.inject.Injector;
import com.rockwellcollins.atc.agree.AgreeStandaloneSetup;
import com.rockwellcollins.atc.agree.tests.testsupport.Aadl2InjectorProvider;

public class AgreeInjectorProvider extends Aadl2InjectorProvider {
	@Override
	protected Injector internalCreateInjector() {
		AgreeStandaloneSetup.doSetup();
		// new Aadl2InjectorProvider().getInjector();
		return super.internalCreateInjector();
	}
}
