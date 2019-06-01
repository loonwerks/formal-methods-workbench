package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddAttestationManagerClaim extends BuiltInClaim {

	private static final String ADD_ATTESTATION_MANAGER = "Add_Attestation_Manager";

	private final Subcomponent commDriver;
	private final Subcomponent attestationManager;

	public AddAttestationManagerClaim(Subcomponent commDriver, Subcomponent attestationManager) {
		super(ADD_ATTESTATION_MANAGER);
		this.commDriver = commDriver;
		this.attestationManager = attestationManager;
	}

	@Override
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
		callArgs.add(Create.THIS(this.commDriver));
		callArgs.add(Create.THIS(this.attestationManager));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("comm_driver", Create.baseType("component")));
		defParams.add(Create.arg("attestation_manager", Create.baseType("component")));
		return defParams;
	}

}
