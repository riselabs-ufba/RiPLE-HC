package br.com.reconcavo.featurejs.util;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public final class ConsoleUtil {
	public static final MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (IConsole element : existing) {
			if (name.equals(element.getName())) {
				return (MessageConsole) element;
			}
		}

		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public static final void printMessage(MessageConsole messageConsole,
			String message) {
		messageConsole.activate();
		MessageConsoleStream out = messageConsole.newMessageStream();
		out.println(message);
	}
}
