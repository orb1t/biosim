package com.traclabs.biosim.server.simulation.air;

import com.traclabs.biosim.idl.simulation.air.NitrogenStorePOATie;
import com.traclabs.biosim.server.framework.GenericServer;
/**
 * The Nitrogen Store Server.  Creates an instance of the NitrogenStore and registers it with the nameserver.
 *
 * @author    Scott Bell
 */
public class NitrogenStoreServer extends GenericServer{
	
	/**
	* Instantiates the server and binds it to the name server.
	* @param args aren't used for anything
	*/
	public static void main(String args[]) {
		NitrogenStoreServer myServer = new NitrogenStoreServer();
		NitrogenStoreImpl myNitrogenStore = new NitrogenStoreImpl(GenericServer.getIDfromArgs(args), GenericServer.getNamefromArgs(args));
		myServer.registerServerAndRun(new NitrogenStorePOATie(myNitrogenStore), myNitrogenStore.getModuleName(), myNitrogenStore.getID());
	}
}

