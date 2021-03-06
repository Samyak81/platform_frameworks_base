/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.security;

import android.content.Context;
import android.text.TextUtils;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

/**
 * This provides the required parameters needed for initializing the
 * {@code KeyPairGenerator} that works with <a href="{@docRoot}
 * guide/topics/security/keystore.html">Android KeyStore facility</a>. The
 * Android KeyStore facility is accessed through a
 * {@link java.security.KeyPairGenerator} API using the {@code AndroidKeyStore}
 * provider. The {@code context} passed in may be used to pop up some UI to ask
 * the user to unlock or initialize the Android KeyStore facility.
 * <p>
 * After generation, the {@code keyStoreAlias} is used with the
 * {@link java.security.KeyStore#getEntry(String, java.security.KeyStore.ProtectionParameter)}
 * interface to retrieve the {@link PrivateKey} and its associated
 * {@link Certificate} chain.
 * <p>
 * The KeyPair generator will create a self-signed certificate with the subject
 * as its X.509v3 Subject Distinguished Name and as its X.509v3 Issuer
 * Distinguished Name along with the other parameters specified with the
 * {@link Builder}.
 * <p>
 * The self-signed X.509 certificate may be replaced at a later time by a
 * certificate signed by a real Certificate Authority.
 *
 * @hide
 */
public final class AndroidKeyPairGeneratorSpec implements AlgorithmParameterSpec {
    private final String mKeystoreAlias;

    private final Context mContext;

    private final X500Principal mSubjectDN;

    private final BigInteger mSerialNumber;

    private final Date mStartDate;

    private final Date mEndDate;

    private final int mFlags;

