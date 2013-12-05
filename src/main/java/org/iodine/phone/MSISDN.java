package org.iodine.phone;

import java.io.Serializable;

/**
 * MSISDN ("Mobile Subscriber Integrated Services Digital Network", (alternate: Mobile Station ISDN)
 * is a number uniquely identifying a subscription in a GSM or a UMTS mobile network, the telephone
 * number of the SIM card in a mobile phone.
 * <p/>
 * The MSISDN together with IMSI [2] are two important numbers used for identifying a mobile subscriber.
 * The latter identifies the SIM, i.e. the card inserted in to the mobile phone, while the former is used
 * for routing calls to the subscriber.  IMSI is often used as a key in the HLR ("subscriber database")
 * and MSISDN is the number normally dialled to connect a  call to the mobile phone.
 * A SIM is uniquely associated to an IMSI, while the MSISDN can change in time (e.g. due to number
 * portability),  i.e., different MSISDNs can be associated with the SIM.
 * <p/>
 * The MSISDN follows the numbering plan defined in the ITU-T recommendation E.164.
 * <p/>
 * <dl>
 * <dt>MSISDN</dt><dd>CC + NDC + SN</dd>
 * <dt>CC</dt><dd>Country Code</dd>
 * <dt>NDC</dt><dd>National Destination Code, identifies one or part of a PLMN[2]</dd>
 * <dt>SN</dt><dd>Subscriber Number</dd>
 * </dl>
 * [1] <i>International Mobile Subscriber Identity</i><br/>
 * [2] <i>Public Land Mobile Network</i><p/>
 * <h3>Example</h3>
 * MSISDN: 353521234567
 * <table border="1">
 * <tr><td>CC</td><td>353</td><td>Ireland</td></tr>
 * <tr><td>NDC</td><td>52</td><td>Waterford</td></tr>
 * <tr><td>SN</td><td>1234567</td><td>Subscriber's number</td></tr>
 * </table>
 * <p/>
 *
 * @author roy.phillips
 */
public final class MSISDN
    implements Comparable<MSISDN>, Serializable {
  private static final long serialVersionUID = -1405789554724028687L;
  private final long value;

  private transient MSISDNScheme scheme;

  /**
   * Private constructor, used by factory methods to set-up the value and scheme of
   * a new MSISDN (as they are final fields)
   *
   * @param value  the MSISDN number as a long
   * @param scheme the scheme to which the MSISDN belongs
   */
  private MSISDN(Long value, MSISDNScheme scheme) {
    this.value = value;
    this.scheme = scheme;
  }

  /** @return a new MSISDN number, initialized with the supplied value,
   *  within the domain of the given scheme
   * @param value of the phone number to return
   * @param scheme defining the number's domain
   */
  static MSISDN fromLong (Long value, MSISDNScheme scheme) {
    return new MSISDN(value,scheme);
  }

  /** @return a new MSISDN from the three parts supplied, belonging to the
   * specified scheme
   *
   * @param cc     country code
   * @param ndc    national dialling code
   * @param sn     subscriber number
   * @param scheme to which the resultant MSISDN should belong
   */
  static MSISDN create(final int cc, final int ndc, final int sn, MSISDNScheme scheme) {
    return new MSISDN(scheme.longValue(cc, ndc, sn), scheme);
  }

  /** @return a MSISDN from the string representation supplied,
   * inferring the scheme from the known set of schemes
   *
   * @param msisdnString string MSISDN number
   * @throws IllegalArgumentException if the string is not a valid MSISDN for known schemes
   */
  public static MSISDN parse(String msisdnString) {
    return MSISDNFactory.createMSISDN(msisdnString);
  }

  /** @return a new MSIDN having the value set from the supplied
   * number, and inferring the scheme by matching declared
   * country codes against the start of the number
   *
   * @param number MSISDN number as long
   */
  public static MSISDN valueOf(long number) {
    return MSISDNFactory.fromLong(number);
  }

  /** @return the country code part of the MSISDN number
   *    an integer representing the country code (CC) */
  public int getCountryCode() {
    return (int) (value / getScheme().ccfactor);
  }

  /** @return the national dialing code part of the MSISDN number
   *    an integer representing the national dialing code (NDC) */
  public int getNationalDialingCode() {
    return (int) ((value - (getCountryCode() * getScheme().ccfactor))
        / getScheme().ndcfactor);
  }

  /** @return the subscriber number part of the MSISDN number,
   *  an integer representing the subscriber numbe (SN) */
  public int getSubscriberNumber() {
    return (int) (value - (getCountryCode() * getScheme().ccfactor)
        - (getNationalDialingCode() * getScheme().ndcfactor));
  }

  /** @return the scheme defining this number, recreating if necessary
   *            (e.g., after de-serialization, as it is transient) */
  public MSISDNScheme getScheme() {
    if (scheme == null) {
      scheme = MSISDNFactory.fromLong(value).scheme;
    }
    return scheme;
  }

  /** @return the canonical MSISDN format String for the number */
  @Override
  public String toString() {
    return "+" + value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    return value == ((MSISDN) other).value;
  }

  @Override
  public int hashCode() {
    return (int) (value ^ (value >>> 32));
  }

  /** @return a long value representing this MSISDN's numeric value */
  public long longValue() {
    return value;
  }

  @Override
  public int compareTo( MSISDN other) {
    if ( other == null) {
      throw new NullPointerException("compareTo: 'other' may not be null");
    }
    return Long.compare(value, other.value);
  }

  /** @return a new MSISDN builder */
  public static MSISDNBuilder Builder() {
    return new MSISDNBuilder();
  }

  /** Fluent builder for constructing MSISDNs from component parts */
  public static class MSISDNBuilder {
    private int cc;
    private int ndc;
    private int subscriber;

    /** @return this builder with the country code set
     *  @param cc  country code */
    public MSISDNBuilder cc(int cc) {
      this.cc = cc;
      return this;
    }

    /** @return this builder with the national dailling code  set
     *  @param ndc  national dailling code */
    public MSISDNBuilder ndc(int ndc) {
      this.ndc = ndc;
      return this;
    }

    /** @return this builder with the subscriber number set
     *  @param subscriber  subscriber number */
    public MSISDNBuilder subscriber(int subscriber) {
      this.subscriber = subscriber;
      return this;
    }

    /** @return a new MSISDN constructed from the builder's state */
    public MSISDN build() {
      return MSISDNFactory.createMSISDN("+"+cc+ndc+subscriber);
    }
  }

}