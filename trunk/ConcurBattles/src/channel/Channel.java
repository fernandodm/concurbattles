package channel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;


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
		this.synchronous = synchronous;
		try {
			server = new ChannelServer(id + 10000, synchronous);
			server.start();
		} catch (IOException e) {}
		try {
			socket = new Socket("localhost", id + 10000);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			input = new LinkedList<Serializable>();
			acks = new Semaphore(0);
			new Thread() {
				public void run() {
					try {
						while (true) {
							ChannelRequest request = (ChannelRequest)inputStream.readObject();
							if (request.option == ChannelRequest.RequestOption.ACK)
								acks.release();
							else
								push(request.serializable);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {}
				};
			}.start();
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
			synchronized (this) {
				outputStream.writeObject(request);
				outputStream.flush();
				outputStream.reset();
			}
			if (synchronous)
				acks.acquireUninterruptibly();
		} catch (IOException e) {
			throw new RuntimeException("Error while sending message");
		}
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
			synchronized (this) {
				outputStream.writeObject(request);
				outputStream.flush();
				outputStream.reset();
			}
			result = (T)pop();
		} catch (Exception e) {
			throw new RuntimeException("Error while receiving object");
		}
		return result;
	}
	
	
	/** Removes the first pending message of the input buffer.
	  * Blocks if the buffer is empty until a message is sent.
	  *
	  * @return the first message of the buffer. 
	  */
	private synchronized Serializable pop() {
		while (input.isEmpty()) {
			try { wait(); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
		return input.remove(0);
	}
	
	
	/** Enqueues a new message object to the input buffer.
	 * 
	 * @param serializable a Serializable object that will be added to the
	 *  server buffer.
	 */
	private synchronized void push(Serializable serializable) {
		input.add(serializable);
		notify();
	}
	
	
	/** Performs necessary housekeeping. */
	@Override
	protected void finalize() throws Throwable {
		try { inputStream.close(); } catch (IOException e) {}
		try { outputStream.close(); } catch (IOException e) {}
		super.finalize();
	}
	

	private ChannelServer server;            /** Server thread if this was build first. */
	private Socket socket;                   /** Socket to communicate through. */
	private ObjectOutputStream outputStream; /** The socket object output stream. */
	private ObjectInputStream inputStream;   /** The socket object input stream. */
	private List<Serializable> input;        /** Buffer with input messages. */
	private Semaphore acks;                  /** Semaphore with acknowledgments. */
	private boolean synchronous;            /** Indicates if this channel is synchronous. */
	

	
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
		private List<ChannelRequest> buffer;         /** Buffer of transmitted elements. */
		private List<ObjectOutputStream> pendingAck; /** Pending acknowledgments if synchronous. */
		private List<ChannelClient> clients;         /** One thread to manage each connected process. */
		private boolean synchronous;                /** Indicates if the channel is synchronous. */
		
		
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
			this.buffer = new LinkedList<ChannelRequest>();
			this.clients = new LinkedList<ChannelClient>();
			this.pendingAck = new LinkedList<ObjectOutputStream>();
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
		public synchronized void push(ChannelRequest serializable) {
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
				final ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				final ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				final Semaphore get = new Semaphore(0, true);
				
				new Thread() {
					public void run() {
						get.acquireUninterruptibly();
						while (true) {
							synchronized (server) {
								try {
									oos.writeObject(server.pop());
									oos.flush();
									oos.reset();
									if (server.synchronous) {
										server.popPending().writeObject(new ChannelRequest(
											ChannelRequest.RequestOption.ACK, null));
									}
								} catch (IOException e) {}
							}
						}
					}
				}.start();
				
				while (true) {
					try {
						ChannelRequest request = (ChannelRequest)ois.readObject();
						switch (request.option) {
							case GET:
								get.release();
								break;
							case PUT:
								synchronized (server) {
									server.push(request);
									if (server.synchronous)
										server.pushPending(oos);
								}
								break;
							default:
						}
					} catch (ClassNotFoundException e) {}
				}
			} catch (IOException e) {
				synchronized (server.clients) {
					server.clients.remove(this);
				}
			}
		}
		
	}
	
}
