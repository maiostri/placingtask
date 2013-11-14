package project.rlsim;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stu



		String path = "C:/Users/CdCliente/Desktop/unicamp/projeto_dados/pruebas";

		String testDir = path + "/test_videos";

		String targetDir = path + "/result";

		int ks = 5;

		int rounds = 1;

		int imagesByRankedList = 5;

		RankedFiles r = new RankedFiles();

		try {
			r.RlSim(path,testDir, targetDir, ks, rounds, imagesByRankedList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		String aux = "C:/Users/CdCliente/Desktop/unicamp/projeto_dados/pruebas/result/p1_00005_list.txt";
//		File f = new File(aux);
//
//
//
//		try {
//			String a = "test";
//			Methods.writeString(a, f);
//
//			f.delete();
//
//			f = new File(aux);
//			Methods.writeString(a, f);
//
//			f.delete();
//
//
//
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


	}
}
