package tests;

public class Tests {

//	@Test
//	public void playlistWriteTest() {
//		TreeMap<String, ArrayList<Song>> playlists = new TreeMap<String, ArrayList<Song>>();
//
//		File f = new File("playlists.txt");
//
//		try {
//			FileOutputStream fos = new FileOutputStream(f);
//			ObjectOutputStream oos = new ObjectOutputStream(fos);
//
//			oos.writeObject(playlists);
//
//			System.out.println("Data saved");
//			oos.close();
//
//		} catch (FileNotFoundException e) {
//			System.out.println("Saving failed");
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.out.println("Saving failed");
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void playlistsReadTest() {
//		File f = new File("playlists.txt");
//
//		try {
//			FileInputStream fis = new FileInputStream(f);
//			ObjectInputStream ois = new ObjectInputStream(fis);
//
//			@SuppressWarnings("unchecked")
//			TreeMap<String, ArrayList<Song>> playlists = (TreeMap<String, ArrayList<Song>>) ois.readObject();
//
//			System.out.println("Data read");
//			ois.close();
//
//		} catch (FileNotFoundException e) {
//			System.out.println("File reading failed");
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.out.println("File reading failed");
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//	}

//	@Test
//	public void profilesWriteTest() {
//		RPGProfile.writeProfilesToFile(new HashMap<String, RPGProfile>());
//	}

//	@Test
//	public void EnumTest() {
//		System.out.println(RPGItems.PICKLE_RICK_SHOWER_HEAD.name);
//		System.out.println(RPGItems.OBBY.name);
//		System.out.println(RPGItems.EMMYS.name);
//	}

}