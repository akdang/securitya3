import java.util.*;

public class Role
{
	protected CLevels cLevel;
	protected ILevels iLevel;
	protected EnumSet<CCategories> cCategories;
	protected EnumSet<ICategories> iCategories;
	protected String name;
	
	public static enum CLevels {SL, AM}			//low to high
	public static enum ILevels {ISL, IO, ISP}	//low to high
	public static enum CCategories {SP, SD, SSD}
	public static enum ICategories {ID, IP}
	public static HashMap<String, Role> roles = new HashMap<String, Role>();
	
	public Role(CLevels cLevel, EnumSet<CCategories> cCats, ILevels iLevel, EnumSet<ICategories> iCats, String name)
	{
		this.cLevel = cLevel;
		this.iLevel = iLevel;
		this.cCategories = cCats;
		this.iCategories = iCats;
		this.name = name;
		
	}
	
	public static void populateHash()
	{
		if(roles.size() == 0)
		{
			roles.put(ORDINARY.name, ORDINARY);
			roles.put(APP_DEV.name, APP_DEV);
			roles.put(SYS_PROGMR.name, SYS_PROGMR);
			roles.put(SYS_AUDTR.name, SYS_AUDTR);
			roles.put(SYS_CONTRLR.name, SYS_CONTRLR);
		}
	}
	
	public String toString()
	{
		return name;
	}
	
	/**
	 * return true if this(subject) can read object
	 * under biba and blp
	 */
	public boolean canRead(Type object)
	{
		Role subject = this; //for clarity
		
		//Confidentiality - read down
		if(subject.cLevel.compareTo(object.cLevel) == -1)
			return false;	//subject's confidentiality level dominated by object's 
		if(! subject.cCategories.containsAll(object.cCategories))
			return false;	//subject's confidentiality categories not a superset of object's
		
		//Integrity - read up
		if(object.iLevel.compareTo(subject.iLevel) == -1)
			return false;	//object's integrity level dominated by subject's
		if(! object.iCategories.containsAll(subject.iCategories))	
			return false;	//object integrity categories not a superset of subject's
		
		return true;
	}
	
	/**
	 * return true if this(subject) can write object
	 * under biba and blp
	 */
	public boolean canWrite(Type object)
	{
		Role subject = this;
		
		//Confidentiality - write up
		if(object.cLevel.compareTo(subject.cLevel) == -1)
			return false;	//objects's confidentiality level dominated by subjects's 
		if(! object.cCategories.containsAll(subject.cCategories))
			return false;	//object's confidentiality categories not a superset of subject's
		
		//Integrity - write down
		if(subject.iLevel.compareTo(object.iLevel) == -1)
			return false;	//object's integrity level dominated by subject's
		if(! subject.iCategories.containsAll(object.iCategories))	
			return false;	//object integrity categories not a superset of subject's
		
		return true;
	}
	
	public boolean canOnlyRead(Type object)
	{
		return canRead(object) && !canWrite(object);
	}
	
	public boolean canOnlyWrite(Type object)
	{
		return canWrite(object) && !canRead(object);
	}
	
	public boolean canBoth(Type object)
	{
		return canRead(object) && canWrite(object);
	}
	
	public boolean cantBoth(Type object)
	{
		return !canRead(object) && !canWrite(object);
	}
	
	//-------------------------------- ROLES -------------------------------------//
	//Ordinary Users 	(SL, {SP}) 	(ISL, {IP})
	public static Role ORDINARY = new Role(
			Role.CLevels.SL,
			EnumSet.of(Role.CCategories.SP),
			Role.ILevels.ISL,
			EnumSet.of(Role.ICategories.IP),
			"ORDINARY");
	
	//Application developers 	(SL, {SD}) 	(ISL, {ID})
	public static Role APP_DEV = new Role(
			Role.CLevels.SL,
			EnumSet.of(Role.CCategories.SD),
			Role.ILevels.ISL,
			EnumSet.of(Role.ICategories.ID),
			"APP_DEV");
	
	//System programmers 	(SL, {SSD}) 	(ISL, {ID})
	public static Role SYS_PROGMR = new Role(
			Role.CLevels.SL,
			EnumSet.of(Role.CCategories.SSD),
			Role.ILevels.ISL,
			EnumSet.of(Role.ICategories.ID),
			"SYS_PROGMR");

