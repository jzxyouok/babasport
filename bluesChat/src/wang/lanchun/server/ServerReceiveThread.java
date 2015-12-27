package wang.lanchun.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import wang.lanchun.dao.MessageDao;
import wang.lanchun.entity.Message;

/**
 * �ͻ������߳�
 * 
 * @author lanchun
 *
 */
public class ServerReceiveThread implements Runnable {

	private ObjectInputStream reader;
	private Socket s;
	private MessageDao messageDao;

	public ServerReceiveThread(Socket s, ObjectInputStream reader) {
		this.s = s;
		this.reader = reader;
		this.messageDao = new MessageDao();
		System.out.println("ServerReceiveThread started!");
	}

	@Override
	public void run() {
		try {
			while (true) {
				// �����socket��������
				if (s == null) {
					System.out.println("socket is null");
					throw new RuntimeException("socket�쳣");
				}
				if (s.isClosed()) {
					System.out.println("socket is closed");
					throw new RuntimeException("socket�쳣");
				}
				Message msg = (Message) reader.readObject();
				if (msg != null) {
					int id = msg.getReceiver().getId();
					// ������ߣ�����ת����Ϣ
					System.out.println("got message!" + msg.getContent() + "," + msg.getReceiver().isOnline());
					if (msg.getReceiver().isOnline()) {

						// ȡ�ý����ߵ�socket
						Socket receiverSo = Server.clients.get(msg.getReceiver().getId());

						// ������ȥ���������е��������map��ȥ�ң��������ֱ��ȡ������û���򴴽������浽map��
						ObjectOutputStream writer = Server.writers.get(receiverSo);
						if (writer == null) {
							writer = new ObjectOutputStream(receiverSo.getOutputStream());
							Server.writers.put(receiverSo, writer);
						}

						writer.writeObject(msg);
					} else {
						// ����������Ϣ���ݿ�
						msg.setRead(false);
						messageDao.add(msg);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
