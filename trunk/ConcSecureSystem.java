import java.util.*;
import java.io.*;

public class ConcSecureSystem 
{
	public static void main(String[] args)
	{
		ArrayList<Instruction> instructions = readInput();
		Firewall fw = new Firewall();
		if(instructions.get(0).command != Instruction.Commands.AUDIT)
		{
			System.out.println("Must give audit as first command... exiting.");
			System.exit(0);
		}
		
		for(Instruction i: instructions)
			fw.attempt(i);
		
		fw.printAudit();
	}
	
	public static ArrayList<Instruction> readInput()
	{
		//populating hashes containing static roles and types
		Role.populateHash();
		Type.populateHash();
		
		ArrayList<Instruction> result = new ArrayList<Instruction>();
		Scanner input = null;
		try 
		{
			input = new Scanner(new File("Instructions"));
		}
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
			System.exit(0);
		}
		
		while(input.hasNext())
		{
			String line = input.nextLine().toLowerCase();
			if (line.equals(""))
				continue;
			StringTokenizer tokenizer = new StringTokenizer(line);
			
			ArrayList<String> tokens = new ArrayList<String>();
			while(tokenizer.hasMoreTokens())
				tokens.add(tokenizer.nextToken());
			
			Instruction.Commands command = Instruction.Commands.BAD_INSTRUCTION;
			ArrayList<Object> instrArgs = new ArrayList<Object>();
		
			try
			{
				if(tokens.get(0).equals("audit") && tokens.size() == 2)
				{
					command = command.AUDIT;
					instrArgs.add(tokens.get(1));
				}
				else if(tokens.get(0).equals("create_subject") && tokens.size() == 3)
				{
					command = command.CREATE_SUBJECT;
					
					instrArgs.add(tokens.get(1)); 
					Role role = Role.roles.get(tokens.get(2).toUpperCase());
					if(role == null) //invalid role
						throw new Exception();
					instrArgs.add(role);
				}
				else if(tokens.get(0).equals("create_object") && tokens.size() == 4)
				{
					command = command.CREATE_OBJECT;
					instrArgs.add(tokens.get(1));
					instrArgs.add(tokens.get(2));
					Type type = Type.types.get(tokens.get(3).toUpperCase());
					if(type == null) //invalid type
						throw new Exception();
					instrArgs.add(type);
				}
				else if(tokens.get(0).equals("read") && tokens.size() == 3)
				{
					command = command.READ;
					instrArgs.add(tokens.get(1));
					instrArgs.add(tokens.get(2));
				}
				else if(tokens.get(0).equals("write") && tokens.size() == 4)
				{
					command = command.WRITE;
					instrArgs.add(tokens.get(1));
					instrArgs.add(tokens.get(2));
					instrArgs.add(tokens.get(3));
				}
				else if(tokens.get(0).equals("downgrade") && tokens.size() == 4)
				{
					command = command.DOWNGRADE;
					instrArgs.add(tokens.get(1));
					instrArgs.add(tokens.get(2));
					Type type = Type.types.get(tokens.get(3).toUpperCase());
					if(type == null) //invalid type
						throw new Exception();
					instrArgs.add(type);
				}
				else //bad command
				{
					throw new Exception();
				}
			}
			catch(Exception e)
			{
				command = command.BAD_INSTRUCTION;
			}
			result.add(new Instruction(command, instrArgs));
		}
		return result;
	}
}


class UnitTests
{
	public static void main(String[] args)
	{
		System.out.print("Testing... ");
		testPerms();
		testDowngrade();
		
		System.out.println("done.");
	}
	
