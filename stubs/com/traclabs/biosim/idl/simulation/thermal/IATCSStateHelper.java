package com.traclabs.biosim.idl.simulation.thermal;
/**
 *	Generated from IDL definition of enum "IATCSState"
 *	@author JacORB IDL compiler 
 */

public final class IATCSStateHelper
{
	private static org.omg.CORBA.TypeCode _type = null;
	public static org.omg.CORBA.TypeCode type ()
	{
		if (_type == null)
		{
			_type = org.omg.CORBA.ORB.init().create_enum_tc(com.traclabs.biosim.idl.simulation.thermal.IATCSStateHelper.id(),"IATCSState",new String[]{"idle","operational","armed","transitioning"});
		}
		return _type;
	}

	public static void insert (final org.omg.CORBA.Any any, final com.traclabs.biosim.idl.simulation.thermal.IATCSState s)
	{
		any.type(type());
		write( any.create_output_stream(),s);
	}

	public static com.traclabs.biosim.idl.simulation.thermal.IATCSState extract (final org.omg.CORBA.Any any)
	{
		return read(any.create_input_stream());
	}

	public static String id()
	{
		return "IDL:com/traclabs/biosim/idl/simulation/thermal/IATCSState:1.0";
	}
	public static IATCSState read (final org.omg.CORBA.portable.InputStream in)
	{
		return IATCSState.from_int(in.read_long());
	}

	public static void write (final org.omg.CORBA.portable.OutputStream out, final IATCSState s)
	{
		out.write_long(s.value());
	}
}
