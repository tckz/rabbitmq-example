package cc.breeze.jobq;

import java.io.IOException;

public abstract class AckDelegate {

	abstract public void ack(boolean multiple) throws IOException;
}
