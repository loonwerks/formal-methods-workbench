package com.collins.fmw.agree.command;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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

			System.out.println("program name: " + programName);

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
