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

import esa.mo.mal.impl.broker.BrokerMatcher;
import esa.mo.mal.impl.broker.MALBrokerImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.AttributeList;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.NamedValue;
import org.ccsds.moims.mo.mal.structures.SubscriptionFilterList;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALEncodedElementList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALPublishBody;

/**
 * Holds a list of subscriptions keyed on a subscription Id
 */
public class Subscriptions {

    private final ArrayList<SubscriptionConsumer> subscriptions = new ArrayList<>();
    private final String subscriptionId;

    public Subscriptions(final String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public final ArrayList<SubscriptionConsumer> getSubscriptions() {
        return subscriptions;
    }

    public void report() {
        StringBuilder str = new StringBuilder();
        str.append("    START Subscription: ").append(subscriptionId);

        for (SubscriptionConsumer key : subscriptions) {
            str.append("            : Rqd : ").append(key);
        }
        MALBrokerImpl.LOGGER.log(Level.FINE, str.toString());
    }

    public void setIds(final IdentifierList domain,
            final MALMessageHeader srcHdr, final SubscriptionFilterList filters) {
        subscriptions.clear();
        subscriptions.add(new SubscriptionConsumer(domain, srcHdr, filters));
    }

    /**
     * The generateNotifyMessage method returns a NotifyMessage object if there
     * are matches with any of the subscriptions, or a null if there are no
     * matches.
     *
     * @param srcHdr The MAL message header.
     * @param srcDomainId The source domain.
     * @param updateHeaderList The update header list.
     * @param publishBody The publish body.
     * @param keyNames The key names.
     * @return The Notify message body.
     * @throws org.ccsds.moims.mo.mal.MALException if the key values size does
     * not match the key names size.
     */
    public NotifyMessageBody generateNotifyMessage(final MALMessageHeader srcHdr,
            final IdentifierList srcDomainId,
            final UpdateHeaderList updateHeaderList,
            final MALPublishBody publishBody,
            IdentifierList keyNames) throws MALException {
        MALBrokerImpl.LOGGER.fine("Generating Notify Message...");

        final List[] updateLists = publishBody.getUpdateLists((List[]) null);
        List[] notifyLists = null;

        // have to check for the case where the pubsub message does not contain a body
        if (updateLists != null) {
            notifyLists = new List[updateLists.length];

            for (int i = 0; i < notifyLists.length; i++) {
                if (updateLists[i] != null) {
                    if (updateLists[i] instanceof MALEncodedElementList) {
                        MALEncodedElementList encodedElementList = (MALEncodedElementList) updateLists[i];
                        notifyLists[i] = new MALEncodedElementList(
                                encodedElementList.getShortForm(), encodedElementList.size());
                    } else {
                        notifyLists[i] = (List) ((Element) updateLists[i]).createElement();
                    }
                } else {
                    // publishing an empty list
                    notifyLists[i] = null;
                }
            }
        }

        final UpdateHeaderList notifyHeaders = new UpdateHeaderList();

        for (int i = 0; i < updateHeaderList.size(); i++) {
            AttributeList keyValues = updateHeaderList.get(i).getKeyValues();

            if (keyValues.size() != keyNames.size()) {
                throw new MALException("The keyValues size don't match the providerNames "
                        + "size: " + keyValues.size() + "!=" + keyNames.size()
                        + "\nkeyNames: " + keyNames.toString()
                        + "\nkeyValues: " + keyValues.toString());
            }

            // Prepare the Key-Value list
            List<NamedValue> providerKeyValues = new ArrayList<>();

            for (int j = 0; j < keyNames.size(); j++) {
                Identifier name = keyNames.get(j);
                Object value = keyValues.get(j);
                value = (Attribute) Attribute.javaType2Attribute(value);
                providerKeyValues.add(new NamedValue(name, (Attribute) value));
            }

            UpdateKeyValues providerUpdates = new UpdateKeyValues(srcHdr, srcDomainId, providerKeyValues);

            if (BrokerMatcher.keyValuesMatchSubs(providerUpdates, subscriptions)) {
                // add update for this consumer/subscription
                notifyHeaders.add(updateHeaderList.get(i));

                if (notifyLists != null) {
                    for (int j = 0; j < notifyLists.length; j++) {
                        if ((notifyLists[j] != null) && (updateLists[j] != null)) {
                            notifyLists[j].add(updateLists[j].get(i));
                        }
                    }
                }
            }
        }

        if (!notifyHeaders.isEmpty()) {
            return new NotifyMessageBody(new Identifier(subscriptionId),
                    notifyHeaders, notifyLists, srcHdr);

        }

        return null;
    }
}
