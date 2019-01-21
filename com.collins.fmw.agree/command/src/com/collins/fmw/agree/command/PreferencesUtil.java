package com.collins.fmw.agree.command;

import java.io.File;
import java.net.URL;

import com.rockwellcollins.atc.agree.analysis.preferences.PreferenceConstants;
import com.rockwellcollins.atc.z3.Z3Plugin;

import jkind.JKindException;
import jkind.SolverOption;
import jkind.api.JKindApi;
import jkind.api.JRealizabilityApi;
import jkind.api.KindApi;

public class PreferencesUtil {


	public static SolverOption getSolverOption() {
		String solverString = PreferenceConstants.SOLVER_Z3;
		SolverOption solver = SolverOption.valueOf(solverString);
		return solver;
	}


	public static KindApi getConsistencyApi() {
		KindApi api = getJKindApi();

		if (api instanceof JKindApi) {
			int depth = 20 + 1;
			((JKindApi) api).setN(depth);
			((JKindApi) api).disableInvariantGeneration();
			// ((JKindApi) api).disableKInduction();
			((JKindApi) api).setPdrMax(0);
		}
		return api;
	}

	public static JKindApi getJKindApi() {
		JKindApi api = new JKindApi();
		api.setJKindJar(getJKindJar());
		try {
			api.setEnvironment("Z3_HOME", Z3Plugin.getZ3Directory());
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			// Z3Plugin not present
		}

		String solverString = PreferenceConstants.SOLVER_Z3;
		SolverOption solver = SolverOption.valueOf(solverString);
		api.setSolver(solver);

			api.setInductiveCounterexamples();
		if (solver == SolverOption.YICES) {
			api.setSmoothCounterexamples();
		}

			api.setIvcReduction();

		api.setN(20);
		api.setTimeout(100);
		api.setPdrMax(100);
		// TODO set pdr invariants as preferences option
		// api.setPdrInvariants();
//		if (prefs.getBoolean(PreferenceConstants.PREF_NO_KINDUCTION)) {
//			api.disableKInduction();
//		}
		return api;
	}

	public static JRealizabilityApi getJRealizabilityApi() {
		JRealizabilityApi api = new JRealizabilityApi();
		api.setJKindJar(getJKindJar());
		try {
			api.setEnvironment("Z3_HOME", Z3Plugin.getZ3Directory());
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			// Z3Plugin not present
		}

		api.setN(20);
		api.setTimeout(100);

		return api;
	}

	public static String getJKindJar() {

//		String bundleId = "com.collins.fmw.agree.command";
//		Bundle bundle = Platform.getBundle(bundleId);
//		URL url = bundle.getEntry("static/jkind.jar");
		try {
//			URL fileUrl = FileLocator.toFileURL(url);

			URL rootUrl = PreferencesUtil.class.getResource("/");
			System.out.println("root path: " + rootUrl.getPath());

			URL fileUrl = PreferencesUtil.class.getResource("/static/jkind.jar");
			System.out.println("file path: " + fileUrl.getPath());
			return new File(fileUrl.getPath()).toString();

//			String jsonToHolPath = (FileLocator
//					.toFileURL(FileLocator.find(bundle, new Path("static/jkind.jar"), null))).getFile();

		} catch (Exception e) {
			throw new JKindException("Unable to extract jkind.jar from plug-in", e);
		}
	}

}
