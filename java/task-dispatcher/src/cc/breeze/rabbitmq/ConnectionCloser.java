package cc.breeze.rabbitmq;

import com.rabbitmq.client.Connection;

public class ConnectionCloser implements AutoCloseable {

	private final Connection connection;
	public ConnectionCloser(Connection conn) {

		this.connection = conn;
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	@Override
	public void close() throws Exception {
		this.connection.close();
	}

}
