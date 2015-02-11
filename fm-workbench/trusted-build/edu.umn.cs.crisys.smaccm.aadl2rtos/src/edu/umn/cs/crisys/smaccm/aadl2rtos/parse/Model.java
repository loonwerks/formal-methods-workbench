/*
Copyright (c) 2011, 2013 Rockwell Collins and the University of Minnesota.
Developed with the sponsorship of the Defense Advanced Research Projects Agency (DARPA).

Permission is hereby granted, free of charge, to any person obtaining a copy of this data,
including any software or models in source or binary form, as well as any drawings, specifications,
and documentation (collectively "the Data"), to deal in the Data without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Data, and to permit persons to whom the Data is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Data.

THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS, SPONSORS, DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE DATA OR THE USE OR OTHER DEALINGS IN THE DATA.
 */

package edu.umn.cs.crisys.smaccm.aadl2rtos.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umn.cs.crisys.smaccm.aadl2rtos.model.dispatcher.Dispatcher;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.dispatcher.IRQDispatcher;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.legacy.ExternalISR;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.legacy.ExternalIRQEvent;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.rpc.RemoteProcedure;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.rpc.RemoteProcedureGroup;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.thread.PortConnection;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.thread.ExternalIRQ;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.thread.SharedData;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.thread.ThreadCalendar;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.thread.ThreadImplementation;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.thread.ThreadInstance;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.thread.ThreadInstancePort;
import edu.umn.cs.crisys.smaccm.aadl2rtos.model.type.Type;

public class Model {
	private String systemImplementationName;
	private String systemInstanceName;
	
	public enum OSTarget {CAmkES, eChronos}; 
	private OSTarget osTarget = OSTarget.eChronos;
		
	// Currently supported targets: QEMU, ODROID, PX4
	public String HWTarget ;
	public String outputDirectory;
	
	// Connection instances - drives number of semaphores
	// (one function per thread implementation, pass in thread instance id)
	List<PortConnection> connectionInstances = new ArrayList<PortConnection>();
	// private ArrayList<String> semaphoreList = new ArrayList<String>();


	// Implementation objects: external.
	List<ThreadImplementation> threadImplementationList = new ArrayList<ThreadImplementation>();
	List<SharedData> sharedDataList = new ArrayList<SharedData>();
	ThreadCalendar threadCalendar = new ThreadCalendar();
	Set<String> sourceFiles = new HashSet<String>();
	List<String> libraryFiles = new ArrayList<String>(); 	

	List<String> legacyMutexList = new ArrayList<String>();
	List<String> legacySemaphoreList = new ArrayList<String>();
	List<ExternalISR> legacyExternalIRQList = new ArrayList<ExternalISR>();
	List<ExternalIRQEvent> legacyIRQEventList = new ArrayList<ExternalIRQEvent>();
	List<ExternalIRQ> externalIRQList = new ArrayList<ExternalIRQ>();
	List<PortConnection> connectionList = new ArrayList<PortConnection>(); 
	
	// type stuff
	Map<String, Type> astTypes = new HashMap<String, Type>();
	Map<String, RemoteProcedureGroup> remoteProcedureGroupMap = new HashMap<>(); 
	Map<String, RemoteProcedure> remoteProcedureMap = new HashMap<>();
	
	boolean generateSystickIRQ;
	
	public enum CommMutualExclusionPrimitive {Semaphore, SuspendInterrupt} ; 
	CommMutualExclusionPrimitive commMutexPrimitive = CommMutualExclusionPrimitive.SuspendInterrupt; 
	
	public enum ISRType {InThreadContextISR, SignalingISR} ; 
	ISRType isrType = ISRType.InThreadContextISR; 
	
	// Model constructor
	public Model(String systemImplementationName, 
	             String systemInstanceName) {
		this.systemImplementationName = systemImplementationName;
		this.systemInstanceName = systemInstanceName;
	}

	/**
   * @return the osTarget
   */
  public OSTarget getOsTarget() {
    return osTarget;
  }

  /**
   * @param osTarget the osTarget to set
   */
  public void setOsTarget(OSTarget osTarget) {
    this.osTarget = osTarget;
  }



  /**
   * @return the hWTarget
   */
  public String getHWTarget() {
    return HWTarget;
  }

  /**
   * @param hWTarget the hWTarget to set
   */
  public void setHWTarget(String hWTarget) {
    HWTarget = hWTarget;
  }

  /**
   * @return the outputDirectory
   */
  public String getOutputDirectory() {
    return outputDirectory;
  }

  /**
   * @param outputDirectory the outputDirectory to set
   */
  public void setOutputDirectory(String outputDirectory) {
    this.outputDirectory = outputDirectory;
  }

  public Set<String> getSourceFiles()  {
	  return this.sourceFiles; 
	}
	
	public List<String> getLibraryFiles()  {
		return this.libraryFiles; 
	}
	
	public CommMutualExclusionPrimitive getCommMutexPrimitive() {
		return this.commMutexPrimitive; 
	}
	
	public ISRType getISRType() {
		return this.isrType; 
	}
	
	public void addSourceFile(String fileName) {
	  this.sourceFiles.add(fileName);
	}
	
	public void addLibraryFile(String fileName) {
		this.libraryFiles.add(fileName);
	}	
	
