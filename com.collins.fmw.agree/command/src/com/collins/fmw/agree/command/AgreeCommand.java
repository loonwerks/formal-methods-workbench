package com.collins.fmw.agree.command;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.Pair;

import com.google.inject.Injector;

import jkind.lustre.Program;

public class AgreeCommand {

	public static void main(String[] args) throws Exception {

		String basisFlag = "basis";
		String componentFlag = "component";

		Options options = new Options();


		Option basisOption = new Option("b", basisFlag, false, "basis file");
		options.addOption(basisOption);

		Option componentOption = new Option("c", componentFlag, false, "component file");
		options.addOption(componentOption);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cli = null;

		try {
			cli = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
		}

		if (cli.getArgList().size() != 1) {
			System.out.println("Wrong number of arguments.");
			formatter.printHelp("utility-name", options);

			System.exit(1);
		}


		Injector injector = new AgreeCommandSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

		// basis library
		String[] basePaths = { "/resources/properties/SEI.aadl", "/resources/properties/Data_Model.aadl",
				"/resources/packages/Base_Types.aadl", "/resources/properties/ARINC653.aadl",
				"/resources/properties/ARINC429.aadl", "/resources/properties/Code_Generation_Properties.aadl",
				"/resources/properties/Predeclared_Property_Sets/AADL_Project.aadl",
				"/resources/properties/Predeclared_Property_Sets/Communication_Properties.aadl",
				"/resources/properties/Predeclared_Property_Sets/Deployment_Properties.aadl",
				"/resources/properties/Predeclared_Property_Sets/Memory_Properties.aadl",
				"/resources/properties/Predeclared_Property_Sets/Modeling_Properties.aadl",
				"/resources/properties/Predeclared_Property_Sets/Programming_Properties.aadl",
				"/resources/properties/Predeclared_Property_Sets/Thread_Properties.aadl",
				"/resources/properties/Predeclared_Property_Sets/Timing_Properties.aadl",
				"/resources/packages/AGREE_PLTL.aadl", "/resources/packages/AGREE_Stdlib.aadl",
				"/resources/packages/Linearizer.aadl", "/resources/packages/DReal.aadl"
		};

		for (int i = 0; i < basePaths.length; i++) {
			String basePath = basePaths[i];

			InputStream input = AgreeCommand.class.getResourceAsStream(basePath);
			URL jarUrl = AgreeCommand.class.getProtectionDomain().getCodeSource().getLocation();
			String dir = new File(jarUrl.getPath()).getParent();
			File target = new File(dir, basePath.substring(1));
			target.getParentFile().mkdirs();
			target.createNewFile();
			java.nio.file.Files.copy(input, target.toPath(), StandardCopyOption.REPLACE_EXISTING);

			resourceSet.getResource(URI.createFileURI(target.getAbsolutePath()), true);
		}
		///////////////

		if (cli.hasOption(basisFlag)) {
			String fileArg = cli.getArgs()[0];
			File basisFile = new File(fileArg);
			String path = basisFile.getAbsolutePath();
			Stream<String> stream = Files.lines(Paths.get(path));
			stream.forEach((line) -> {
				line = line.replaceFirst("^~", System.getProperty("user.home"));
				File aadlFile = new File(line);
				String aadlPath = "";
				if (aadlFile.isAbsolute()) {
					aadlPath = aadlFile.getAbsolutePath();
				} else {
					aadlPath = basisFile.getParentFile().getAbsolutePath() + File.separator + aadlFile.getPath();
				}

				List<Pair<String, Program>> programPairs = new JKindGen().run(resourceSet, aadlPath);
				write(aadlPath, programPairs);


				System.out.println("* aadl path (basis): " + aadlPath);

      });


		} else {
			String fileArg = cli.getArgs()[0];
			File aadlFile = new File(fileArg);
			String aadlPath = aadlFile.getAbsolutePath();
			List<Pair<String, Program>> programPairs = new JKindGen().run(resourceSet, aadlPath);
			write(aadlPath, programPairs);
			System.out.println("* aadl path: " + aadlPath);

		}


	}

	private static void write(String inputPath, List<Pair<String, Program>> programPairs) {

		for (Pair<String, Program> pair : programPairs) {
			String programName = pair.getFirst();
			Program program = pair.getSecond();
			File target = new File(new File(inputPath).getName() + "." + programName + ".jkind");
			FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(target);
				fileWriter.write(program.toString());

				fileWriter.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}


}
