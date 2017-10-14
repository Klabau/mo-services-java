/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Fixed Length Binary encoder
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.mal.encoder.binary.variable;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import org.ccsds.moims.mo.mal.MALException;

/**
 * Implements the MALDecoder interface for a fixed length binary encoding.
 */
public class VariableBinaryDecoder extends esa.mo.mal.encoder.binary.base.BaseBinaryDecoder
{
  /**
   * Constructor.
   *
   * @param src Byte array to read from.
   */
  public VariableBinaryDecoder(final byte[] src)
  {
    super(new VariableBinaryBufferHolder(null, src, 0, src.length));
  }

  /**
   * Constructor.
   *
   * @param is Input stream to read from.
   */
  public VariableBinaryDecoder(final java.io.InputStream is)
  {
    super(new VariableBinaryBufferHolder(is, null, 0, 0));
  }

  /**
   * Constructor.
   *
   * @param src Byte array to read from.
   * @param offset index in array to start reading from.
   */
  public VariableBinaryDecoder(final byte[] src, final int offset)
  {
    super(new VariableBinaryBufferHolder(null, src, offset, src.length));
  }

  /**
   * Constructor.
   *
   * @param src Source buffer holder to use.
   */
  protected VariableBinaryDecoder(final BufferHolder src)
  {
    super(src);
  }

  /**
   * MALListDecoder constructor implementation.
   *
   * @param list List to decode into.
   * @param srcBuffer Buffer to manage.
   * @throws MALException If cannot decode list size.
   */
  public VariableBinaryDecoder(final List list, final BufferHolder srcBuffer)
          throws MALException
  {
    super(list, srcBuffer);
  }

  @Override
  public org.ccsds.moims.mo.mal.MALListDecoder createListDecoder(final List list) throws MALException
  {
    return new VariableBinaryDecoder(list, sourceBuffer);
  }

  /**
   * Internal class that implements the fixed length field decoding.
   */
  protected static class VariableBinaryBufferHolder extends BaseBinaryBufferHolder
  {

    private static final BigInteger B_127 = new BigInteger("127");

    /**
     * Constructor.
     *
     * @param is Input stream to read from.
     * @param buf Source buffer to use.
     * @param offset Buffer offset to read from next.
     * @param length Length of readable data held in the array, which may be larger.
     */
    public VariableBinaryBufferHolder(final java.io.InputStream is, final byte[] buf, final int offset, final int length)
    {
      super(is, buf, offset, length);
    }

    /**
     * Constructor allowing child classes to introduce its own input reader
     *
     * @param buf Source buffer to use.
     */
    protected VariableBinaryBufferHolder(final VariableBinaryInputReader buf)
    {
      super(buf);
    }

    @Override
    public long getSignedLong() throws MALException
    {
      final long raw = getUnsignedLong();
      final long temp = (((raw << 63) >> 63) ^ raw) >> 1;
      return temp ^ (raw & (1L << 63));
    }

    @Override
    public int getSignedInt() throws MALException
    {
      final int raw = getUnsignedInt();
      final int temp = (((raw << 31) >> 31) ^ raw) >> 1;
      return temp ^ (raw & (1 << 31));
    }

    @Override
    public short getSignedShort() throws MALException
    {
      return (short) getSignedInt();
    }

    @Override
    public long getUnsignedLong() throws MALException
    {
      long value = 0L;
      int i = 0;
      long b;
      while (((b = get8()) & 128L) != 0)
      {
        value |= (b & 127) << i;
        i += 7;
      }
      return value | (b << i);
    }

    @Override
    public long getUnsignedLong32() throws MALException
    {
      return getUnsignedLong();
    }

    @Override
    public int getUnsignedInt() throws MALException
    {
      int value = 0;
      int i = 0;
      int b;
      while (((b = get8()) & 128) != 0)
      {
        value |= (b & 127) << i;
        i += 7;
      }
      return value | (b << i);
    }

    @Override
    public int getUnsignedInt16() throws MALException
    {
      return getUnsignedInt();
    }

    @Override
    public int getUnsignedShort() throws MALException
    {
      return getUnsignedInt();
    }

    @Override
    public short getUnsignedShort8() throws MALException
    {
      return (short) (get8() & 255);
    }

    @Override
    public BigInteger getBigInteger() throws MALException
    {
      int i = 0;
      int b;
      BigInteger rv = BigInteger.ZERO;
      while (((b = get8()) & 128) != 0)
      {
        rv = rv.or((new BigInteger(Integer.toString(b)).and(B_127)).shiftLeft(i));
        i += 7;
      }
      rv = rv.or(new BigInteger(Integer.toString(b)).shiftLeft(i));
      return rv;
    }
  }

  protected static class VariableBinaryInputReader extends BaseBinaryInputReader
  {

    public VariableBinaryInputReader(InputStream is, byte[] buf, int offset, int length)
    {
      super(is, buf, offset, length);
    }
  }
}
