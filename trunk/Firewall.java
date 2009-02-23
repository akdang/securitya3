import java.util.EnumSet;


public class Firewall 
{

	

	//-------------------------------- ROLES -------------------------------------//
	//Ordinary Users 	(SL, {SP}) 	(ISL, {IP})
	private static Role ORDINARY = new Role(
			Role.CLevels.SL,
			EnumSet.of(Role.CCategories.SP),
			Role.ILevels.ISL,
			EnumSet.of(Role.ICategories.IP));
	
	//Application developers 	(SL, {SD}) 	(ISL, {ID})
	private static Role APP_DEV = new Role(
			Role.CLevels.SL,
			EnumSet.of(Role.CCategories.SD),
			Role.ILevels.ISL,
			EnumSet.of(Role.ICategories.ID));
	
	//System programmers 	(SL, {SSD}) 	(ISL, {ID})
	private static Role SYS_PROGMR = new Role(
			Role.CLevels.SL,
			EnumSet.of(Role.CCategories.SSD),
			Role.ILevels.ISL,
			EnumSet.of(Role.ICategories.ID));

	//System managers/auditors 	(AM, {SP, SD, SSD}) 	(ISL, {IP, ID})
	private static Role SYS_AUDTR = new Role(
			Role.CLevels.AM,
			EnumSet.of(Role.CCategories.SP, Role.CCategories.SD, Role.CCategories.SSD),
			Role.ILevels.ISL,
			EnumSet.of(Role.ICategories.IP, Role.ICategories.ID));

	//System controllers 	(SL, {SP, SD}) 	(ISP, {IP, ID}) + downgrade
	private static Role SYS_CONTRLR = new Role(
			Role.CLevels.SL,
			EnumSet.of(Role.CCategories.SP, Role.CCategories.SD),
			Role.ILevels.ISP,
			EnumSet.of(Role.ICategories.IP, Role.ICategories.ID)); //TODO DOWNGRADE!
	
	
	//-------------------------------- TYPES -------------------------------------//
	//Development code/test data 	(SL, {SD}) 	(ISL, {ID})
	private static Type DEV_CODE = new Type(
			Type.CLevels.SL,
			EnumSet.of(Type.CCategories.SD),
			Type.ILevels.ISL,
			EnumSet.of(Type.ICategories.ID));
	
	//Production code 	(SL, {SP}) 	(IO, {IP})
	private static Type PROD_CODE = new Type(
			Type.CLevels.SL,
			EnumSet.of(Type.CCategories.SP),
			Type.ILevels.IO,
			EnumSet.of(Type.ICategories.IP));
	
	//Production data 	(SL, {SP}) 	(ISL, {IP})
	private static Type PROD_DATA = new Type(
			Type.CLevels.SL,
			EnumSet.of(Type.CCategories.SP),
			Type.ILevels.ISL,
			EnumSet.of(Type.ICategories.IP));
	
	//Software tools 	(SL, EMPTY) 	(IO, {ID})
	private static Type TOOLS = new Type(
			Type.CLevels.SL,
			EnumSet.noneOf(Type.CCategories.class),
			Type.ILevels.IO,
			EnumSet.of(Type.ICategories.ID));
	
	//System programs 	(SL, EMPTY) 	(ISP, {IP, ID})
	private static Type SYS_CODE = new Type(
			Type.CLevels.SL,
			EnumSet.noneOf(Type.CCategories.class),
			Type.ILevels.ISP,
			EnumSet.of(Type.ICategories.IP, Type.ICategories.ID));
	
	//System programs in modification 	(SL, {SSD} ) 	(ISL, {ID})
	private static Type SYS_CODE_MOD = new Type(
			Type.CLevels.SL,
			EnumSet.of(Type.CCategories.SSD),
			Type.ILevels.ISL,
			EnumSet.of(Type.ICategories.ID));

