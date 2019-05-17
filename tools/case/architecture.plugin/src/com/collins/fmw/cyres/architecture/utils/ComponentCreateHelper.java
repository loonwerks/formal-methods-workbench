package com.collins.fmw.cyres.architecture.utils;

import org.eclipse.emf.ecore.EClass;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AbstractImplementation;
import org.osate.aadl2.AbstractSubcomponent;
import org.osate.aadl2.AbstractType;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.Port;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.SystemSubcomponent;
import org.osate.aadl2.SystemType;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;

/**
 * This class provides methods for creating component features without knowing
 * the component type or implementation class in advance
 */
public class ComponentCreateHelper {


	public ComponentCreateHelper() {

	}

	public static EClass getTypeClass(ComponentCategory compCategory) {
		if (compCategory == ComponentCategory.SYSTEM) {
			return Aadl2Package.eINSTANCE.getSystemType();
		} else if (compCategory == ComponentCategory.ABSTRACT) {
			return Aadl2Package.eINSTANCE.getAbstractType();
		} else if (compCategory == ComponentCategory.PROCESS) {
			return Aadl2Package.eINSTANCE.getProcessType();
		} else if (compCategory == ComponentCategory.THREAD) {
			return Aadl2Package.eINSTANCE.getThreadType();
		} else {
			return null;
		}
	}

	public static EClass getImplClass(ComponentCategory compCategory) {
		if (compCategory == ComponentCategory.SYSTEM) {
			return Aadl2Package.eINSTANCE.getSystemImplementation();
		} else if (compCategory == ComponentCategory.ABSTRACT) {
			return Aadl2Package.eINSTANCE.getAbstractImplementation();
		} else if (compCategory == ComponentCategory.PROCESS) {
			return Aadl2Package.eINSTANCE.getProcessImplementation();
		} else if (compCategory == ComponentCategory.THREAD) {
			return Aadl2Package.eINSTANCE.getThreadImplementation();
		} else {
			return null;
		}
	}

	// Ports
	public static Port createOwnedEventDataPort(ComponentType compType) {
		if (compType instanceof SystemType) {
			return ((SystemType) compType).createOwnedEventDataPort();
		} else if (compType instanceof AbstractType) {
			return ((AbstractType) compType).createOwnedEventDataPort();
		} else if (compType instanceof ProcessType) {
			return ((ProcessType) compType).createOwnedEventDataPort();
		} else if (compType instanceof ThreadType) {
			return ((ThreadType) compType).createOwnedEventDataPort();
		} else {
			return null;
		}
	}

	public static Port createOwnedDataPort(ComponentType compType) {
		if (compType instanceof SystemType) {
			return ((SystemType) compType).createOwnedDataPort();
		} else if (compType instanceof AbstractType) {
			return ((AbstractType) compType).createOwnedDataPort();
		} else if (compType instanceof ProcessType) {
			return ((ProcessType) compType).createOwnedDataPort();
		} else if (compType instanceof ThreadType) {
			return ((ThreadType) compType).createOwnedDataPort();
		} else {
			return null;
		}
	}

	public static Port createOwnedEventPort(ComponentType compType) {
		if (compType instanceof SystemType) {
			return ((SystemType) compType).createOwnedEventPort();
		} else if (compType instanceof AbstractType) {
			return ((AbstractType) compType).createOwnedEventPort();
		} else if (compType instanceof ProcessType) {
			return ((ProcessType) compType).createOwnedEventPort();
		} else if (compType instanceof ThreadType) {
			return ((ThreadType) compType).createOwnedEventPort();
		} else {
			return null;
		}
	}

	// Annex subclause
	public static DefaultAnnexSubclause createOwnedAnnexSubclause(ComponentType compType) {
		if (compType instanceof SystemType) {
			return ((SystemType) compType).createOwnedAnnexSubclause();
		} else if (compType instanceof AbstractType) {
			return ((AbstractType) compType).createOwnedAnnexSubclause();
		} else if (compType instanceof ProcessType) {
			return ((ProcessType) compType).createOwnedAnnexSubclause();
		} else if (compType instanceof ThreadType) {
			return ((ThreadType) compType).createOwnedAnnexSubclause();
		} else {
			return null;
		}
	}

//	// Realization
//	public static Realization createOwnedRealization(ComponentImplementation compImpl) {
//		if (compImpl instanceof SystemImplementation) {
//			return ((SystemImplementation) compImpl).createOwnedRealization();
//		} else if (compImpl instanceof AbstractImplementation) {
//			return ((AbstractImplementation) compImpl).createOwnedRealization();
//		} else if (compImpl instanceof ProcessImplementation) {
//			return ((ProcessImplementation) compImpl).createOwnedRealization();
//		} else if (compImpl instanceof ThreadImplementation) {
//			return ((ThreadImplementation) compImpl).createOwnedRealization();
//		} else {
//			return null;
//		}
//	}

	// Subcomponent
	public static Subcomponent createOwnedSubcomponent(ComponentImplementation compImpl,
			ComponentCategory compCategory) {
		if (compImpl instanceof SystemImplementation) {
			if (compCategory == ComponentCategory.SYSTEM) {
				return ((SystemImplementation) compImpl).createOwnedSystemSubcomponent();
			} else if (compCategory == ComponentCategory.ABSTRACT) {
				return ((SystemImplementation) compImpl).createOwnedAbstractSubcomponent();
			} else if (compCategory == ComponentCategory.PROCESS) {
				return ((SystemImplementation) compImpl).createOwnedProcessSubcomponent();
			}
		} else if (compImpl instanceof AbstractImplementation) {
			if (compCategory == ComponentCategory.SYSTEM) {
				return ((AbstractImplementation) compImpl).createOwnedSystemSubcomponent();
			} else if (compCategory == ComponentCategory.ABSTRACT) {
				return ((AbstractImplementation) compImpl).createOwnedAbstractSubcomponent();
			} else if (compCategory == ComponentCategory.PROCESS) {
				return ((AbstractImplementation) compImpl).createOwnedProcessSubcomponent();
			} else if (compCategory == ComponentCategory.THREAD) {
				return ((AbstractImplementation) compImpl).createOwnedThreadSubcomponent();
			}
		} else if (compImpl instanceof ProcessImplementation) {
			if (compCategory == ComponentCategory.ABSTRACT) {
				return ((ProcessImplementation) compImpl).createOwnedAbstractSubcomponent();
			} else if (compCategory == ComponentCategory.THREAD) {
				return ((ProcessImplementation) compImpl).createOwnedThreadSubcomponent();
			}
		}
		return null;
	}

	public static void setSubcomponentType(Subcomponent subcomponent, ComponentImplementation compImpl) {
		if (subcomponent instanceof SystemSubcomponent) {
			((SystemSubcomponent) subcomponent).setSystemSubcomponentType((SystemImplementation) compImpl);
		} else if (subcomponent instanceof AbstractSubcomponent) {
			((AbstractSubcomponent) subcomponent).setAbstractSubcomponentType((AbstractImplementation) compImpl);
		} else if (subcomponent instanceof ProcessSubcomponent) {
			((ProcessSubcomponent) subcomponent).setProcessSubcomponentType((ProcessImplementation) compImpl);
		} else if (subcomponent instanceof ThreadSubcomponent) {
			((ThreadSubcomponent) subcomponent).setThreadSubcomponentType((ThreadImplementation) compImpl);
		}
	}

}
