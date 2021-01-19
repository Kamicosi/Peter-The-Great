package main;

import java.util.HashMap;
import java.util.HashSet;

import javax.security.auth.login.LoginException;

import RPG.RPGProfile;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot {

	public static final String prefix = "$";
	public static final int COLOR = 0x005420;

	public static void main(String[] args) {

		HashSet<GatewayIntent> gatewayIntents = new HashSet<GatewayIntent>();
		for (GatewayIntent c : GatewayIntent.values()) {
			gatewayIntents.add(c);
		}

		JDABuilder builder = JDABuilder.create("Insert-token-here",
				gatewayIntents);

		builder.setActivity(Activity.playing("Peter RPG"));

		HashMap<String, RPGProfile> profiles = RPGProfile.readProfilesFromFile();

		builder.addEventListeners(new TextCommands(profiles));
		builder.addEventListeners(new VoiceCommands(profiles));

		try {
			builder.build();
		} catch (LoginException e) {
			e.printStackTrace();
			System.out.println("Failed to build bot!");
		}
	}

}