	//production log (AM, {SP}) 	(ISL, EMPTY)
	private static Type SYS_LOG_SP = new Type(Type.CLevels.AM, 
			EnumSet.of(Type.CCategories.SP),
			Type.ILevels.ISL,
			EnumSet.noneOf(Type.ICategories.class));
	
	//developer log (AM, {SD}) 	(ISL, EMPTY)
	private static Type SYS_LOG_SD = new Type(Type.CLevels.AM, 
			EnumSet.of(Type.CCategories.SD),
			Type.ILevels.ISL,
			EnumSet.noneOf(Type.ICategories.class));
	
	//system developer log (AM, {SSD}) 	(ISL, EMPTY)
	private static Type SYS_LOG_SSD = new Type(Type.CLevels.AM, 
			EnumSet.of(Type.CCategories.SSD),
			Type.ILevels.ISL,
			EnumSet.noneOf(Type.ICategories.class));
	
	
	public static class UnitTests
	{
		public static void main(String[] args)
		{
			System.out.print("Testing... ");
			//assert(false);
			//TODO downgrade special case
			
			assert(ORDINARY.cantBoth(DEV_CODE));
			assert(ORDINARY.canOnlyRead(PROD_CODE));
			assert(ORDINARY.canBoth(PROD_DATA));
			assert(ORDINARY.cantBoth(TOOLS));
			assert(ORDINARY.canOnlyRead(SYS_CODE));
			assert(ORDINARY.cantBoth(SYS_CODE_MOD));
			assert(ORDINARY.canOnlyWrite(SYS_LOG_SP));
			
			assert(APP_DEV.canBoth(DEV_CODE));
			assert(APP_DEV.cantBoth(PROD_CODE));
			assert(APP_DEV.cantBoth(PROD_DATA));
			assert(APP_DEV.canOnlyRead(TOOLS));
			assert(APP_DEV.canOnlyRead(SYS_CODE));
			assert(APP_DEV.cantBoth(SYS_CODE_MOD));
			assert(APP_DEV.canOnlyWrite(SYS_LOG_SD));
			
			assert(SYS_PROGMR.cantBoth(DEV_CODE));
			assert(SYS_PROGMR.cantBoth(PROD_CODE));
			assert(SYS_PROGMR.cantBoth(PROD_DATA));
			assert(SYS_PROGMR.canOnlyRead(TOOLS));
			assert(SYS_PROGMR.canOnlyRead(SYS_CODE));
			assert(SYS_PROGMR.canBoth(SYS_CODE_MOD));
			assert(SYS_PROGMR.canOnlyWrite(SYS_LOG_SSD));
			
			assert(SYS_AUDTR.cantBoth(DEV_CODE));
			assert(SYS_AUDTR.cantBoth(PROD_CODE));
			assert(SYS_AUDTR.cantBoth(PROD_DATA));
			assert(SYS_AUDTR.cantBoth(TOOLS));
			assert(SYS_AUDTR.canOnlyRead(SYS_CODE));
			assert(SYS_AUDTR.cantBoth(SYS_CODE_MOD));
			assert(SYS_AUDTR.cantBoth(SYS_LOG_SP));
			assert(SYS_AUDTR.cantBoth(SYS_LOG_SD));
			assert(SYS_AUDTR.cantBoth(SYS_LOG_SSD));
			
			assert(SYS_CONTRLR.cantBoth(DEV_CODE));
			assert(SYS_CONTRLR.cantBoth(PROD_CODE));
			assert(SYS_CONTRLR.cantBoth(PROD_DATA));
			assert(SYS_CONTRLR.cantBoth(TOOLS));
			assert(SYS_CONTRLR.canOnlyRead(SYS_CODE));
			assert(SYS_CONTRLR.cantBoth(SYS_CODE_MOD));
			assert(SYS_CONTRLR.cantBoth(SYS_LOG_SP));
			assert(SYS_CONTRLR.cantBoth(SYS_LOG_SD));
			assert(SYS_CONTRLR.cantBoth(SYS_LOG_SSD));
			
			System.out.println("done.");
		}
	}
}