package channel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;


/** This class acts like a interprocess communication channel for Object passing. */
public class Channel<T extends Serializable> {
	
	
	/** Constructor for an asynchronous channel with the given <b>id</b>.
	  * 
	  * @param id an int with the identification number of this channel
	  *  (must be between 0 and 10000).
	  *  
	  * @throws RuntimeException if the <b>id</b> is out of range or
	  *  if an I/O error occurred.
	  */
	public Channel(int id) {
		this(id, false);
	}
	
	
	/** Constructor for a channel with the given <b>id</b> that can work in a
	  *  <b>synchronous</b> mode. 
	  * 
	  * @param id an int with the identification number of this channel
	  *  (must be between 0 and 10000).
	  * 
	  * @param synchronous a boolean that indicates if this channel should be
	  *  build synchronous. All channel variables using the same id must
	  *  construct the channel in the same way. 
	  *  
	  * @throws RuntimeException if the <b>id</b> is out of range or
	  *  if an I/O error occurred.
	  */
	public Channel(int id, boolean synchronous) {
		if (id < 0 || id > 10000)
			throw new RuntimeException("Channel id " + id + " out of bounds (0 - 10000)");
		try {
			server = new ChannelServer(id + 10000, synchronous);
			server.start();
		} catch (IOException e) {}
		try {
			socket = new Socket("localhost", id + 10000);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException("Error creating channel " + id);
		}
	}

	
	/** Sends the given <b>object</b> through the channel. 
	  * 
	  * @param object the element to be passed through the channel.
	  * 
	  * @throws RuntimeException if an I/O error occurred during transmission. 
	  */
	public void send(T object) {
		ChannelRequest request = new ChannelRequest(
			ChannelRequest.RequestOption.PUT, object);
		try {
			outputStream.writeObject(request);
			outputStream.flush();
			outputStream.reset();
			if (synchronous)
				inputStream.readObject();
		} catch (IOException e) {
			throw new RuntimeException("Error while sending message");
		} catch (ClassNotFoundException e) {}
	}
	
	
	/** Reads an object from the channel. If no element is available blocks
	  *  until one object is written by another process.
	  * 
	  * @return the received object.
	  * 
	  * @throws RuntimeException if an I/O occurred during transmission.
	  */
	@SuppressWarnings("unchecked")
	public T receive() {
		ChannelRequest request = new ChannelRequest(
			ChannelRequest.RequestOption.GET, null);
		T result = null;
		try {
			outputStream.writeObject(request);
			outputStream.flush();
			outputStream.reset();
			result = (T)inputStream.readObject();
		} catch (Exception e) {
			throw new RuntimeException("Error while receiving object");
		}
		return result;
	}

	
	/** Main function that creates a process to manage a given channel.*/
	public static void main(String[] args) {
		if (args.length == 0)
			System.err.println("Error the channel id is required (0 - 10000).");
		try { new Channel<Serializable>(Integer.parseInt(args[0])); }
		catch (NumberFormatException e) {
			System.err.println("Error: invalid channel id " + args[0]);
		}
	}
	

	private ChannelServer server;            /** Server thread if this was build first. */
	private Socket socket;                   /** Socket to communicate through. */
	private ObjectInputStream inputStream;   /** The socket object input stream. */
	private ObjectOutputStream outputStream; /** The socket object output stream. */
	private boolean synchronous;             /** Indicates if this channel is synchronous. */
	

	
	/** Internal request structure. */
	private static class ChannelRequest implements Serializable {
		private static final long serialVersionUID = 7105851698096727208L;
		public enum RequestOption { GET, PUT, ACK }
		public RequestOption option;
		public Serializable serializable;
		public ChannelRequest(RequestOption option, Serializable serializable) {
			this.option = option;
			this.serializable = serializable;
		}
	}
	
	

	/** Internal server thread. */
	private static class ChannelServer extends Thread {

		private ServerSocket serverSocket;           /** Socket to accept new connections. */
		private List<Serializable> buffer;           /** Buffer of transmitted elements. */
		private List<ObjectOutputStream> pendingAck; /** Pending acknowledgments if synchronous. */
		private List<ChannelClient> clients;         /** One thread to manage each connected process. */
		private boolean synchronous;                 /** Indicates if the channel is synchronous. */
		
		
		/** Constructor for a ChannelServer.
		  *
		  * @param id an int with the identifier of the channel.
		  * 
		  * @param synchronous a boolean that indicates if the channel is synchronous.
		  * 
		  * @throws IOException if an I/O error occurred.
		  */
		public ChannelServer(int id, boolean synchronous) throws IOException {
			this.serverSocket = new ServerSocket(id);
			this.buffer = new LinkedList<Serializable>();
			this.clients = new LinkedList<ChannelClient>();
			this.synchronous = synchronous;
		}
		
		
		/** Thread run method. This server runs forever accepting new connections. */
		@Override
		public void run() {
			try {
				while (true) {
					Socket socket = serverSocket.accept();
					ChannelClient channelClient = new ChannelClient(this, socket);
					synchronized (clients) { clients.add(channelClient); }
					channelClient.start();
				}
			} catch (IOException e) {}
		}
		
		
		/** Removes the first pending message of the server buffer.
		  * Blocks if the buffer is empty until a message is sent.
		  *
		  * @return the first message of the buffer. 
		  */
		public synchronized Serializable pop() {
			while (buffer.isEmpty()) {
				try { wait(); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
			return buffer.remove(0);
		}
		
		
		/** Enqueues a new message object to the server buffer.
		 * 
		 * @param serializable a Serializable object that will be added to the
		 *  server buffer.
		 */
		public synchronized void push(Serializable serializable) {
			buffer.add(serializable);
			notify();
		}
		
		
		/** Removes the first stream pending for acknowledgment (only for synchronous channels).
		  *  
		  *  @return the first stream pending for acknowledgment.
		  */
		public ObjectOutputStream popPending() {
			return pendingAck.remove(0);
		}
		
		
		/** Adds a new stream pending for acknowledgment (only for synchronous channels).
		  *
		  * @param oos an ObjectOutputStream pending for acknowledgment.
		  */
		public void pushPending(ObjectOutputStream oos) {
			pendingAck.add(oos);
		}
		
	}
	
	
	
	/** Thread class that manages an active channel connection. */
	private static class ChannelClient extends Thread {
		
		private ChannelServer server; /** Parent server. */
		private Socket socket;        /** Socket to the server. */
		
		
		/** Constructor for a ChannelClient.
		  *
		  * @param server a ChannelServer that built this client.
		  * 
		  * @param socket a Socket to communicate with the server.
		  */
		public ChannelClient(ChannelServer server, Socket socket) {
			this.server = server;
			this.socket = socket;
		}
		

		/** Thread run method. This client runs forever waiting for incoming objects from
		  *  the channel end it manages forwarding them to the server and back.
		  */
		@Override
		public void run() {
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				while (true) {
					try {
						ChannelRequest request = (ChannelRequest)ois.readObject();
						switch (request.option) {
							case GET:
								synchronized (server) {
									oos.writeObject(server.pop());
									oos.flush();
									oos.reset();
									if (server.synchronous)
										server.popPending().writeObject(new ChannelRequest(
											ChannelRequest.RequestOption.ACK, null));
								}
								break;
							case PUT:
								synchronized (server) {
									server.push(request.serializable);
									if (server.synchronous)
										server.pushPending(oos);
								}
								break;
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				synchronized (server.clients) {
					server.clients.remove(this);
				}
			}
		}
		
	}
	
	
}
