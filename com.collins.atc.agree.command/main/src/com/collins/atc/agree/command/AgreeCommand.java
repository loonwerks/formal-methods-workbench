package com.collins.atc.agree.command;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Injector;

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

				Resource resource = resourceSet.getResource(URI.createFileURI(aadlPath), true);
				validate(resource);
				System.out.println("* aadl path (basis): " + aadlPath);

            });


		} else {
			String fileArg = cli.getArgs()[0];
			File aadlFile = new File(fileArg);
			String aadlPath = aadlFile.getAbsolutePath();

			Resource resource = resourceSet.getResource(URI.createFileURI(aadlPath), true);
			validate(resource);
			System.out.println("* aadl path: " + aadlPath);


		}



	}

	private static List<Issue> issues = new ArrayList<Issue>();
	private static List<String> visited = new ArrayList<String>();

	private static void validate(Resource resource) {

		if (visited.contains(resource.getURI().toString())) {
			return;
		}

		List<EObject> contents = resource.getContents();
		for (EObject o : contents) {
			System.out.println("ooga " + o);
		}

	}



}