    /**
     * Parameter specification for the "{@code AndroidKeyPairGenerator}"
     * instance of the {@link java.security.KeyPairGenerator} API. The
     * {@code context} passed in may be used to pop up some UI to ask the user
     * to unlock or initialize the Android keystore facility.
     * <p>
     * After generation, the {@code keyStoreAlias} is used with the
     * {@link java.security.KeyStore#getEntry(String, java.security.KeyStore.ProtectionParameter)}
     * interface to retrieve the {@link PrivateKey} and its associated
     * {@link Certificate} chain.
     * <p>
     * The KeyPair generator will create a self-signed certificate with the
     * properties of {@code subjectDN} as its X.509v3 Subject Distinguished Name
     * and as its X.509v3 Issuer Distinguished Name, using the specified
     * {@code serialNumber}, and the validity date starting at {@code startDate}
     * and ending at {@code endDate}.
     *
     * @param context Android context for the activity
     * @param keyStoreAlias name to use for the generated key in the Android
     *            keystore
     * @param subjectDN X.509 v3 Subject Distinguished Name
     * @param serialNumber X509 v3 certificate serial number
     * @param startDate the start of the self-signed certificate validity period
     * @param endDate the end date of the self-signed certificate validity
     *            period
     * @throws IllegalArgumentException when any argument is {@code null} or
     *             {@code endDate} is before {@code startDate}.
     * @hide should be built with AndroidKeyPairGeneratorSpecBuilder
     */
    public AndroidKeyPairGeneratorSpec(Context context, String keyStoreAlias,
            X500Principal subjectDN, BigInteger serialNumber, Date startDate, Date endDate,
            int flags) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        } else if (TextUtils.isEmpty(keyStoreAlias)) {
            throw new IllegalArgumentException("keyStoreAlias must not be empty");
        } else if (subjectDN == null) {
            throw new IllegalArgumentException("subjectDN == null");
        } else if (serialNumber == null) {
            throw new IllegalArgumentException("serialNumber == null");
        } else if (startDate == null) {
            throw new IllegalArgumentException("startDate == null");
        } else if (endDate == null) {
            throw new IllegalArgumentException("endDate == null");
        } else if (endDate.before(startDate)) {
            throw new IllegalArgumentException("endDate < startDate");
        }

        mContext = context;
        mKeystoreAlias = keyStoreAlias;
        mSubjectDN = subjectDN;
        mSerialNumber = serialNumber;
        mStartDate = startDate;
        mEndDate = endDate;
        mFlags = flags;
    }

    /**
     * Returns the alias that will be used in the {@code java.security.KeyStore}
     * in conjunction with the {@code AndroidKeyStore}.
     */
    public String getKeystoreAlias() {
        return mKeystoreAlias;
    }

    /**
     * Gets the Android context used for operations with this instance.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Gets the subject distinguished name to be used on the X.509 certificate
     * that will be put in the {@link java.security.KeyStore}.
     */
    public X500Principal getSubjectDN() {
        return mSubjectDN;
    }

    /**
     * Gets the serial number to be used on the X.509 certificate that will be
     * put in the {@link java.security.KeyStore}.
     */
    public BigInteger getSerialNumber() {
        return mSerialNumber;
    }

    /**
     * Gets the start date to be used on the X.509 certificate that will be put
     * in the {@link java.security.KeyStore}.
     */
    public Date getStartDate() {
        return mStartDate;
    }

    /**
     * Gets the end date to be used on the X.509 certificate that will be put in
     * the {@link java.security.KeyStore}.
     */
    public Date getEndDate() {
        return mEndDate;
    }

    /**
     * @hide
     */
    int getFlags() {
        return mFlags;
    }

    /**
     * Returns {@code true} if this parameter will require generated keys to be
     * encrypted in the {@link java.security.KeyStore}.
     */
    public boolean isEncryptionRequired() {
        return (mFlags & KeyStore.FLAG_ENCRYPTED) != 0;
    }

    /**
     * Builder class for {@link AndroidKeyPairGeneratorSpec} objects.
     * <p>
     * This will build a parameter spec for use with the <a href="{@docRoot}
     * guide/topics/security/keystore.html">Android KeyStore facility</a>.
     * <p>
     * The required fields must be filled in with the builder.
     * <p>
     * Example:
     *
     * <pre class="prettyprint">
     * Calendar start = new Calendar();
     * Calendar end = new Calendar();
     * end.add(1, Calendar.YEAR);
     *
     * AndroidKeyPairGeneratorSpec spec =
     *         new AndroidKeyPairGeneratorSpec.Builder(mContext)
     *                 .setAlias(&quot;myKey&quot;)
     *                 .setSubject(new X500Principal(&quot;CN=myKey&quot;))
     *                 .setSerial(BigInteger.valueOf(1337))
     *                 .setStartDate(start.getTime())
     *                 .setEndDate(end.getTime())
     *                 .build();
     * </pre>
     */
    public final static class Builder {
        private final Context mContext;

        private String mKeystoreAlias;

        private X500Principal mSubjectDN;

        private BigInteger mSerialNumber;

        private Date mStartDate;

        private Date mEndDate;

        private int mFlags;

        /**
         * Creates a new instance of the {@code Builder} with the given
         * {@code context}. The {@code context} passed in may be used to pop up
         * some UI to ask the user to unlock or initialize the Android KeyStore
         * facility.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new NullPointerException("context == null");
            }
            mContext = context;
        }

        /**
         * Sets the alias to be used to retrieve the key later from a
         * {@link java.security.KeyStore} instance using the
         * {@code AndroidKeyStore} provider.
         */
        public Builder setAlias(String alias) {
            if (alias == null) {
                throw new NullPointerException("alias == null");
            }
            mKeystoreAlias = alias;
            return this;
        }

        /**
         * Sets the subject used for the self-signed certificate of the
         * generated key pair.
         */
        public Builder setSubject(X500Principal subject) {
            if (subject == null) {
                throw new NullPointerException("subject == null");
            }
            mSubjectDN = subject;
            return this;
        }

        /**
         * Sets the serial number used for the self-signed certificate of the
         * generated key pair.
         */
        public Builder setSerialNumber(BigInteger serialNumber) {
            if (serialNumber == null) {
                throw new NullPointerException("serialNumber == null");
            }
            mSerialNumber = serialNumber;
            return this;
        }

        /**
         * Sets the start of the validity period for the self-signed certificate
         * of the generated key pair.
         */
        public Builder setStartDate(Date startDate) {
            if (startDate == null) {
                throw new NullPointerException("startDate == null");
            }
            mStartDate = startDate;
            return this;
        }

        /**
         * Sets the end of the validity period for the self-signed certificate
         * of the generated key pair.
         */
        public Builder setEndDate(Date endDate) {
            if (endDate == null) {
                throw new NullPointerException("endDate == null");
            }
            mEndDate = endDate;
            return this;
        }

        /**
         * Indicates that this key must be encrypted at rest on storage. Note
         * that enabling this will require that the user enable a strong lock
         * screen (e.g., PIN, password) before creating or using the generated
         * key is successful.
         */
        public Builder setEncryptionRequired() {
            mFlags |= KeyStore.FLAG_ENCRYPTED;
            return this;
        }

        /**
         * Builds the instance of the {@code AndroidKeyPairGeneratorSpec}.
         *
         * @throws IllegalArgumentException if a required field is missing
         * @return built instance of {@code AndroidKeyPairGeneratorSpec}
         */
        public AndroidKeyPairGeneratorSpec build() {
            return new AndroidKeyPairGeneratorSpec(mContext, mKeystoreAlias, mSubjectDN,
                    mSerialNumber, mStartDate, mEndDate, mFlags);
        }
    }
}
