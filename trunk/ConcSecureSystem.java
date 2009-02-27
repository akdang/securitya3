import java.util.*;


public class ConcSecureSystem 
{
	public static void main(String[] args)
	{
//	   CREATE_SUBJECT subject_name subject_role
//	   CREATE_OBJECT subject_name object_name object_type
//	   READ subject_name object_name 
//	   WRITE subject_name object_name value 
//	   DOWNGRADE subject_name object_name object_type
		
		Firewall wall = new Firewall();
		
		ArrayList instrArgs = new ArrayList();
		instrArgs.add("SystemLog");
		Instruction aud = new Instruction(Instruction.Commands.AUDIT, instrArgs);
		wall.executeInstruction(aud);
		instrArgs.clear();
		
		
		instrArgs = new ArrayList(Arrays.asList(new Object[]{"sysCntrler", Role.SYS_CONTRLR}));
		aud = new Instruction(Instruction.Commands.CREATE_SUBJECT, instrArgs);
		wall.executeInstruction(aud);
		
		instrArgs = new ArrayList(Arrays.asList(new Object[]{"appDever", Role.APP_DEV}));
		aud = new Instruction(Instruction.Commands.CREATE_SUBJECT, instrArgs);
		wall.executeInstruction(aud);
		
		instrArgs = new ArrayList(Arrays.asList(new Object[]{"appDever", "devCode", Type.DEV_CODE}));
		aud = new Instruction(Instruction.Commands.CREATE_OBJECT, instrArgs);
		wall.executeInstruction(aud);
		
		
		instrArgs = new ArrayList(Arrays.asList(new Object[]{"sysCntrler", "devCode", Type.PROD_DATA}));
		aud = new Instruction(Instruction.Commands.DOWNGRADE, instrArgs);
		wall.executeInstruction(aud);

		instrArgs = new ArrayList(Arrays.asList(new Object[]{"sysCntrler", "devCode", Type.PROD_CODE}));
		aud = new Instruction(Instruction.Commands.DOWNGRADE, instrArgs);
		wall.executeInstruction(aud);
		
		
		/*
		instrArgs.remove(0);
		instrArgs.add(0, "ImaUser");
		
		instrArgs.add(Role.ORDINARY);
		aud = new Instruction(Instruction.Commands.CREATE_SUBJECT, instrArgs);
		wall.executeInstruction(aud);
		instrArgs.clear();
		

		instrArgs.add("ImaUser");
		instrArgs.add("fooObj");
		instrArgs.add(Type.PROD_DATA);
		aud = new Instruction(Instruction.Commands.CREATE_OBJECT, instrArgs);
		wall.executeInstruction(aud);
		
		instrArgs.remove(1);
		instrArgs.add(1, "SystemLog");
		aud = new Instruction(Instruction.Commands.CREATE_OBJECT, instrArgs);
		wall.executeInstruction(aud);
		
		instrArgs.remove(2);
//		aud = new Instruction(Instruction.Commands.READ, instrArgs);
//		wall.executeInstruction(aud);

		instrArgs.add("value stuff");
		aud = new Instruction(Instruction.Commands.WRITE, instrArgs);
		wall.executeInstruction(aud);
		*/
		
		
		
		wall.printAudit();
	}
}

