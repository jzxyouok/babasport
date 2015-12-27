package wang.lanchun.client;

import java.awt.EventQueue;

import wang.lanchun.view.LoginFrame;

/**
 * �ͻ������
 * ������¼����
 * @author lanchun
 *
 */
public class Client {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
