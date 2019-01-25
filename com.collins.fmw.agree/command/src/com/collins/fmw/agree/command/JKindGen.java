package com.collins.fmw.agree.command;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.InstanceFactory;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instantiation.InstantiateModel;
import org.osate.aadl2.modelsupport.AadlConstants;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.errorreporting.MarkerAnalysisErrorReporter;
import org.osate.annexsupport.AnnexUtil;

import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.Contract;
import com.rockwellcollins.atc.agree.agree.SpecStatement;
import com.rockwellcollins.atc.agree.analysis.AgreeException;
import com.rockwellcollins.atc.agree.analysis.AgreeLayout;
import com.rockwellcollins.atc.agree.analysis.AgreeRenaming;
import com.rockwellcollins.atc.agree.analysis.AgreeUtils;
import com.rockwellcollins.atc.agree.analysis.ast.AgreeProgram;
import com.rockwellcollins.atc.agree.analysis.lustre.visitors.RenamingVisitor;
import com.rockwellcollins.atc.agree.analysis.views.AgreeResultsLinker;

import jkind.api.results.JKindResult;
import jkind.lustre.Node;
import jkind.lustre.Program;

public class JKindGen {
	protected AgreeResultsLinker linker = new AgreeResultsLinker();
	protected Queue<JKindResult> queue = new ArrayDeque<>();
	protected AtomicReference<IProgressMonitor> monitorRef = new AtomicReference<>();

//	private enum AnalysisType {
//		AssumeGuarantee, Consistency, Realizability
//	};

