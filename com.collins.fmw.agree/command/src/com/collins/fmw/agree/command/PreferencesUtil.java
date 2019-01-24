package com.collins.fmw.agree.command;

import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

import com.rockwellcollins.atc.agree.analysis.preferences.PreferenceConstants;

import jkind.JKindException;
import jkind.SolverOption;
import jkind.api.JKindApi;
import jkind.api.JRealizabilityApi;
import jkind.api.KindApi;

public class PreferencesUtil {

	private static int timeout = 100;
	private static int depth = 200;
	private static int consistDepth = 1;
	private static boolean inductCex = true;
	private static boolean smoothCex = false;
	private static boolean support = true;
	private static int pdrMax = 0;
	private static boolean noKInduction = false;

	public static SolverOption getSolverOption() {
		String solverString = PreferenceConstants.SOLVER_Z3;
		SolverOption solver = SolverOption.valueOf(solverString);
		return solver;
	}

	public static String getJKindJar() {

//		String bundleId = "com.collins.fmw.agree.command";
//		Bundle bundle = Platform.getBundle(bundleId);
//		URL url = bundle.getEntry("static/jkind.jar");
		try {
//			URL fileUrl = FileLocator.toFileURL(url);

			File f = new File(PreferencesUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String targetDir = f.getParent();
			InputStream input = PreferencesUtil.class.getResourceAsStream("/resources/jkind.jar");
			File target = new File(targetDir, "jkind.jar");
			java.nio.file.Files.copy(input, target.toPath(), StandardCopyOption.REPLACE_EXISTING);

			System.out.println("file path: " + target.toString());

			String path = target.toString();
			input.close();
			return path;

		} catch (Exception e) {
			e.printStackTrace();
			throw new JKindException("Unable to extract jkind.jar from plug-in", e);
		}
	}

	private static String getArchDir() {
		String name = System.getProperty("os.name").toLowerCase();
		String arch = System.getProperty("os.arch").toLowerCase();

		if (name.contains("windows")) {
			if (arch.contains("64")) {
				return "x64-win";
			} else {
				return "x86-win";
			}
		} else if (name.contains("mac os x")) {
			return "x64-osx";
		} else if (arch.contains("64")) {
			return "x64-linux";
		} else {
			return "x86-linux";
		}
	}

	private static String getExecutableName() {
		boolean isWindows = System.getProperty("os.name").startsWith("Windows");
		return isWindows ? "z3.exe" : "z3";
	}

	public static String getZ3Directory() {
		try {

			String archDir = getArchDir();
			String exeName = getExecutableName();
			InputStream input = PreferencesUtil.class.getResourceAsStream("/resources/" + archDir + "/" + exeName);

			File f = new File(PreferencesUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String targetDir = f.getParent();
			File target = new File(targetDir, exeName);
			java.nio.file.Files.copy(input, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			target.setExecutable(true);
			return target.getParent();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Unable to extract z3 from plug-in", e);
		}
	}

	public static KindApi getConsistencyApi() {
		KindApi api = getJKindApi();

		if (api instanceof JKindApi) {
			((JKindApi) api).setN(consistDepth + 1);
			((JKindApi) api).disableInvariantGeneration();
			((JKindApi) api).setPdrMax(0);
		}
		return api;
	}

	public static JKindApi getJKindApi() {
		JKindApi api = new JKindApi();
		api.setJKindJar(getJKindJar());
		try {
			api.setEnvironment("Z3_HOME", getZ3Directory());
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			// Z3Plugin not present
		}

		String solverString = PreferenceConstants.SOLVER_Z3;
		SolverOption solver = SolverOption.valueOf(solverString);
		api.setSolver(solver);

		if (inductCex) {
			api.setInductiveCounterexamples();
		}
		if (smoothCex && solver == SolverOption.YICES) {
			api.setSmoothCounterexamples();
		}

		if (support) {
			api.setIvcReduction();
		}
		api.setN(depth);
		api.setTimeout(timeout);
		api.setPdrMax(pdrMax);
		// TODO set pdr invariants as preferences option
		// api.setPdrInvariants();
		if (noKInduction) {
			api.disableKInduction();
		}
		return api;
	}

	public static JRealizabilityApi getJRealizabilityApi() {
		JRealizabilityApi api = new JRealizabilityApi();
		api.setJKindJar(getJKindJar());
		try {
			api.setEnvironment("Z3_HOME", getZ3Directory());
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			// Z3Plugin not present
		}

		api.setN(depth);
		api.setTimeout(timeout);

		return api;
	}



}
