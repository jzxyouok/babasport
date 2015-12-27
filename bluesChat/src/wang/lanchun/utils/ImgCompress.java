package wang.lanchun.utils;
import java.io.*;
import java.util.Date;
import java.util.UUID;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import com.sun.image.codec.jpeg.*;
/**
 * ͼƬѹ������
 * @author ����ǿ
 */
public class ImgCompress {
	private Image img;
	private int width;
	private int height;
	public static final int WIDTH = 200;
	public static final int HEIGHT = 200;
	
	public static void main(String[] args){
		ImgCompress imgCom = new ImgCompress("D:\\temp\\1.jpg");
		imgCom.resizeFix(WIDTH, HEIGHT);
	}
	
	/**
	 * ���캯��
	 */
	public ImgCompress(String fileName){
		System.out.println(fileName);
		File file = new File(fileName);// �����ļ�
		System.out.println("sdf"+file.getAbsolutePath());
		try {
			img = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}      
		// ����Image����
		width = img.getWidth(null);    // �õ�Դͼ��
		height = img.getHeight(null);  // �õ�Դͼ��
	}
	/**
	 * ���տ�Ȼ��Ǹ߶Ƚ���ѹ��
	 * @param w int �����
	 * @param h int ���߶�
	 */
	public String resizeFix(int w, int h) {
		String descFileName;
		if (width / height > w / h) {
			descFileName = resizeByWidth(w);
		} else {
			descFileName = resizeByHeight(h);
		}
		return descFileName;
	}
	/**
	 * �Կ��Ϊ��׼���ȱ�������ͼƬ
	 * @param w int �¿��
	 */
	public String resizeByWidth(int w) {
		int h = (int) (height * w / width);
		return resize(w, h);
	}
	/**
	 * �Ը߶�Ϊ��׼���ȱ�������ͼƬ
	 * @param h int �¸߶�
	 */
	public String resizeByHeight(int h) {
		int w = (int) (width * h / height);
		return resize(w, h);
	}
	/**
	 * ǿ��ѹ��/�Ŵ�ͼƬ���̶��Ĵ�С
	 * @param w int �¿��
	 * @param h int �¸߶�
	 */
	public String resize(int w, int h){
		// SCALE_SMOOTH �������㷨 ��������ͼƬ��ƽ���ȵ� ���ȼ����ٶȸ� ���ɵ�ͼƬ�����ȽϺ� ���ٶ���
		BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB ); 
		image.getGraphics().drawImage(img, 0, 0, w, h, null); // ������С���ͼ
		File destFile = new File("D:\\temp\\"+UUID.randomUUID()+".jpg");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(destFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		// ������ļ���
		// ��������ʵ��bmp��png��gifתjpg
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		try {
			encoder.encode(image);
			out.close();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		// JPEG����
		return destFile.getAbsolutePath();
	}
}