	public static void testDowngrade()
	{
		Firewall wall = new Firewall();
		
		ArrayList instrArgs = new ArrayList();
		instrArgs.add("SystemLog");
		Instruction aud = new Instruction(Instruction.Commands.AUDIT, instrArgs);
		wall.attempt(aud);
		instrArgs.clear();
		
		
		instrArgs = new ArrayList(Arrays.asList(new Object[]{"sysCntrler", Role.SYS_CONTRLR}));
		aud = new Instruction(Instruction.Commands.CREATE_SUBJECT, instrArgs);
		wall.attempt(aud);
		
		instrArgs = new ArrayList(Arrays.asList(new Object[]{"appDever", Role.APP_DEV}));
		aud = new Instruction(Instruction.Commands.CREATE_SUBJECT, instrArgs);
		wall.attempt(aud);
		
		instrArgs = new ArrayList(Arrays.asList(new Object[]{"appDever", "devCode", Type.DEV_CODE}));
		aud = new Instruction(Instruction.Commands.CREATE_OBJECT, instrArgs);
		wall.attempt(aud);
		
		
		instrArgs = new ArrayList(Arrays.asList(new Object[]{"sysCntrler", "devCode", Type.PROD_DATA}));
		aud = new Instruction(Instruction.Commands.DOWNGRADE, instrArgs);
		wall.attempt(aud);

		instrArgs = new ArrayList(Arrays.asList(new Object[]{"sysCntrler", "devCode", Type.PROD_CODE}));
		aud = new Instruction(Instruction.Commands.DOWNGRADE, instrArgs);
		wall.attempt(aud);
		
		wall.attempt(aud);
		String expected = "AUDIT SystemLog SYS_LOG\nSuccessful.\n\nCREATE_SUBJECT sysCntrler SYS_CONTRLR\nSuccessful.\n\nCREATE_SUBJECT appDever APP_DEV\nSuccessful.\n\nCREATE_OBJECT appDever devCode DEV_CODE\nSuccessful.\n\nDOWNGRADE sysCntrler devCode PROD_DATA\nFailure. Cannot downgrade from DEV_CODE to PROD_DATA\n\nDOWNGRADE sysCntrler devCode PROD_CODE\nSuccessful.\n\nDOWNGRADE sysCntrler devCode PROD_CODE\nFailure. Cannot downgrade from PROD_CODE to PROD_CODE\n\n";
		String result = wall.getAudit();
		assert (expected.equals(result));
	}
	
	public static void testPerms()
	{
		assert(Role.ORDINARY.cantBoth(Type.DEV_CODE));
		assert(Role.ORDINARY.canOnlyRead(Type.PROD_CODE));
		assert(Role.ORDINARY.canBoth(Type.PROD_DATA));
		assert(Role.ORDINARY.cantBoth(Type.TOOLS));
		assert(Role.ORDINARY.canOnlyRead(Type.SYS_CODE));
		assert(Role.ORDINARY.cantBoth(Type.SYS_CODE_MOD));
		assert(Role.ORDINARY.canOnlyWrite(Type.SYS_LOG));
		
		assert(Role.APP_DEV.canBoth(Type.DEV_CODE));
		assert(Role.APP_DEV.cantBoth(Type.PROD_CODE));
		assert(Role.APP_DEV.cantBoth(Type.PROD_DATA));
		assert(Role.APP_DEV.canOnlyRead(Type.TOOLS));
		assert(Role.APP_DEV.canOnlyRead(Type.SYS_CODE));
		assert(Role.APP_DEV.cantBoth(Type.SYS_CODE_MOD));
		assert(Role.APP_DEV.canOnlyWrite(Type.SYS_LOG));
		
		assert(Role.SYS_PROGMR.cantBoth(Type.DEV_CODE));
		assert(Role.SYS_PROGMR.cantBoth(Type.PROD_CODE));
		assert(Role.SYS_PROGMR.cantBoth(Type.PROD_DATA));
		assert(Role.SYS_PROGMR.canOnlyRead(Type.TOOLS));
		assert(Role.SYS_PROGMR.canOnlyRead(Type.SYS_CODE));
		assert(Role.SYS_PROGMR.canBoth(Type.SYS_CODE_MOD));
		assert(Role.SYS_PROGMR.canOnlyWrite(Type.SYS_LOG));
		
		assert(Role.SYS_AUDTR.cantBoth(Type.DEV_CODE));
		assert(Role.SYS_AUDTR.cantBoth(Type.PROD_CODE));
		assert(Role.SYS_AUDTR.cantBoth(Type.PROD_DATA));
		assert(Role.SYS_AUDTR.cantBoth(Type.TOOLS));
		assert(Role.SYS_AUDTR.canOnlyRead(Type.SYS_CODE));
		assert(Role.SYS_AUDTR.cantBoth(Type.SYS_CODE_MOD));
		assert(Role.SYS_AUDTR.canOnlyWrite(Type.SYS_LOG));
		
		assert(Role.SYS_CONTRLR.cantBoth(Type.DEV_CODE));
		assert(Role.SYS_CONTRLR.cantBoth(Type.PROD_CODE));
		assert(Role.SYS_CONTRLR.cantBoth(Type.PROD_DATA));
		assert(Role.SYS_CONTRLR.cantBoth(Type.TOOLS));
		assert(Role.SYS_CONTRLR.canOnlyRead(Type.SYS_CODE));
		assert(Role.SYS_CONTRLR.cantBoth(Type.SYS_CODE_MOD));
		assert(Role.SYS_CONTRLR.canOnlyWrite(Type.SYS_LOG));
	}
}
