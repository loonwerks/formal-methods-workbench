package com.collins.atc.agree.command;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.linking.ILinker;
import org.eclipse.xtext.linking.lazy.LazyLinker;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.Mode;
import org.osate.aadl2.modelsupport.errorreporting.AbstractParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterFactory;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterManager;
import org.osate.aadl2.modelsupport.errorreporting.QueuingParseErrorReporter;
import org.osate.aadl2.util.OsateDebug;
import org.osate.annexsupport.AnnexLinkingService;
import org.osate.annexsupport.AnnexParser;
import org.osate.annexsupport.AnnexUtil;

import com.rockwellcollins.atc.agree.parsing.AgreeAnnexLinkingService;
import com.rockwellcollins.atc.agree.parsing.AgreeAnnexParser;

import antlr.RecognitionException;

public class AgreeAgent extends LazyLinker {

	private ParseErrorReporterFactory factory;

	// Instantiating parseErrManager when afterModelLinked is recursively called
	// (see l244) deletes the error markers (see ParseErrorReporterManager at l120)
	// of the annexes not built with xtext(for example BA).
	// That why parseErrManager is an class attribute.
	private final ParseErrorReporterManager parseErrManager;

	// Control flag for cleaning the error markers from the previous stack of recursive
	// calls of afterModelLinked.
	private boolean hasToClean = true;

	private boolean standalone = false;

