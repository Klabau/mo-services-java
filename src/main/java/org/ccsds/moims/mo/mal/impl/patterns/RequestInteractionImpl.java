/* ----------------------------------------------------------------------------
 * (C) 2010      European Space Agency
 *               European Space Operations Centre
 *               Darmstadt Germany
 * ----------------------------------------------------------------------------
 * System       : CCSDS MO MAL Implementation
 * Author       : cooper_sf
 *
 * ----------------------------------------------------------------------------
 */
package org.ccsds.moims.mo.mal.impl.patterns;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.impl.Address;
import org.ccsds.moims.mo.mal.impl.MessageSend;
import org.ccsds.moims.mo.mal.provider.MALRequest;
import org.ccsds.moims.mo.mal.transport.MALMessage;

/**
 * Request interaction class.
 */
public class RequestInteractionImpl extends BaseInteractionImpl implements MALRequest
{
  /**
   * Constructor.
   * @param sender Used to return the messages.
   * @param address Details of this endpoint.
   * @param internalTransId Internal transaction identifier.
   * @param msg The source message.
   */
  public RequestInteractionImpl(MessageSend sender,
          Address address,
          Long internalTransId,
          MALMessage msg) throws MALInteractionException
  {
    super(sender, address, internalTransId, msg);
  }

  @Override
  /**
   *
   * @param result
   * @throws MALException
   */
  public MALMessage sendResponse(Object... result) throws MALInteractionException, MALException
  {
    return returnResponse(MALRequestOperation.REQUEST_RESPONSE_STAGE, result);
  }

  @Override
  /**
   *
   * @param error
   * @throws MALException
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage sendError(MALStandardError error) throws MALException
  {
    return returnError(MALRequestOperation.REQUEST_RESPONSE_STAGE, error);
  }
}
