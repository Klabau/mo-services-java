/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO MAL Java Implementation
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
package esa.mo.mal.impl.pubsub;

import esa.mo.mal.impl.broker.MALBrokerImpl;
import java.util.logging.Level;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Represents a publisher (provider) in a broker, so contains the list of
 * entities it is allowed to publish.
 */
public final class PublisherSource {

    private final String uri;
    private final QoSLevel qosLevel;
    private IdentifierList subscriptionKeyNames;

    public PublisherSource(final String uri, final QoSLevel qosLevel) {
        super();
        this.uri = uri;
        this.qosLevel = qosLevel;
    }

    public QoSLevel getQosLevel() {
        return qosLevel;
    }

    public IdentifierList getSubscriptionKeyNames() {
        return subscriptionKeyNames;
    }

    public void setSubscriptionKeyNames(IdentifierList subscriptionKeyNames) {
        this.subscriptionKeyNames = subscriptionKeyNames;
    }

    public void report() {
        MALBrokerImpl.LOGGER.log(Level.FINE, "  START Provider ( {0} )", uri);
        // MALBrokerImpl.LOGGER.log(Level.FINE, "    Domain : {0}", StructureHelper.domainToString(domain));
        for (Identifier key : subscriptionKeyNames) {
            MALBrokerImpl.LOGGER.log(Level.FINE, "    Allowed key: {0}", key);
        }
        MALBrokerImpl.LOGGER.log(Level.FINE, "  END Provider ( {0} )", uri);
    }

    /*
    public void checkPublish(final MALMessageHeader hdr, 
            final UpdateHeaderList updateList) throws MALInteractionException {
        if (StructureHelper.isSubDomainOf(domain, hdr.getDomain())) {
            // Check if the number of key matches:
            for (final UpdateHeader update : updateList) {
                if(update.getKeyValues().size() != keySet.size()) {
                    MALBrokerImpl.LOGGER.warning("The number of published keys does not match!");
                    throw new MALInteractionException(new MALStandardError(
                            MALHelper.UNKNOWN_ERROR_NUMBER, "The number of published keys does not match!"));
                }
            }
        } else {
            MALBrokerImpl.LOGGER.warning("Provider not allowed to publish to the domain");
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }
    }
    */
    public void checkPublish(final MALMessageHeader hdr, 
            final UpdateHeaderList updateList) throws MALInteractionException {
        // Check if the number of key matches:
        /*
        for (final UpdateHeader update : updateList) {
            if(update.getKeyValues().size() != keySet.size()) {
                MALBrokerImpl.LOGGER.warning("The number of published keys does not match!");
                throw new MALInteractionException(new MALStandardError(
                        MALHelper.UNKNOWN_ERROR_NUMBER, "The number of published keys does not match!"));
            }
        }
        */
    }
}