	AgreeAgent() {
//		try {
//			AnnexRegistry.getRegistry(AnnexRegistry.ANNEX_PARSER_EXT_ID);
//		} catch (NoClassDefFoundError e) {
//			// we're running without osgi
//			standalone = true;
//		}
		factory = QueuingParseErrorReporter.factory;
		parseErrManager = new ParseErrorReporterManager(factory);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.xtext.linking.impl.AbstractCleaningLinker#afterModelLinked(org.eclipse.emf.ecore.EObject,
	 * org.eclipse.xtext.diagnostics.IDiagnosticConsumer)
	 */
	@Override
	protected void afterModelLinked(EObject model, IDiagnosticConsumer diagnosticsConsumer) {
		// we can't process annexes in standalone mode yet
		if (standalone) {
			return;
		}

		String filename = model.eResource().getURI().lastSegment();
		// set up reporter for ParseErrors

		boolean hasToRestoreCleanFlag = false;

		if (hasToClean) // Don't clean the error markers after the first recursive call of this method.
		{
			parseErrManager.clean();
			hasToRestoreCleanFlag = true;
			hasToClean = false;
		}

		final ParseErrorReporter errReporter = parseErrManager.getReporter(model.eResource());
		if (errReporter instanceof AbstractParseErrorReporter) {
			((AbstractParseErrorReporter) errReporter).setContextResource(model.eResource());
		}


//		AnnexParserRegistry registry = (AnnexParserRegistry) AnnexRegistry
//				.getRegistry(AnnexRegistry.ANNEX_PARSER_EXT_ID);
//		AnnexResolverRegistry resolverregistry = (AnnexResolverRegistry) AnnexRegistry
//				.getRegistry(AnnexRegistry.ANNEX_RESOLVER_EXT_ID);
//		AnnexLinkingServiceRegistry linkingserviceregistry = (AnnexLinkingServiceRegistry) AnnexRegistry
//				.getRegistry(AnnexRegistry.ANNEX_LINKINGSERVICE_EXT_ID);

		// Don't resolve annexes if there are parsing errors.
		if (model instanceof AadlPackage) {
			// do this only for packages
			List<DefaultAnnexLibrary> all = AnnexUtil.getAllDefaultAnnexLibraries((AadlPackage) model);
			for (DefaultAnnexLibrary defaultAnnexLibrary : all) {
				INode node = NodeModelUtils.findActualNodeFor(defaultAnnexLibrary);
				int line = node.getStartLine() + computeLineOffset(node);
				int offset = AnnexUtil.getAnnexOffset(defaultAnnexLibrary);
				AnnexLibrary al = null;
				// look for plug-in parser
				String annexText = defaultAnnexLibrary.getSourceText();
				String annexName = defaultAnnexLibrary.getName();
				if (annexText != null && annexText.length() > 6 && annexName != null) {
					// strip {** **}
					if (annexText.startsWith("{**")) {
						annexText = annexText.substring(3, annexText.length() - 3);
					}
					AnnexParser ap = new AgreeAnnexParser();
					try {
						int errs = errReporter.getNumErrors();
						al = ap.parseAnnexLibrary(annexName, annexText, filename, line, offset, errReporter);
						if (al != null)// && errReporter.getNumErrors() == errs)
						{
							al.setName(annexName);
							defaultAnnexLibrary.setParsedAnnexLibrary(al);

//							AnnexResolver resolver = new AnnexResolverProxy(null);

							AnnexLinkingService linkingservice = new AgreeAnnexLinkingService();
//							if (resolver != null && hasToResolveAnnex && errReporter.getNumErrors() == errs) {
//								errs = resolveErrManager.getNumErrors();
//								resolver.resolveAnnex(annexName, Collections.singletonList(al), resolveErrManager);
//								if (errs != resolveErrManager.getNumErrors()) {
//									defaultAnnexLibrary.setParsedAnnexLibrary(null);
//								}
//							} else
							if (linkingservice != null) {
								try {
									final ListBasedDiagnosticConsumer consumer = new ListBasedDiagnosticConsumer();
									Resource res = model.eResource();
									ILinker linker = ((XtextResource) res).getLinker();
									linker.linkModel(al, consumer);
									res.getErrors().addAll(consumer.getResult(Severity.ERROR));
									res.getWarnings().addAll(consumer.getResult(Severity.WARNING));
								} catch (Exception e) {
									errReporter.error(filename, line, "Linking Service error");
								}
							}
						}
					} catch (RecognitionException e) {
						errReporter.error(filename, line, "Major parsing error");
					}
				}
			}
		}
		// do this for both packages and property sets
		List<DefaultAnnexSubclause> asl = AnnexUtil.getAllDefaultAnnexSubclauses(model);
		for (DefaultAnnexSubclause defaultAnnexSubclause : asl) {

			INode node = NodeModelUtils.findActualNodeFor(defaultAnnexSubclause);

			if (node == null) {
				OsateDebug.osateDebug("Annex not found for code: " + defaultAnnexSubclause.getSourceText());
				continue;
			}
			int offset = node.getOffset();
			int line = node.getStartLine() + computeLineOffset(node);
			offset = AnnexUtil.getAnnexOffset(defaultAnnexSubclause);
			// look for plug-in parser
			String annexText = defaultAnnexSubclause.getSourceText();
			String annexName = defaultAnnexSubclause.getName();
			if (annexText != null && annexText.length() > 6 && annexName != null) {
				// strip {** **}
				if (annexText.startsWith("{**")) {
					annexText = annexText.substring(3, annexText.length() - 3);
				}
				AnnexParser ap = new AgreeAnnexParser();
				try {
					int errs = errReporter.getNumErrors();
					AnnexSubclause asc = ap.parseAnnexSubclause(annexName, annexText, filename, line, offset,
							errReporter);
					if (asc != null)// && errReporter.getNumErrors() == errs)
					{
						asc.setName(annexName);
						defaultAnnexSubclause.setParsedAnnexSubclause(asc);
						// copy in modes list
						EList<Mode> inmodelist = defaultAnnexSubclause.getInModes();
						for (Mode mode : inmodelist) {
							asc.getInModes().add(mode);
						}

						// now resolve reference so we messages if we have references to undefined items
//						AnnexResolver resolver = resolverregistry.getAnnexResolver(annexName);
						AnnexLinkingService linkingservice = new AgreeAnnexLinkingService();
//						if (resolver != null && hasToResolveAnnex && errReporter.getNumErrors() == errs) {// Don't resolve any annex with parsing error.)
//							errs = resolveErrManager.getNumErrors();
//							resolver.resolveAnnex(annexName, Collections.singletonList(asc), resolveErrManager);
//							if (errs != resolveErrManager.getNumErrors()) {
//								defaultAnnexSubclause.setParsedAnnexSubclause(null);
//							}
//						} else
						if (linkingservice != null) {
							try {
								final ListBasedDiagnosticConsumer consumer = new ListBasedDiagnosticConsumer();
								Resource res = model.eResource();
								ILinker linker = ((XtextResource) res).getLinker();
								linker.linkModel(asc, consumer);
								res.getErrors().addAll(consumer.getResult(Severity.ERROR));
								res.getWarnings().addAll(consumer.getResult(Severity.WARNING));
							} catch (Exception e) {
								errReporter.error(filename, line, "Linking Service error");
							}
						}
					}
				} catch (RecognitionException e) {
					errReporter.error(filename, line, "Major uncaught parsing error");
				}
			}
		}

		// The first recursive call of this method reset the flag for others
		// stacks of recursive calls of this method.
		if (hasToRestoreCleanFlag) {
			hasToClean = true;
			hasToRestoreCleanFlag = false;
		}
	}

	// Compute the number of line between the token "annex" and the token "{**".
	// TODO test under windows.
	private int computeLineOffset(INode node) {
		int result = 0;
		boolean next = true;
		char c;
		int index = 0;
		String text = node.getText();

		// Trim the space or new line before the keyword "annex".
		while (text.charAt(index++) != 'a' && index < text.length()) {
			continue;
		}

		index += 4; // Complete the word "annex".

		while (next && index < text.length()) {
			c = text.charAt(index);

			if (c == '\n') {
				result++;
			} else if (c == '{') {
				next = false;
			}

			index++;
		}

		return result;
	}
}
