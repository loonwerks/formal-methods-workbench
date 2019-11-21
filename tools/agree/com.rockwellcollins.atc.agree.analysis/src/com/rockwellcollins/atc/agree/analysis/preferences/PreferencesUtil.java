package com.rockwellcollins.atc.agree.analysis.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

//import com.rockwellcollins.atc.z3.Z3Plugin;
import com.collins.trustedsystems.z3.Z3Plugin;
import com.rockwellcollins.atc.agree.analysis.Activator;

import jkind.SolverOption;
import jkind.api.JKindApi;
import jkind.api.JRealizabilityApi;
import jkind.api.Kind2Api;
import jkind.api.Kind2WebApi;
import jkind.api.KindApi;
import jkind.api.SallyApi;

public class PreferencesUtil {
	public static KindApi getKindApi() {
		IPreferenceStore prefs = getPreferenceStore();
		String modelChecker = prefs.getString(PreferenceConstants.PREF_MODEL_CHECKER);
		String remoteUrl = prefs.getString(PreferenceConstants.PREF_REMOTE_URL);
		KindApi api = getKindApi(modelChecker, remoteUrl);
		if (prefs.getBoolean(PreferenceConstants.PREF_DEBUG)) {
			api.setApiDebug();
		}
		return api;
	}

	public static SolverOption getSolverOption() {
		IPreferenceStore prefs = getPreferenceStore();
		String solverString = prefs.getString(PreferenceConstants.PREF_SOLVER).toUpperCase().replaceAll(" ", "");
		SolverOption solver = SolverOption.valueOf(solverString);
		return solver;
	}

	private static IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	public static KindApi getKindApi(String modelChecker, String remoteUrl) {
		switch (modelChecker) {
		case PreferenceConstants.MODEL_CHECKER_JKIND:
			return getJKindApi();
		case PreferenceConstants.MODEL_CHECKER_KIND2:
			return getKind2Api();
		case PreferenceConstants.MODEL_CHECKER_KIND2WEB:
			return getKind2WebApi(remoteUrl);
		case PreferenceConstants.MODEL_CHECKER_SALLY:
			return getSallyApi();
		default:
			throw new IllegalArgumentException("Unknown model checker setting: " + modelChecker);
		}
	}

	public static KindApi getConsistencyApi() {
		KindApi api = getKindApi();

		if (api instanceof JKindApi) {
			IPreferenceStore prefs = getPreferenceStore();
			int depth = prefs.getInt(PreferenceConstants.PREF_CONSIST_DEPTH) + 1;
			((JKindApi) api).setN(depth);
			((JKindApi) api).disableInvariantGeneration();
			// ((JKindApi) api).disableKInduction();
			((JKindApi) api).setPdrMax(0);
		}
		return api;
	}

	private static JKindApi getJKindApi() {
		IPreferenceStore prefs = getPreferenceStore();
		JKindApi api = new JKindApi();
		api.setJKindJar(getJKindJar());
		try {
			api.setEnvironment("Z3_HOME", Z3Plugin.getZ3Directory());
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			// Z3Plugin not present
		} catch (Exception e) {
			// Some unknown exception finding Z3
			e.printStackTrace();
		}

		String solverString = prefs.getString(PreferenceConstants.PREF_SOLVER).toUpperCase().replaceAll(" ", "");
		SolverOption solver = SolverOption.valueOf(solverString);
		api.setSolver(solver);

		if (prefs.getBoolean(PreferenceConstants.PREF_INDUCT_CEX)) {
			api.setInductiveCounterexamples();
		}
		if (prefs.getBoolean(PreferenceConstants.PREF_SMOOTH_CEX) && solver == SolverOption.YICES) {
			api.setSmoothCounterexamples();
		}
		if (prefs.getBoolean(PreferenceConstants.PREF_SUPPORT)) {
			api.setIvcReduction();
		}
		api.setN(prefs.getInt(PreferenceConstants.PREF_DEPTH));
		api.setTimeout(prefs.getInt(PreferenceConstants.PREF_TIMEOUT));
		api.setPdrMax(prefs.getInt(PreferenceConstants.PREF_PDR_MAX));
		// TODO set pdr invariants as preferences option
		// api.setPdrInvariants();
		if (prefs.getBoolean(PreferenceConstants.PREF_NO_KINDUCTION)) {
			api.disableKInduction();
		}
		return api;
	}

	public static JRealizabilityApi getJRealizabilityApi() {
		IPreferenceStore prefs = getPreferenceStore();
		JRealizabilityApi api = new JRealizabilityApi();
		api.setJKindJar(getJKindJar());
		try {
			api.setEnvironment("Z3_HOME", Z3Plugin.getZ3Directory());
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			// Z3Plugin not present
		} catch (Exception e) {
			// Some unknown exception finding Z3
			e.printStackTrace();
		}

		api.setN(prefs.getInt(PreferenceConstants.PREF_DEPTH));
		api.setTimeout(prefs.getInt(PreferenceConstants.PREF_TIMEOUT));

		return api;
	}

	public static String getJKindJar() {
		return com.collins.trustedsystems.jkindapi.PluginUtil.getJKindJar();
	}

	private static Kind2Api getKind2Api() {
		IPreferenceStore prefs = getPreferenceStore();
		Kind2Api api = new Kind2Api();
		api.setTimeout(prefs.getInt(PreferenceConstants.PREF_TIMEOUT));
		return api;
	}

	private static Kind2WebApi getKind2WebApi(String uri) {
		IPreferenceStore prefs = getPreferenceStore();
		Kind2WebApi api = new Kind2WebApi(uri);
		api.setTimeout(prefs.getInt(PreferenceConstants.PREF_TIMEOUT));
		return api;
	}

	private static SallyApi getSallyApi() {
		IPreferenceStore prefs = getPreferenceStore();
		SallyApi api = new SallyApi();
		api.setTimeout(prefs.getInt(PreferenceConstants.PREF_TIMEOUT));
		return api;
	}
}
