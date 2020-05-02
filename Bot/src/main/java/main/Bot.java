package main;

import java.util.HashSet;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot {

	public static String prefix = "$";

	public static void main(String[] args) {

		HashSet<GatewayIntent> gatewayIntents = new HashSet<GatewayIntent>();
		for (GatewayIntent c : GatewayIntent.values()) {
			gatewayIntents.add(c);
		}

		JDABuilder builder = JDABuilder.create("Njk2NzU4NjA1MjY5MDQxMjU0.XqR5iA.SPanCzX33nBc1Up-kaP8a9CiKbo",
				gatewayIntents);

		builder.setActivity(Activity.watching("Gamerboy80"));
		builder.addEventListeners(new TextCommands());
		builder.addEventListeners(new VoiceCommands());

		try {
			builder.build();
		} catch (LoginException e) {
			e.printStackTrace();
			System.out.println("Failed to build bot!");
		}
	}

}
