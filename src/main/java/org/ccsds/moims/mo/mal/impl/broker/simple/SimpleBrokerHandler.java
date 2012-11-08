/* ----------------------------------------------------------------------------
 * (C) 2010      European Space Agency
 *               European Space Operations Centre
 *               Darmstadt Germany
 * ----------------------------------------------------------------------------
 * System       : CCSDS MO MAL Implementation
 * Author       : Sam Cooper
 *
 * ----------------------------------------------------------------------------
 */
package org.ccsds.moims.mo.mal.impl.broker.simple;

import org.ccsds.moims.mo.mal.impl.broker.BaseBrokerHandler;
import org.ccsds.moims.mo.mal.impl.broker.MALBrokerBindingImpl;
import org.ccsds.moims.mo.mal.impl.broker.SubscriptionSource;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Extends the BaseBrokerHandler for the Simple broker implementation.
 */
public class SimpleBrokerHandler extends BaseBrokerHandler
{
  /**
   * Creates a new instance of MALBrokerHandler
   */
  public SimpleBrokerHandler()
  {
  }

  @Override
  protected SubscriptionSource createEntry(final MALMessageHeader hdr, final MALBrokerBindingImpl binding)
  {
    return new SimpleSubscriptionSource(hdr, binding);
  }
}
