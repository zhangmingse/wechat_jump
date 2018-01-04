package gamer;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class PictureProcess {

	Mat template_player, template_end, template_circle;

	public PictureProcess() throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		template_circle = Imgcodecs.imread(Main.basePath + "template\\template-circle-black.png");
		if (template_circle == null) {
			throw new Exception("tepmlet-circle.png not found");
		}
		template_player = Imgcodecs.imread(Main.basePath + "template\\template-player.png");
		if (template_player == null) {
			throw new Exception("tepmlet-player.png not found");
		}
		template_end = Imgcodecs.imread(Main.basePath + "template\\template-end.png");
		if (template_end == null) {
			throw new Exception("tepmlet-end.png not found");
		}
	}

	public double getDistance(String currentPath, String imageid) throws Exception {

		double playerx = 0, playery = 0;
		double destx = 0, desty = 0;
		Mat mat = null;
		mat = Imgcodecs.imread(currentPath + imageid + ".png");
		if (mat.empty()) {
			throw new Exception(currentPath + imageid + ".png " + " doesn't exist!");
		}

		MinMaxLocResult minMaxLocResult = null;
		Mat result = new Mat();
		// is game over?
		Imgproc.matchTemplate(mat, template_end, result, Imgproc.TM_CCOEFF_NORMED);
		minMaxLocResult = Core.minMaxLoc(result);
		if (minMaxLocResult.maxVal > 0.95) {// game over
			return -1;
		}

		// find player
		Imgproc.matchTemplate(mat, template_player, result, Imgproc.TM_CCOEFF_NORMED);

		minMaxLocResult = Core.minMaxLoc(result);
		System.out.println("player max match :" + minMaxLocResult.maxVal);
		if (minMaxLocResult.maxVal < 0.6) {
			System.out.println("can't find player");
			return -1;
		}
		Point matchloc_player = minMaxLocResult.maxLoc;

		playerx = matchloc_player.x + template_player.width() / 2;
		playery = matchloc_player.y + (template_player.height() * 0.9);

		// find dest
		mat = convert2Eedgeimg(mat, imageid);

		// Imgproc.TM_CCORR_NORMED
		Imgproc.matchTemplate(mat, template_circle, result, Imgproc.TM_CCOEFF_NORMED);

		boolean circlematch = false;
		minMaxLocResult = Core.minMaxLoc(result);
		Point matchloc_circle = null;
		if (minMaxLocResult.maxVal > 0.5) {
			System.out.println("find dest center by template match . circle max match :" + minMaxLocResult.maxVal);
			matchloc_circle = minMaxLocResult.maxLoc;
			destx = matchloc_circle.x + template_circle.width() / 2;
			desty = matchloc_circle.y + template_circle.height() / 2;

			circlematch = true;

			if (destx >= matchloc_player.x && destx <= matchloc_player.x + template_player.width()
					&& desty >= matchloc_player.y && desty <= matchloc_player.y + template_player.height()) {
				destx = 0;
				desty = 0;
				circlematch = false;
			}

		}
		if ((destx + desty) == 0) {
			System.out.println("find dest center by edge image");
			try {
				matchloc_circle = getCenterByEdge(mat, 0, 400,(int)(matchloc_player.x)-1,(int)(matchloc_player.x+template_player.width())+1);
				destx = matchloc_circle.x;
				desty = matchloc_circle.y + 40;

				matchloc_circle.x = destx - template_circle.width() / 2;
				matchloc_circle.y = desty - template_circle.height() / 2;

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		Imgproc.rectangle(mat, matchloc_player,
				new Point(matchloc_player.x + template_player.width(), matchloc_player.y + template_player.height()),
				new Scalar(0, 255, 0));

		Imgproc.rectangle(mat, matchloc_circle,
				new Point(matchloc_circle.x + template_circle.width(), matchloc_circle.y + template_circle.height()),
				new Scalar(0, 255, 0));
		if (circlematch) {
			int radius = template_circle.height()/2;
			Point center  = new Point(matchloc_circle.x + radius,matchloc_circle.y + radius);
			Imgproc.circle(mat, center, radius, new Scalar(0,255,0));
		}

		Imgproc.line(mat, new Point(playerx, playery), new Point(destx, desty), new Scalar(0, 255, 0));
		// Imgproc.line(mat, new Point(0, 400), new Point(400, 400), new Scalar(0, 255,
		// 0));

		Imgcodecs.imwrite(currentPath + imageid + "-match-dest.png", mat);

		double width = destx - playerx;
		double height = desty - playery;
		Double leng = Math.sqrt(width * width + height * height);
		return leng;

	}

	private Point getCenterByEdge(Mat mat, int startx, int starty,int excludex1,int excludex2) throws Exception {
		Point center = new Point();
		boolean find = false;
		int row;
		int column = 0;
		
		for (row = starty; row < mat.rows(); row++) {
			for (column = startx; column < mat.cols(); column++) {
				double[] item = mat.get(row, column);

				if(column >=excludex1 && column<=excludex2) {
					continue;
				}
				if ((item[0] + item[1] + item[2]) > 1) {
					find = true;
					break;
				}
			}
			if (find) {
				break;
			}
		}

		if (find) {
			center.x = column + 0.0;
			center.y = row + 0.0;
			return center;

		} else {
			throw new Exception("did not find any pixel which is not black");
		}

	}

	private Mat convert2Eedgeimg(Mat frame, String imageid) {

		double threshold = 10;
		Mat grayImage = new Mat();
		Mat detectedEdges = new Mat();
		// convert to gray scale
		Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
		// Imgcodecs.imwrite("C:\\Users\\think\\Desktop\\screencap\\" + imageid +
		// "-gray.png", grayImage);
		// reduce noise with 3*3 kernel
		Imgproc.GaussianBlur(grayImage, detectedEdges, new Size(3, 3), 0);
		// Imgcodecs.imwrite("C:\\Users\\think\\Desktop\\screencap\\" + imageid +
		// "-blur.png", detectedEdges);
		// canny detector , with ratio of lower : upper threshold of 3:1
		Imgproc.Canny(detectedEdges, detectedEdges, 2, 6);
		// Imgcodecs.imwrite("C:\\Users\\think\\Desktop\\screencap\\" + imageid +
		// "-edge.png", detectedEdges);
		Mat dest = new Mat();
		frame.copyTo(dest, detectedEdges);
		detectedEdges.reshape(0);

		return dest;

	}

}
