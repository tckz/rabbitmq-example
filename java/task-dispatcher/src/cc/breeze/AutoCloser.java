package cc.breeze;

import java.util.LinkedList;

import com.google.common.collect.Lists;

public class AutoCloser implements AutoCloseable {

	private final LinkedList<AutoCloseable>stack = Lists.newLinkedList();
	
	public AutoCloseable add(AutoCloseable c) {
		this.stack.push(c);
		return c;
	}
	
	@Override
	public void close() throws Exception {
		Exception thrown = null;
		while (this.stack.size() > 0) {
			AutoCloseable c = this.stack.pop();
			try {
				c.close();
			} catch (Exception e) {
				if (thrown == null) {
					thrown = e;
				}
			}
		}
		
		if (thrown != null) {
			throw thrown;
		}
	}

}
