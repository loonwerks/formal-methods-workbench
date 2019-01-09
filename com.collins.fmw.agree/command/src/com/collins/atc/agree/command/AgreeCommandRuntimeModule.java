package com.collins.fmw.agree.command;

public class AgreeCommandRuntimeModule extends org.osate.xtext.aadl2.Aadl2RuntimeModule {

	@Override
	public Class<? extends org.eclipse.xtext.linking.ILinker> bindILinker() {
		return AgreeAgent.class;
	}
}