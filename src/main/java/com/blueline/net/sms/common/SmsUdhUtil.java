package com.blueline.net.sms.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Toolkit class for SmsUdhElement objects.
 *
 * @author Markus Eriksson
 * @version $Id$
 */
public final class SmsUdhUtil
{
	public static final int PDUMAXLENGTH = 140;
	public  static final int ASCIIMAXLENGTH = 159;
    /**
     * Constructor for SmsUdhUtil.
     */
    private SmsUdhUtil()
    {
    }

    /**
     * Calculates the number of bytes needed for the supplied udh elements.
     * 
     * @param udhElements The udh elements
     * @return The size (in bytes)
     */
    public static int getTotalSize(SmsUdhElement[] udhElements)
    {
        int totLength = 0;

        if (udhElements == null)
        {
            return 0;
        }

        for (SmsUdhElement udhElement : udhElements) {
            totLength += udhElement.getTotalSize();
        }

        return totLength;
    }

    /**
     * Returns the whole udh as a byte array.
     * <p>
     * The returned UDH is the same as specified when the message was created.
     * No concat headers are added.
     * 
     * TODO: Rename this function. The name is totally wrong.
     * 
     * @return the UDH elements as a byte array.
     */
    public static byte[] toByteArray(SmsUdhElement[] udhElements)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
        
        if (udhElements == null)
        {
            return new byte[0];
        }

        baos.write((byte) SmsUdhUtil.getTotalSize(udhElements));

        try
        {
            for (SmsUdhElement udhElement : udhElements) {
                udhElement.writeTo(baos);
            }
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }

