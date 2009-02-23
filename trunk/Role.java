import java.util.*;

public class Role
{
	protected CLevels cLevel;
	protected ILevels iLevel;
	protected EnumSet<CCategories> cCategories;
	protected EnumSet<ICategories> iCategories;
	
	public static enum CLevels {SL, AM}			//low to high
	public static enum ILevels {ISL, IO, ISP}	//low to high
	public static enum CCategories {SP, SD, SSD}
	public static enum ICategories {ID, IP}
	
	public Role(CLevels cLevel, EnumSet<CCategories> cCats, ILevels iLevel, EnumSet<ICategories> iCats)
	{
		this.cLevel = cLevel;
		this.iLevel = iLevel;
		this.cCategories = cCats;
		this.iCategories = iCats;
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
}

class Type extends Role //just a clone of Role, named Type for clarity
{
	public Type(CLevels cLevel, EnumSet<CCategories> cCats, ILevels iLevel, EnumSet<ICategories> iCats)
	{
		super(cLevel, cCats, iLevel, iCats);
	}
}

