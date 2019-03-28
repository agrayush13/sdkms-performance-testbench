/* Copyright (c) Fortanix, Inc.
 *
 * Licensed under the GNU General Public License, version 2 <LICENSE-GPL or
 * https://www.gnu.org/licenses/gpl-2.0.html> or the Apache License, Version
 * 2.0 <LICENSE-APACHE or http://www.apache.org/licenses/LICENSE-2.0>, at your
 * option. This file may not be copied, modified, or distributed except
 * according to those terms. */

package com.fortanix.sdkms.performance.sampler;

import com.fortanix.sdkms.v1.ApiException;
import com.fortanix.sdkms.v1.api.SignAndVerifyApi;
import com.fortanix.sdkms.v1.model.*;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.security.ProviderException;
import java.util.logging.Level;
import java.util.ArrayList;

import static com.fortanix.sdkms.performance.sampler.Constants.HASH_ALGORITHM;

public class SignatureVerificationSampler extends AbstractSignatureSampler {

    private VerifyRequest verifyRequest;
    private VerifyRequestEx verifyRequestEx;
    private BatchVerifyRequest batchVerifyRequest;

    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        byte[] signature;
        String hashAlorithm = context.getParameter(HASH_ALGORITHM);
        DigestAlgorithm digestAlgorithm = DigestAlgorithm.fromValue(hashAlorithm);
        if (digestAlgorithm == null) {
            digestAlgorithm = DigestAlgorithm.SHA1;
        }

        try {
            signature = this.signAndVerifyApi.sign(this.keyId, this.signRequest).getSignature();
        } catch (ApiException e) {
            LOGGER.log(Level.INFO, "failure in generating signature : " + e.getMessage(), e);
            throw new ProviderException(e.getMessage());
        }
        this.verifyRequest = new VerifyRequest().hashAlg(digestAlgorithm).hash(this.hash).signature(signature);
        this.verifyRequestEx = new VerifyRequestEx().hashAlg(digestAlgorithm).hash(this.hash).key(new SobjectDescriptor().kid(this.keyId)).signature(signature);
        if (this.batchSignRequest.size() != 0){
            this.batchVerifyRequest = new BatchVerifyRequest();
            for ( int i = 0; i < this.batchSignRequest.size() ; i++ ) {
                this.batchVerifyRequest.add(this.verifyRequestEx);
            }
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        result.sampleStart();
        try {
            retryOperationIfSessionExpires(new RetryableOperation() {
                SignAndVerifyApi signAndVerifyApi;
                String keyId;
                VerifyRequest verifyRequest;
                BatchVerifyRequest batchVerifyRequest;

                RetryableOperation init(SignAndVerifyApi signAndVerifyApi, String keyId, VerifyRequest verifyRequest, BatchVerifyRequest batchVerifyRequest) {
                    this.signAndVerifyApi = signAndVerifyApi;
                    this.keyId = keyId;
                    this.verifyRequest = verifyRequest;
                    this.batchVerifyRequest = batchVerifyRequest;
                    return this;
                }

                @Override
                public Object execute() throws ApiException {
                    if (this.batchVerifyRequest == null) {
                        return this.signAndVerifyApi.verify(this.keyId, this.verifyRequest);
                    } else{
                        return this.signAndVerifyApi.batchVerify(this.batchVerifyRequest);
                    }
                }
            }.init(this.signAndVerifyApi, this.keyId, this.verifyRequest, this.batchVerifyRequest));
            result.setSuccessful(true);
        } catch (ApiException e) {
            LOGGER.log(Level.INFO, "failure in verifying signature : " + e.getMessage(), e);
            result.setSuccessful(false);
        }
        result.sampleEnd();
        return result;
    }
}
