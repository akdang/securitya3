import java.util.EnumSet;
import java.util.HashMap;


public class Firewall 
{
	private HashMap<String, Role> subLvls;
	private HashMap<String, Type> objLvls;
	private Core myCore;
	private SecureObject audit;
	
	public Firewall()
	{
		subLvls = new HashMap<String, Role>();
		objLvls = new HashMap<String, Type>();
		myCore = new Core();
		audit = new SecureObject();
	}

	public void printAudit()
	{
		System.out.println(audit.getData());
	}
	
	public String getAudit()
	{
		return audit.getData();
	}
	
	public void attempt(Instruction instr)
	{
		Instruction.Commands command = instr.command;
		String auditString = instr + "\n"; //describes current instruction
		
		if(command.equals(Instruction.Commands.AUDIT)) 
		{
			if(!myCore.coreObjects.containsValue(audit))
			{
				myCore.execute(instr);
				auditString += "Successful.";
			}
			else
				auditString += "Failure. Audit object already exists.";
		}
		
		else if(command.equals(Instruction.Commands.CREATE_SUBJECT))
		{
			if(!subLvls.containsKey(instr.subject_name)) //subject doesn't already exist
			{
				myCore.execute(instr);
				auditString += "Successful.";
			}
			else auditString += "Failure. Subject " + instr.subject_name + " already exists.";
		}
		
		else if(command.equals(Instruction.Commands.CREATE_OBJECT))
		{
			if(!objLvls.containsKey(instr.object_name)) //object doesn't already exist
			{
				if(subLvls.get(instr.subject_name).canWrite(instr.object_type)) //has permission
				{
					myCore.execute(instr);
					auditString += "Successful.";
				}
				else auditString += "Failure. Subject " + instr.subject_name + " does not have write access.";
			}
			else auditString += "Failure. Object " + instr.object_name + " already exists.";
		}
		
		else if(command.equals(Instruction.Commands.READ))
		{
			if(subLvls.containsKey(instr.subject_name)) //subject exists
			{
				if(objLvls.containsKey(instr.object_name)) //object exists 	
				{
					if(subLvls.get(instr.subject_name).canRead(objLvls.get(instr.object_name))) //permission
					{
						myCore.execute(instr);
						auditString += "Successful.";
					}
					else auditString += "Failure. Subject " + instr.subject_name + " does not have read access.";
				}
				else auditString += "Failure. Object " + instr.object_name + " does not exist.";
			}
			else auditString += "Failure. Subject " + instr.subject_name + " does not exist.";
		}
		
		else if(command.equals(Instruction.Commands.WRITE))
		{
			if(subLvls.containsKey(instr.subject_name)) //subject exists
			{
				if(objLvls.containsKey(instr.object_name)) //object exists 	
				{
					if(subLvls.get(instr.subject_name).canWrite(objLvls.get(instr.object_name))) //has permission
					{
						myCore.execute(instr);
						auditString += "Successful.";
					}
					else auditString += "Failure. Subject " + instr.subject_name + " does not have write access.";
				}
				else auditString += "Failure. Object " + instr.object_name + " does not exist.";
			}
			else auditString += "Failure. Subject " + instr.subject_name + " does not exist.";
		}
		
		else if(command.equals(Instruction.Commands.DOWNGRADE))
		{
			if(subLvls.containsKey(instr.subject_name)) //subject exists
			{
				if(objLvls.containsKey(instr.object_name)) //object exists 	
				{
					if(subLvls.get(instr.subject_name) == Role.SYS_CONTRLR) //proper role
					{
						Type currentLvl = objLvls.get(instr.object_name);
						Type newLvl = instr.object_type;
						
						if(	//2 valid downgrades
						(currentLvl == Type.DEV_CODE && newLvl == Type.PROD_CODE) 
						||
						(currentLvl == Type.SYS_CODE_MOD && newLvl == Type.SYS_CODE))
						{	
							myCore.execute(instr);
							auditString += "Successful.";
						}
						else auditString += "Failure. Cannot downgrade from " + currentLvl + " to " + newLvl;
					}
					else auditString += "Failure. Subject " + instr.subject_name + " is not a System Controller.";
				}
				else auditString += "Failure. Object " + instr.object_name + " does not exist.";
			}
			else auditString += "Failure. Subject " + instr.subject_name + " does not exist.";
		}
		else //bad instr
			auditString = "Failure, BAD INSTRUCTION.";
		
		//update audit
		audit.appendToData(auditString);
	}

	private class Core
	{
		//where objects reside
		private HashMap<String, SecureObject> coreObjects;
		
		public Core() 
		{
			coreObjects = new HashMap<String, SecureObject>();
		}
		
		/**
		 * Blindly executes command passed in, relying
		 * on Firewall to have checked the permissions.
		 * @param instr instruction to execute
		 */
		public void execute(Instruction instr)
		{
			Instruction.Commands command = instr.command;
			
			if(command.equals(Instruction.Commands.AUDIT)) 
			{
				myCore.coreObjects.put(instr.object_name, audit);
				objLvls.put(instr.object_name, instr.object_type);
			}
			else if(command.equals(Instruction.Commands.CREATE_SUBJECT))
			{
				subLvls.put(instr.subject_name, instr.subject_role);
			}
			else if(command.equals(Instruction.Commands.CREATE_OBJECT))
			{
				objLvls.put(instr.object_name, instr.object_type);
				myCore.coreObjects.put(instr.object_name, new SecureObject());
			}
			else if(command.equals(Instruction.Commands.READ))
			{
				//nop
			}	
			else if(command.equals(Instruction.Commands.WRITE))
			{
				//nop
			}
			else if(command.equals(Instruction.Commands.DOWNGRADE))
			{
				Type newLvl = instr.object_type;
				objLvls.put(instr.object_name, newLvl);
			}
		}
	}

	private class SecureObject
	{
		private String data;
		
		public SecureObject()
		{
			data = "";
		}
		
		//special method useful for updating audit log
		public void appendToData(String newData)
		{
			data += (newData + "\n\n"); //extra newlines for spacing
		}
		
		public String getData()
		{
			return data;
		}
	}
}