  public List<IRQDispatcher> getIRQDispatcherList() {
    List<IRQDispatcher> idList = new ArrayList<IRQDispatcher>(); 
    for (ThreadImplementation ti: this.getAllThreadImplementations()) {
      for (Dispatcher d: ti.getDispatcherList()) {
        if (d instanceof IRQDispatcher) {
          idList.add((IRQDispatcher)d);
        }
      }
    }
    return idList;
  }

  public String getSystemImplementationName() {
    return systemImplementationName;
  }

  public String getSystemInstanceName() {
		return this.systemInstanceName;
	}
	
	public ThreadCalendar getThreadCalendar() {
	  return this.threadCalendar;
	}
  
	public List<SharedData> getSharedDataList() {
	  return this.sharedDataList;
	}
	
	/*
	public List<ThreadImplementation> getInternalThreadImplementations() {
	  List<ThreadImplementation> internalThreads = new ArrayList<ThreadImplementation>();
	  for (ThreadImplementation ti: this.threadImplementationList) {
	    if (!ti.getIsExternal()) {
	      internalThreads.add(ti);
	    }
	  }
	  return internalThreads;
	}
	
	public List<ThreadImplementation> getExternalThreadImplementations() {
    List<ThreadImplementation> externalThreads = new ArrayList<ThreadImplementation>();
    for (ThreadImplementation ti: this.threadImplementationList) {
      if (!ti.getIsExternal()) {
        externalThreads.add(ti);
      }
    }
    return externalThreads;
	}
	*/
	public List<ThreadImplementation> getAllThreadImplementations() {
    return this.threadImplementationList;
	}
	
	public List<ThreadImplementation> getActiveThreadImplementations() {
	  List<ThreadImplementation> activeThreads = new ArrayList<ThreadImplementation>(); 
	  for (ThreadImplementation ti: getAllThreadImplementations()) {
	    if (!ti.getIsPassive()) {
	      activeThreads.add(ti);
	    }
	  }
	  return activeThreads;
	}
	
  public List<ThreadImplementation> getPassiveThreadImplementations() {
    List<ThreadImplementation> t = new ArrayList<ThreadImplementation>(); 
    for (ThreadImplementation ti: getAllThreadImplementations()) {
      if (ti.getIsPassive()) {
        t.add(ti);
      }
    }
    return t;
  }

  /**
   * @return the rpcInterfaces
   */
  public Map<String, RemoteProcedureGroup> getRemoteProcedureGroupMap() {
    return remoteProcedureGroupMap;
  }

  /**
   * @param rpcInterfaces the rpcInterfaces to set
   */
  public void setRemoteProcedureGroupMap(Map<String, RemoteProcedureGroup> rpcInterfaces) {
    this.remoteProcedureGroupMap = rpcInterfaces;
  }

  
  /**
   * @return the remoteProcedureMap
   */
  public Map<String, RemoteProcedure> getRemoteProcedureMap() {
    return remoteProcedureMap;
  }

  /**
   * @param remoteProcedureMap the remoteProcedureMap to set
   */
  public void setRemoteProcedureMap(
      Map<String, RemoteProcedure> remoteProcedureMap) {
    this.remoteProcedureMap = remoteProcedureMap;
  }

  public List<ExternalISR> getLegacyExternalIRQs() {
	  return this.legacyExternalIRQList;
	}
	
	public List<ExternalIRQEvent> getLegacyIRQEvents() {
	  return this.legacyIRQEventList;
	}
	
	public List<ExternalIRQ> getExternalIRQs() {
		return this.externalIRQList;
	}
	
	public List<ThreadInstance> getAllThreadInstances() {
	  List<ThreadInstance> list = new ArrayList<ThreadInstance>(); 
	  for (ThreadImplementation t: getAllThreadImplementations()) {
	    list.addAll(t.getThreadInstanceList()); 
	  }
	  return list;
	}
	
	public List<ThreadInstancePort> getAllThreadInstanceInputPorts() {
	  ArrayList<ThreadInstancePort> instances = 
	      new ArrayList<ThreadInstancePort>();
	  for (ThreadInstance ti: getAllThreadInstances()) {
	    instances.addAll(ti.getThreadInstanceInputPorts());
	  }
	  return instances;
	}

	
/*
	public Map<ThreadImplementation, Set<Pair<MyPort, MyPort>>> getThreadSourcePorts() {
		return threadSourcePorts;
	}
*/	

//	public List<ThreadImplementation> getThreads(List<String> threadNameList) {
//		List<ThreadImplementation> threadList = new ArrayList<ThreadImplementation>();
//		for (String threadName : threadNameList) { 
//			threadList.add(threadImplementationMap.get(threadName));
//		}
//		return threadList;
//	}

	
	public List<PortConnection> getConnections() {
	  return this.connectionInstances;
	}

	public List<String> getLegacyMutexList() {
		return this.legacyMutexList;
	}
	
  public List<String> getLegacySemaphoreList() {
    return this.legacySemaphoreList;
  }

  public boolean getGenerateSystickIRQ() {
	  return this.generateSystickIRQ;
	}

  public Map<String, Type> getAstTypes() {
		return this.astTypes;
}


}