	protected SystemInstance getSysInstance(ComponentImplementation ci, Resource aadlResource) {
// ORIGINAL
//		try {
//			return InstantiateModel.buildInstanceModelFile(ci);
//		} catch (Exception e) {
////			Dialog.showError("Model Instantiate", "Error while re-instantiating the model: " + e.getMessage());
//			throw new AgreeException("Error Instantiating model");
//		}

// VERSION 1
//		SystemInstance root = InstanceFactory.eINSTANCE.createSystemInstance();
//		final String instanceName = ci.getTypeName() + "_" + ci.getImplementationName();
//
//		root.setComponentImplementation(ci);
//		root.setName(instanceName);
//		root.setCategory(ci.getCategory());
//		return root;

//VERSION 2
		final InstantiateModel instantiateModel = new InstantiateModel(new NullProgressMonitor(),
				new AnalysisErrorReporterManager(
						new MarkerAnalysisErrorReporter.Factory(AadlConstants.INSTANTIATION_OBJECT_MARKER)));


		SystemInstance root = InstanceFactory.eINSTANCE.createSystemInstance();
		final String instanceName = ci.getTypeName() + "_" + ci.getImplementationName();

		root.setComponentImplementation(ci);
		root.setName(instanceName);
		root.setCategory(ci.getCategory());
		aadlResource.getContents().add(root);
		// Needed to save the root object because we may attach warnings to the
		// IResource as we build it.


		try {
			instantiateModel.fillSystemInstance(root);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return root;

	}

	private ComponentImplementation getComponentImplementation(AadlPackage root) {

		List<ComponentImplementation> cis = getComponentImplementations(root);
		if (cis.size() == 0) {
			throw new AgreeException("AADL Component Type has no implementation to verify");
		} else if (cis.size() == 1) {
			ComponentImplementation ci = cis.get(0);
			return ci;
		} else {
			throw new AgreeException(
					"AADL Component Type has multiple implementations to verify: please select just one");
		}
	}

	private List<ComponentImplementation> getComponentImplementations(AadlPackage pkg) {
		List<ComponentImplementation> result = new ArrayList<>();
		for (ComponentImplementation ci : EcoreUtil2.getAllContentsOfType(pkg, ComponentImplementation.class)) {
			result.add(ci);
		}
		return result;
	}

	public List<Pair<String, Program>> run(Resource resource) {

		List<Pair<String, Program>> programPairs = new ArrayList<Pair<String, Program>>();
		List<EObject> contents = resource.getContents();

		for (EObject o : contents) {

			if (o instanceof AadlPackage) {

				ComponentImplementation ci = getComponentImplementation((AadlPackage) o);
				SystemInstance si = getSysInstance(ci, resource);

				programPairs.addAll(genComponentPrograms(ci.getName(), si));

//				while (!queue.isEmpty()) {
//					JKindResult result = queue.peek();
//					result.getName();
//
//					Program program = linker.getProgram(result);
//					programs.add(program);
////					System.out.println("OOGA PROGRAM: " + program);
//
//					queue.remove();
//				}
//
//				while (!queue.isEmpty()) {
//					queue.remove().cancel();
//				}

			} else {
				System.out.println("not running jkind generation: " + o);

			}
		}

		return programPairs;

	}

//	private void wrapVerificationResult(ComponentInstance si, CompositeAnalysisResult wrapper) {
//		AgreeProgram agreeProgram = new ASTBuilder().getAgreeProgram(si, false);
//
//		Program program = LustreASTBuilder.getAssumeGuaranteeLustreProgram(agreeProgram);
//
//		List<Pair<String, Program>> consistencies = LustreASTBuilder.getConsistencyChecks(agreeProgram);

//		wrapper.addChild(
//				createVerification("Contract Guarantees", si, program, agreeProgram, AnalysisType.AssumeGuarantee));
//		for (Pair<String, Program> consistencyAnalysis : consistencies) {
//			wrapper.addChild(createVerification(consistencyAnalysis.getFirst(), si, consistencyAnalysis.getSecond(),
//					agreeProgram, AnalysisType.Consistency));
//		}
//	}

	protected String getNestedMessages(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		while (e != null) {
			if (e.getMessage() != null && !e.getMessage().isEmpty()) {
				pw.println(e.getMessage());
			}
			e = e.getCause();
		}
		pw.close();
		return sw.toString();
	}

	private List<Pair<String, Program>> genComponentPrograms(String name, ComponentInstance ci) {
//		CompositeAnalysisResult result = new CompositeAnalysisResult("Verification for " + name);

		List<Pair<String, Program>> programs = new ArrayList<Pair<String, Program>>();

		System.out.println("Comp Instance: " + ci);
		if (containsAGREEAnnex(ci)) {
//			wrapVerificationResult(ci, result);
			AgreeProgram agreeProgram = new ASTBuilder().getAgreeProgram(ci, false);
			Program program = LustreASTBuilder.getAssumeGuaranteeLustreProgram(agreeProgram);

			List<Pair<String, Program>> consistencies = LustreASTBuilder.getConsistencyChecks(agreeProgram);

			System.out.println("consist size: " + consistencies.size());

			createVerification("Contract Guarantees", ci, program, agreeProgram);
			programs.add(Tuples.create("Contract Guarantees", program));

			for (Pair<String, Program> consistencyAnalysis : consistencies) {
				createVerification(consistencyAnalysis.getFirst(), ci, consistencyAnalysis.getSecond(), agreeProgram);
			}
			programs.addAll(consistencies);

//			ComponentImplementation compImpl = AgreeUtils.getInstanceImplementation(ci);
			for (ComponentInstance subInst : ci.getComponentInstances()) {
				if (AgreeUtils.getInstanceImplementation(subInst) != null) {
					programs.addAll(genComponentPrograms(subInst.getName(), subInst));
				}
			}

//			if (result.getChildren().size() != 0) {
//				linker.setComponent(result, compImpl);
//
//			}
		}
		return programs;
	}

	private void createVerification(String resultName, ComponentInstance compInst, Program lustreProgram,
			AgreeProgram agreeProgram) {
//		AgreeAutomaterRegistry aAReg = (AgreeAutomaterRegistry) ExtensionRegistry
//				.getRegistry(ExtensionRegistry.AGREE_AUTOMATER_EXT_ID);
//		List<AgreeAutomater> automaters = aAReg.getAgreeAutomaters();
		AgreeRenaming renaming = new AgreeRenaming();
		AgreeLayout layout = new AgreeLayout();
		Node mainNode = null;
		for (Node node : lustreProgram.nodes) {
			if (node.id.equals(lustreProgram.main)) {
				mainNode = node;
				break;
			}
		}
		if (mainNode == null) {
			throw new AgreeException("Could not find main lustre node after translation");
		}

		RenamingVisitor.addRenamings(lustreProgram, renaming, compInst, layout);
//		List<String> properties = new ArrayList<>();
//		addProperties(renaming, properties, mainNode, agreeProgram);

//		for (AgreeAutomater aa : automaters) {
//			renaming = aa.rename(renaming);
//			layout = aa.transformLayout(layout);
//		}
//
//		JKindResult result;
//		switch (analysisType) {
//		case Consistency:
//			result = new ConsistencyResult(resultName, mainNode.properties, Collections.singletonList(true), renaming);
//			break;
//		case Realizability:
//			result = new JRealizabilityResult(resultName, renaming);
//			break;
//		case AssumeGuarantee:
//			result = new JKindResult(resultName, properties, renaming);
//			break;
//		default:
//			throw new AgreeException("Unhandled Analysis Type");
//		}
//		queue.add(result);
//
//		ComponentImplementation compImpl = AgreeUtils.getInstanceImplementation(compInst);
//		linker.setProgram(result, lustreProgram);
//		linker.setComponent(result, compImpl);
//		linker.setContract(result, getContract(compImpl));
//		linker.setLayout(result, layout);
//		linker.setReferenceMap(result, renaming.getRefMap());
//		linker.setLog(result, AgreeLogger.getLog());
//		linker.setRenaming(result, renaming);
//
//		// System.out.println(program);
//		return result;

	}

	private boolean containsAGREEAnnex(ComponentInstance ci) {
		ComponentClassifier compClass = ci.getComponentClassifier();
		if (compClass instanceof ComponentImplementation) {
			compClass = ((ComponentImplementation) compClass).getType();
		}
		for (AnnexSubclause annex : AnnexUtil.getAllAnnexSubclauses(compClass,
				AgreePackage.eINSTANCE.getAgreeContractSubclause())) {
			if (annex instanceof AgreeContractSubclause) {

				Contract c = ((AgreeContractSubclause) annex).getContract();
				System.out.println("ooga contract: " + c);
				if (c instanceof AgreeContract) {
					for (SpecStatement spec : ((AgreeContract) c).getSpecs()) {
						System.out.println("ooga spec: " + spec);
					}
				}
				return true;
			}
		}
		return false;
	}

}