	//System managers/auditors 	(AM, {SP, SD, SSD}) 	(ISL, {IP, ID})
	public static Role SYS_AUDTR = new Role(
			Role.CLevels.AM,
			EnumSet.of(Role.CCategories.SP, Role.CCategories.SD, Role.CCategories.SSD),
			Role.ILevels.ISL,
			EnumSet.of(Role.ICategories.IP, Role.ICategories.ID),
			"SYS_AUDTR");

	//System controllers 	(SL, {SP, SD}) 	(ISP, {IP, ID}) + downgrade
	public static Role SYS_CONTRLR = new Role(
			Role.CLevels.SL,
			EnumSet.of(Role.CCategories.SP, Role.CCategories.SD),
			Role.ILevels.ISP,
			EnumSet.of(Role.ICategories.IP, Role.ICategories.ID),
			"SYS_CONTRLR"); //TODO DOWNGRADE!
}

class Type extends Role //just a clone of Role, named Type for clarity
{
	//hash of all types
	public static HashMap<String, Type> types = new HashMap<String, Type>();
	
	public Type(CLevels cLevel, EnumSet<CCategories> cCats, ILevels iLevel, EnumSet<ICategories> iCats, String name)
	{
		super(cLevel, cCats, iLevel, iCats, name);
	}
	
	public static void populateHash()
	{
		if(types.size() == 0)
		{
			types.put(DEV_CODE.name, DEV_CODE);
			types.put(PROD_CODE.name, PROD_CODE);
			types.put(PROD_DATA.name, PROD_DATA);
			types.put(TOOLS.name, TOOLS);
			types.put(SYS_CODE.name, SYS_CODE);
			types.put(SYS_CODE_MOD.name, SYS_CODE_MOD);
			types.put(SYS_LOG.name, SYS_LOG);
		}
	}
	
	//-------------------------------- TYPES -------------------------------------//
		
	//Development code/test data 	(SL, {SD}) 	(ISL, {ID})
	public static Type DEV_CODE = new Type(
			Type.CLevels.SL,
			EnumSet.of(Type.CCategories.SD),
			Type.ILevels.ISL,
			EnumSet.of(Type.ICategories.ID),
			"DEV_CODE");
	
	//Production code 	(SL, {SP}) 	(IO, {IP})
	public static Type PROD_CODE = new Type(
			Type.CLevels.SL,
			EnumSet.of(Type.CCategories.SP),
			Type.ILevels.IO,
			EnumSet.of(Type.ICategories.IP),
			"PROD_CODE");
	
	//Production data 	(SL, {SP}) 	(ISL, {IP})
	public static Type PROD_DATA = new Type(
			Type.CLevels.SL,
			EnumSet.of(Type.CCategories.SP),
			Type.ILevels.ISL,
			EnumSet.of(Type.ICategories.IP),
			"PROD_DATA");
	
	//Software tools 	(SL, EMPTY) 	(IO, {ID})
	public static Type TOOLS = new Type(
			Type.CLevels.SL,
			EnumSet.noneOf(Type.CCategories.class),
			Type.ILevels.IO,
			EnumSet.of(Type.ICategories.ID),
			"TOOLS");
	
	//System programs 	(SL, EMPTY) 	(ISP, {IP, ID})
	public static Type SYS_CODE = new Type(
			Type.CLevels.SL,
			EnumSet.noneOf(Type.CCategories.class),
			Type.ILevels.ISP,
			EnumSet.of(Type.ICategories.IP, Type.ICategories.ID),
			"SYS_CODE");
	
	//System programs in modification 	(SL, {SSD} ) 	(ISL, {ID})
	public static Type SYS_CODE_MOD = new Type(
			Type.CLevels.SL,
			EnumSet.of(Type.CCategories.SSD),
			Type.ILevels.ISL,
			EnumSet.of(Type.ICategories.ID),
			"SYS_CODE_MOD");

	//production log (AM, {SP}) 	(ISL, EMPTY)
	public static Type SYS_LOG = new Type(Type.CLevels.AM, 
			EnumSet.of(Type.CCategories.SP, Type.CCategories.SD, Type.CCategories.SSD),
			Type.ILevels.ISL,
			EnumSet.noneOf(Type.ICategories.class),
			"SYS_LOG");
}


