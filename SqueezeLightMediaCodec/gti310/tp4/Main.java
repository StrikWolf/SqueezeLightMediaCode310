package gti310.tp4;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.SwingUtilities;

/**
 * The Main class is where the different functions are called to either encode a
 * PPM file to the Squeeze-Light format or to decode a Squeeze-Ligth image into
 * PPM format. It is the implementation of the simplified JPEG block diagrams.
 * 
 * @author Francois Caron
 * @author Antoine de Villers et Francis Cadorette
 */
public class Main {

	/*
	 * The entire application assumes that the blocks are 8x8 squares.
	 */
	public static final int BLOCK_SIZE = 8;

	/*
	 * The number of dimensions in the color spaces.
	 */
	public static final int COLOR_SPACE_SIZE = 3;

	/*
	 * The RGB color space.
	 */
	public static final int R = 0;
	public static final int G = 1;
	public static final int B = 2;

	/*
	 * The YUV color space.
	 */
	public static final int Y = 0;
	public static final int Cb = 1;
	public static final int Cr = 2;

	public static final Scanner sc = new Scanner(System.in);
	public static final PPMReaderWriter ppm = new PPMReaderWriter();
	public static final SZLReaderWriter szl = new SZLReaderWriter();

	/**
	 * The application's entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		boolean valid = false;
		while (!valid) {
			System.out.println("Squeeze Light Media Codec !\nEntrez C pour encoder l'image en format "
					+ "Squeeze Light,\nou D pour decoder l'image Squeeze Light");
			String reponse = sc.nextLine();
			if (reponse.toLowerCase().equals("c")) {
				System.out.println("Veuillez nommer le fichier ppm a encoder en format SZL");
				reponse = sc.nextLine();
				encodeSZL(reponse);
				valid = true;
			} else if (reponse.toLowerCase().equals("d")) {
				sc.close();
				decodeSZL();
				valid = true;
			} else {
				System.out.println("Vous n'avez pas rentrer une reponse valide, veuillez reessayez.");
			}
		}

	}

	public static void encodeSZL(String filename) {
		FileViewer f = new FileViewer(true);
		/*
		 * int[][][] image= ppm.readPPMFile(filename);
		 * System.out.println(filename); String[] parts = filename.split(".");
		 * String end = ".szl"; String newfilename=parts[0];
		 * System.out.println(newfilename);
		 * szl.writeSZLFile(newfilename,image[0].length,image[0][0].length,90);
		 */
		
		System.out.println("Avec quelle facteur de qualite souhaitez-vous charger l'image (0-100)");
		int reponse = Integer.parseInt(sc.nextLine());
		System.out.println(f.getFile().getName());
		List<int[][][]> blocs = new LinkedList<int[][][]>();
		blocs = Quantification.Do(
				DCTManager.DCT(BlocManager.split(Conversion.convertRGBToYCbCr(PPMReaderWriter.readPPMFile(f.getFile().toString())))),
				reponse);
		int[][][] AC = ZigzagChange.GetAC(blocs);
		int[][] DC = ZigzagChange.GetDC(blocs);

		// DC
		for (int i = 0; i < DC[0].length; i++)
			Entropy.writeDC(DC[0][i]);

		for (int i = 0; i < DC[1].length; i++)
			Entropy.writeDC(DC[1][i]);

		for (int i = 0; i < DC[2].length; i++)
			Entropy.writeDC(DC[2][i]);

		// AC
		for (int i = 0; i < AC[0].length; i++)
			Entropy.writeAC(AC[0][i][0], AC[0][i][1]);

		for (int i = 0; i < AC[1].length; i++)
			Entropy.writeAC(AC[1][i][0], AC[1][i][1]);

		for (int i = 0; i < AC[2].length; i++)
			Entropy.writeAC(AC[2][i][0], AC[2][i][1]);
		System.out.println("ok");
		SZLReaderWriter.writeSZLFile("output.szl", PPMReaderWriter.readPPMFile(f.getFile().toString())[0].length,
				PPMReaderWriter.readPPMFile(f.getFile().toString())[0][0].length, reponse);
	}

	public static void decodeSZL() {
		FileViewer f = new FileViewer(false);

	}
}
