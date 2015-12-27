package wang.lanchun.client;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ListModel;

import wang.lanchun.dao.UserDao;
import wang.lanchun.entity.Message;
import wang.lanchun.entity.User;
import wang.lanchun.view.ChatFrame;
import wang.lanchun.view.ChatFriendBoard;

/**
 * client���������
 * 
 * @author lanchun
 *
 */

public class ClientManager {
	private static ClientManager instance;
	public static Socket s;
	public static ObjectOutputStream writer;
	private static ObjectInputStream reader;
	private static UserDao userDao;
	public static User user;

	public static Map<Integer, List<Message>> unReadMsgMap = new HashMap<>();

	// �������д򿪵����
	public static Map<Integer, ChatFrame> frames = new HashMap<>();

	private ClientManager() {
		userDao = new UserDao();
		try {
			s = new Socket("localhost", 10086);
			writer = new ObjectOutputStream(s.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ����ģʽ
	public static ClientManager getInstance() {
		if (instance == null) {
			instance = new ClientManager();
		}
		return instance;
	}

	public void launchChatFrame(final int id) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatFrame frame = new ChatFrame(id);
					frame.setVisible(true);
					frames.put(id, frame);

					if (unReadMsgMap.get(id) != null) {
						// ��δ����Ϣlist�еĶ�Ӧ��δ����Ϣȡ��������ʾ��frame��
						for (Message m : unReadMsgMap.get(id)) {
							frame.appendFromRecord(m);
						}
						// ɾ��map���Ѿ��ó�������
						unReadMsgMap.remove(id);
						// �������
						updateBoard(id);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	// ��¼����Ϣ������
	public void loginToChatServer() {
		ExecutorService exec = null;

		// reader = new ObjectInputStream(s.getInputStream());

		userDao.switchOnline(user.getId(), 1);
		try {
			writer.writeObject(user);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		exec = Executors.newFixedThreadPool(2);
		exec.execute(new ClientReceiveThread(s, user));
		// exec.execute(new ClientSendThread(s,user,writer));
	}

	// �ں����б�����ʾδ����Ϣ��
	public static void updateBoard() {
		if (unReadMsgMap != null && unReadMsgMap.size() > 0) {
			for (Map.Entry<Integer, List<Message>> en : unReadMsgMap.entrySet()) {
				int id = en.getKey();
				for (int i = 0; i < ChatFriendBoard.model.size(); i++) {
					User user = ChatFriendBoard.model.getElementAt(i);
					if (user.getId() == id) {
						user.setNickname(user.getNickname() + "(" + en.getValue().size() + ")");
					}
				}
			}
		} else {
			System.out.println("unReadMsgMap Ϊ��");
		}
	}

	// ������壬˫����ɾ��δ����Ϣ��
	public static void updateBoard(int id) {
		for (int i = 0; i < ChatFriendBoard.model.size(); i++) {
			User user = ChatFriendBoard.model.getElementAt(i);
			if (user.getId() == id) {
				String nickname = user.getNickname();
				if (nickname.lastIndexOf("(") != -1) {
					user.setNickname(nickname.substring(0, nickname.lastIndexOf("(")));
				}
			}
		}
	}

	public static void updateBoard2(int id) {
		for (int i = 0; i < ChatFriendBoard.model.size(); i++) {
			User user = ChatFriendBoard.model.getElementAt(i);
			if (user.getId() == id) {
				String nickname = user.getNickname();
				String newName;
				// ��������������ˣ���ô��δ����Ϣ����1
				if (nickname.indexOf("(") != -1) {
					int n = Integer.parseInt(nickname.substring(nickname.indexOf("(") + 1, nickname.indexOf(")"))) + 1;
					newName = nickname.substring(0, nickname.indexOf("(") + 1) + n + ")";
				} else {
					newName = user.getNickname() + "(1)";
				}
				user.setNickname(newName);
			}
			// ChatFriendBoard.list.updateUI();
		}
	}

	// ����δ����Ϣ��map�У������յ���Ϣ�������촰��û�д�ʱ����
	public static void addMap(Message m) {
		int id = m.getSender().getId();
		List<Message> temp = null;
		temp = unReadMsgMap.get(id);
		if (temp == null) {
			temp = new ArrayList<>();
			unReadMsgMap.put(id, temp);
		}
		temp.add(m);
	}

}
