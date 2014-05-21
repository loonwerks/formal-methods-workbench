package com.rockwellcollins.atc.agree.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.NamedElement;
import org.osate.annexsupport.AnnexContentAssist;
import org.osate.xtext.aadl2.properties.ui.contentassist.PropertiesProposalProvider;

import com.google.inject.Injector;
import com.rockwellcollins.atc.agree.agree.Arg;
import com.rockwellcollins.atc.agree.agree.NestedDotID;
import com.rockwellcollins.atc.agree.agree.RecordDefExpr;
import com.rockwellcollins.atc.agree.agree.RecordType;
import com.rockwellcollins.atc.agree.agree.Type;
import com.rockwellcollins.atc.agree.ui.contentassist.AgreeProposalProvider;
import com.rockwellcollins.atc.agree.ui.internal.AgreeActivator;

public class AgreeAnnexContentAssist implements AnnexContentAssist {
	final private Injector injector = AgreeActivator.getInstance().getInjector(
			AgreeActivator.COM_ROCKWELLCOLLINS_ATC_AGREE_AGREE);


	private PropertiesProposalProvider propPropProv;
	private AgreeAnnexParser parser;
	private EObjectAtOffsetHelper offsetHelper;


	protected PropertiesProposalProvider getLinkingService() {
		if (propPropProv == null) {
			propPropProv = injector.getInstance(AgreeProposalProvider.class);
		}
		return propPropProv;
	}
	
	protected EObjectAtOffsetHelper getOffsetHelper() {
		if(offsetHelper == null){
			offsetHelper = injector.getInstance(EObjectAtOffsetHelper.class);
		}
		return offsetHelper;
	}

	@Override
	public List<String> callAnnexContentAssist(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {

		int offset = context.getOffset();
		offset = (offset <= 0) ? 0 : offset - 1; //get one character back
		EObjectAtOffsetHelper helper = getOffsetHelper();
		EObject grammerObject = null;
		//EObjectAtOffsetHelper
		if(model instanceof DefaultAnnexLibrary){
			AnnexLibrary annexLib = ((DefaultAnnexLibrary)model).getParsedAnnexLibrary();
			XtextResource resource = (XtextResource)annexLib.eResource();
			grammerObject = helper.resolveContainedElementAt(resource, offset);
		}else if(model instanceof DefaultAnnexSubclause){
			AnnexSubclause annexSub = ((DefaultAnnexSubclause)model).getParsedAnnexSubclause();
			XtextResource resource = (XtextResource)annexSub.eResource();
			grammerObject = helper.resolveContainedElementAt(resource, offset);
		}
		
		List<String> results = new ArrayList<>();
		if(grammerObject instanceof NestedDotID){
			results.addAll(getNestedDotIDCandidates((NestedDotID)grammerObject));
		}
		
		return results;
	}

	private List<String> getNestedDotIDCandidates(NestedDotID id) {
		
		List<String> results = new ArrayList<>();
		NamedElement base = id.getBase();
		
		if(base instanceof Arg){
			Type type = ((Arg) base).getType();
			NestedDotID elID = ((RecordType) type).getRecord();
    		NamedElement namedEl = elID.getBase();
    		List<NamedElement> namedEls = new ArrayList<NamedElement>();
    		if(namedEl instanceof ComponentImplementation){
    			namedEls.addAll(((ComponentImplementation) namedEl).getAllSubcomponents());
    		}else if(namedEl instanceof RecordDefExpr){
    			namedEls.addAll(((RecordDefExpr) namedEl).getArgs());
    		}
    		for(NamedElement el : namedEls){
    			results.add(el.getName());
    		}
		}
		return results;
	}

}