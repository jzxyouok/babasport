package wang.lanchun.client;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wang.lanchun.dao.MessageDao;
import wang.lanchun.entity.Message;
import wang.lanchun.entity.User;
import wang.lanchun.view.ChatFrame;

/**
 * �ͻ��˽�����Ϣ�߳���
 * @author lanchun
 *
 */
public class ClientReceiveThread implements Runnable {

	private ObjectInputStream reader;
	private Socket s;
	private MessageDao messageDao = new MessageDao();

	public ClientReceiveThread(Socket s, User user) {
		this.s = s;
		System.out.println("ClientReceiveThread start!");
	}

	@Override
	public void run() {
		/**
		 * ���ȴ����ݿ��л�ȡ������Ϣ������ClientManager�е�list
		 */
		List<Message> list = messageDao.loadMsg(ClientManager.user.getId());
		if(list != null && list.size() > 0){
			System.out.println("got unread msg!");
			Map<Integer,List<Message>> map = new HashMap<>();
			for(Message m : list){
				ClientManager.addMap(m);
			}
			
		}else{
			System.out.println("no unread msg");
		}
		
		try {
			reader = new ObjectInputStream(s.getInputStream());
			while (true) {
				Message msg = (Message) reader.readObject();
				System.out.println(msg.getSender().getId() + " : " + msg.getContent() + " " + msg.getTime());
				ChatFrame frame = ClientManager.frames.get(msg.getSender().getId());
				if(frame != null){
					frame.appendComingMessage(msg);
				}else{
					System.out.println("û�д����촰��");
					//���clientManager�е�map��
					ClientManager.addMap(msg);
					//����board
					ClientManager.updateBoard2(msg.getSender().getId());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
