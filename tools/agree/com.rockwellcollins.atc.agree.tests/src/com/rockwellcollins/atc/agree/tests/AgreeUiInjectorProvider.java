package com.rockwellcollins.atc.agree.tests;

import com.google.inject.Injector;

public class AgreeUiInjectorProvider extends AgreeInjectorProvider {

	@Override
	public Injector internalCreateInjector() {
		com.rockwellcollins.atc.agree.ui.internal.AgreeActivator.getInstance()
				.getInjector("com.rockwellcollins.atc.agree.Agree");
		return super.internalCreateInjector();
	}

}
