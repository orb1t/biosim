package biosim.server.framework;

import biosim.idl.framework.*;
import biosim.idl.util.*;
/**
 * The BioModule Implementation.  Every Module should derive from this as to allow ticking.
 *
 * @author    Scott Bell
 */

public abstract class BioModuleImpl extends BioModulePOA{
	
	/**
	* Called at every tick of the simulation.  Does nothing if not overriden.
	*/
	public void tick(){
	}
	
	/**
	* Logs this module to the Logger
	*/
	public void log(){
	}
	
	/**
	* Returns the name of the module, "Unamed" if not overriden
	* @return the name of this module
	*/
	public String getModuleName(){
		return "Unamed";
	}
}