        return baos.toByteArray();
    }
    
    /**
     * Calculates if the given data needs a concatenated SMS.
     * 
     * @param ud User data
     * @param udh UDH elements
     * @return true if the message must be concatentated.
     */
    public static boolean isConcat(SmsUserData ud, byte[] udh)
    {
        int udLength = ud.getLength();
        
        int bytesLeft = PDUMAXLENGTH;
        int maxChars;
        
        if (udh != null)
        {
            bytesLeft -= udh.length;
        }

        switch (ud.getDcs().getAlphabet())
        {
        case GSM:
            maxChars = (bytesLeft * 8) / 7;
            break;
            
        case UCS2:
            maxChars = bytesLeft / 2;
            break;
            
       
        case ASCII:
        	maxChars = bytesLeft+ASCIIMAXLENGTH-PDUMAXLENGTH ; 
            break;
        case LATIN1:
        default:
            maxChars = bytesLeft;
            break;
        }

        return (udLength > maxChars);
    }
    
    /**
     * Creates a "8Bit concatenated" UDH element using UDH_IEI_CONCATENATED_8BIT.
     * 
     * This can be used to create a concatenated SMS.
     *
     * @param refNr The reference number of this SMS, must be the same in
     * all SMS. Max 255.
     * @param totalNumberOfSms Total number of SMS. Max 255.
     * @param seqNr Sequence number. Max 255.
     * @return A SmsUdhElement
     */
    public static SmsUdhElement get8BitConcatUdh(int refNr, int totalNumberOfSms, int seqNr)
    {
        byte[] udh = new byte[3];

        udh[0] = (byte) (refNr  & 0xff);
        udh[1] = (byte) (totalNumberOfSms & 0xff);
        udh[2] = (byte) (seqNr  & 0xff);

        return new SmsUdhElement(SmsUdhIei.CONCATENATED_8BIT, udh);
    }

    /**
     * Creates a "Message waiting" UDH element using UDH_IEI_SPECIAL_MESSAGE.
     * <p>
     * If more than one type of message is required to be indicated within
     * one SMS message, then multiple "Message waiting" UDH elements must
     * be used.
     * <p>
     * <b>Special handling in concatenated messages:</b><br>
     * <i>
     * "In the case where this IEI is to be used in a concatenated SM then the
     * IEI, its associated IEI length and IEI data shall be contained in the
     * first segment of the concatenated SM. The IEI, its associated IEI length
     * and IEI data should also be contained in every subsequent segment of the
     * concatenated SM although this is not mandatory. However, in the case
     * where these elements are not contained in every subsequent segment of
     * the concatenated SM and where an out of sequence segment delivery
     * occurs or where the first segment is not delivered then processing
     * difficulties may arise at the receiving entity which may result in
     * the concatenated SM being totally or partially discarded."
     * </i>
     *
     * @param storeMsg Set to true if the message should be stored
     * @param msgType Message type, may be one of MESSAGE_WAITING_VOICE,
     * MESSAGE_WAITING_FAX, MESSAGE_WAITING_EMAIL or MESSAGE_WAITING_OTHER.
     * @param msgCount Number of messages waiting for retrieval. Max 255
     * messages. The value 255 shall be taken to mean 255 or greater.
     * @return A SmsUdhElement
     */
    public static SmsUdhElement getMessageWaitingUdh(boolean storeMsg, int msgType, int msgCount)
    {
        byte[] udh = new byte[2];

        udh[0] = (byte) (msgType  & 0x7f);
        if ( storeMsg )
        {
            udh[0] |= (byte) (0x80);
        }
        udh[1] = (byte) (msgCount & 0xff);

        return new SmsUdhElement(SmsUdhIei.SPECIAL_MESSAGE, udh);
    }

    /**
     * Creates a "8 bit Application Port Adressing" UDH element
     * using UDH_IEI_APP_PORT_8BIT
     * <p>
     * Note! Only values between 240 and 255 are usable, the rest of the port
     * numbers are marked as reserved.
     * <p>
     * <b>Special handling in concatenated messages:</b><br>
     * <i>
     * In the case where this IE is to be used in a concatenated SM then the
     * IEI, its associated IEI length and IEI data shall be contained in the
     * first segment of the concatenated SM. The IEI, its associated IEI length
     * and IEI data shall also be contained in every subsequent segment of the
     * concatenated SM.
     * </i>
     * @param destPort Destination port
     * @param origPort Source port
     * @return A SmsUdhElement
     */
    public static SmsUdhElement get8BitApplicationPortUdh(SmsPort destPort, SmsPort origPort)
    {
        byte[] udh = new byte[2];

        int destPortNo = destPort.getPort();
        int origPortNo = origPort.getPort();

        udh[0] = (byte) (destPortNo & 0xff);
        udh[1] = (byte) (origPortNo & 0xff);

        return new SmsUdhElement(SmsUdhIei.APP_PORT_8BIT, udh);
    }

    /**
     * Creates a "16 bit Application Port Adressing" UDH element
     * using UDH_IEI_APP_PORT_16BIT
     * <p>
     * Note! Only values between 0 and 16999 are usable, the rest of the port
     * numbers are marked as reserved.
     * <p>
     * <b>Special handling in concatenated messages:</b><br>
     * <i>
     * In the case where this IE is to be used in a concatenated SM then the
     * IEI, its associated IEI length and IEI data shall be contained in the
     * first segment of the concatenated SM. The IEI, its associated IEI length
     * and IEI data shall also be contained in every subsequent segment of the
     * concatenated SM.
     * </i>
     * @param destPort Destination port
     * @param origPort Source port
     * @return A SmsUdhElement
     */
    public static SmsUdhElement get16BitApplicationPortUdh(SmsPort destPort, SmsPort origPort)
    {
        byte[] udh = new byte[4];

        int destPortNo = destPort.getPort();
        int origPortNo = origPort.getPort();

        udh[0] = (byte) ((destPortNo >> 8) & 0xff);
        udh[1] = (byte) (destPortNo & 0xff);
        udh[2] = (byte) ((origPortNo >> 8) & 0xff);
        udh[3] = (byte) (origPortNo & 0xff);

        return new SmsUdhElement(SmsUdhIei.APP_PORT_16BIT, udh);
    }

    /**
     * Creates a "16Bit concatenated" UDH element using UDH_IEI_CONCATENATED_16BIT
     * <p>
     * This can be used to create a concatenated SMS.
     *
     * @param refNr The reference number of this SMS, must be the same in
     * all SMS. Max 65536
     * @param totalNumberOfSms Total number of SMS. Max 255
     * @param seqNr Sequence number. Max 255
     * @return A SmsUdhElement
     */
    public static SmsUdhElement get16BitConcatUdh(int refNr, int totalNumberOfSms, int seqNr)
    {
        byte[] udh = new byte[4];

        udh[0] = (byte) ((refNr >> 8) & 0xff);
        udh[1] = (byte) (refNr & 0xff);
        udh[2] = (byte) (totalNumberOfSms & 0xff);
        udh[3] = (byte) (seqNr  & 0xff);

        return new SmsUdhElement(SmsUdhIei.CONCATENATED_16BIT, udh);
    }
}
