package com.rockwellcollins.atc.agree.validation;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.OutputStatement;
import com.rockwellcollins.atc.agree.agree.SpecStatement;

public class EqCycleFinder {

	public static List<OutputStatement> noDelayCycles(AgreeContract contract) {
		List<OutputStatement> eqs = new ArrayList<>();
		for (SpecStatement spec : contract.getSpecs()) {
			if (spec instanceof OutputStatement) {
				eqs.add((OutputStatement) spec);
			}
		}
		return getFirstCycle(eqs);
	}

	private static List<OutputStatement> getFirstCycle(List<OutputStatement> eqs) {
		// TODO Auto-generated method stub
		return null;
	}

}
