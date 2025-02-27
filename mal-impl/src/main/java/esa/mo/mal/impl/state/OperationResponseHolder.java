/* ----------------------------------------------------------------------------
 * Copyright (C) 2016      European Space Agency
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
package esa.mo.mal.impl.state;

import esa.mo.mal.impl.MALContextFactoryImpl;
import java.util.Map;
import java.util.logging.Level;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALInteractionListener;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;

/**
 * This small class is used to hold the response to interactions for a consumer.
 */
public class OperationResponseHolder {

    private final BooleanHolder responseSignal = new BooleanHolder();
    private final MALInteractionListener listener;
    private boolean inError = false;
    private MALMessage result = null;

    public OperationResponseHolder(MALInteractionListener listener) {
        this.listener = listener;
    }

    public OperationResponseHolder(MALPublishInteractionListener listener) {
        this.listener = new InteractionListenerPublishAdapter(listener);
    }

    public MALInteractionListener getListener() {
        return listener;
    }

    public void waitForResponseSignal() {
        // wait for the response signal
        synchronized (responseSignal) {
            while (!responseSignal.getValue()) {
                try {
                    responseSignal.wait();
                } catch (InterruptedException ex) {
                    MALContextFactoryImpl.LOGGER.log(Level.WARNING,
                            "Interrupted waiting for handler lock ", ex);
                }
            }
        }
    }

    public void signalResponse(final boolean isError, final MALMessage msg) {
        this.inError = isError;
        this.result = msg;

        synchronized (responseSignal) {
            responseSignal.setValue();
            responseSignal.notifyAll();
        }
    }

    public MALMessage getResult() throws MALInteractionException, MALException {
        if (inError) {
            throw new MALInteractionException(((MALErrorBody) result.getBody()).getError());
        }

        return result;
    }

    /**
     * Small class to hold a boolean value. Can be used for signalling purposes
     * as can be synchronised on and have its value set.
     */
    private static final class BooleanHolder {

        private boolean value = false;

        /**
         * Returns the current value.
         *
         * @return the current value.
         */
        public synchronized boolean getValue() {
            return value;
        }

        /**
         * Sets the held value to true.
         */
        public synchronized void setValue() {
            value = true;
        }
    }

    /**
     * Wrapper class to allow an PubSub interaction to be processed by common
     * code.
     */
    private static final class InteractionListenerPublishAdapter implements MALInteractionListener {

        private final MALPublishInteractionListener delegate;

        protected InteractionListenerPublishAdapter(final MALPublishInteractionListener delegate) {
            this.delegate = delegate;
        }

        @Override
        public void registerAckReceived(final MALMessageHeader header,
                final Map qosProperties) throws MALException {
            delegate.publishRegisterAckReceived(header, qosProperties);
        }

        @Override
        public void registerErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            delegate.publishRegisterErrorReceived(header, body, qosProperties);
        }

        @Override
        public void deregisterAckReceived(final MALMessageHeader header,
                final Map qosProperties) throws MALException {
            delegate.publishDeregisterAckReceived(header, qosProperties);
        }

        @Override
        public void invokeAckErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void invokeAckReceived(final MALMessageHeader header,
                final MALMessageBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void invokeResponseErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void invokeResponseReceived(final MALMessageHeader header,
                final MALMessageBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void notifyErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void notifyReceived(final MALMessageHeader header,
                final MALNotifyBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void progressAckErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void progressAckReceived(final MALMessageHeader header,
                final MALMessageBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void progressResponseErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void progressResponseReceived(final MALMessageHeader header,
                final MALMessageBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void progressUpdateErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void progressUpdateReceived(final MALMessageHeader header,
                final MALMessageBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void requestErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void requestResponseReceived(final MALMessageHeader header,
                final MALMessageBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void submitAckReceived(final MALMessageHeader header,
                final Map qosProperties) throws MALException {
            // nothing to do here
        }

        @Override
        public void submitErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            // nothing to do here
        }
    }
}
