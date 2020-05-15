package RPG;

import java.util.HashMap;

public class ProfileSaver implements Runnable {

	private HashMap<String, RPGProfile> profiles;

	public ProfileSaver(HashMap<String, RPGProfile> profiles) {
		this.profiles = profiles;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(6 * 60 * 1000); // 6 minutes
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			RPGProfile.writeProfilesToFile(profiles);
			System.out.println("Profiles saved via profiler thread");
		}

	}

}
