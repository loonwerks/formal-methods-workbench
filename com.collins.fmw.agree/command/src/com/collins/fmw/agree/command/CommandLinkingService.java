package com.collins.fmw.agree.command;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.nodemodel.INode;
import org.osate.annexsupport.AnnexLinkingService;
import org.osate.xtext.aadl2.linking.Aadl2LinkingService;


public class CommandLinkingService extends Aadl2LinkingService {

  //AnnexLinkingServiceRegistry annexlinkingserviceregistry;

	@Override
	protected void Aadl2linkingService() {
      //initAnnexLinkingServiceRegistry();
	}

	@Override
	protected void initAnnexLinkingServiceRegistry() {
      //if (annexlinkingserviceregistry == null) {
      //	annexlinkingserviceregistry = (AnnexLinkingServiceRegistry) AnnexRegistry
      //			.getRegistry(AnnexRegistry.ANNEX_LINKINGSERVICE_EXT_ID);
      //}
	}

	@Override
	public List<EObject> getLinkedObjects(EObject context, EReference reference, INode node)
			throws IllegalNodeException {

    String annexName = "agree";
    AnnexLinkingService linkingservice = new AgreeAnnexLinkingService();
    List<EObject> result = linkingservice.resolveAnnexReference(annexName, context, reference, node);

    return result;
  }
}
