package poyrazinan.com.tr.tuccar.commands;

import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.commands.Main.MainCommands;

public class CommandRegister {
	
	public CommandRegister() {
		Tuccar.instance.getCommand("tüccar").setExecutor(new MainCommands(Tuccar.instance));
	}

}
