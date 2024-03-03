package limit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class UnclosableInputStream extends InputStream
{
	private InputStream inner;
	
	UnclosableInputStream(InputStream inner)
	{
		this.inner = inner;
	}
	
	@Override
	public void close() throws IOException
	{
		// no
	}
	
	InputStream getInnerStream()
	{
		return this.inner;
	}
	
	@Override
	public int read() throws IOException
	{
		return this.inner.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException
	{
		return this.inner.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return this.inner.read(b, off, len);
	}
	
	@Override
	public byte[] readAllBytes() throws IOException
	{
		return this.inner.readAllBytes();
	}
	
	@Override
	public byte[] readNBytes(int len) throws IOException
	{
		return this.inner.readNBytes(len);
	}
	
	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException
	{
		return this.inner.readNBytes(b, off, len);
	}
	
	@Override
	public long skip(long n) throws IOException
	{
		return this.inner.skip(n);
	}
	
	@Override
	public void skipNBytes(long n) throws IOException
	{
		this.inner.skipNBytes(n);
	}
	
	@Override
	public int available() throws IOException
	{
		return this.inner.available();
	}
	
	@Override
	public synchronized void mark(int readlimit)
	{
		this.inner.mark(readlimit);
	}
	
	@Override
	public synchronized void reset() throws IOException
	{
		this.inner.reset();
	}
	
	@Override
	public boolean markSupported()
	{
		return this.inner.markSupported();
	}
	
	@Override
	public long transferTo(OutputStream out) throws IOException
	{
		return this.inner.transferTo(out);
	}
}
