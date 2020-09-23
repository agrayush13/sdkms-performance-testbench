package com.fortanix.sdkms.performance.sampler.jce;

import com.fortanix.sdkms.v1.model.CryptMode;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.util.logging.Level;

public class EncryptionJCESampler extends EncryptDecryptBaseJCESampler {
    private static final String AAD = "SampleAAD";

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        result.sampleStart();
        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.key, this.params);
            if(mode.equals(CryptMode.GCM)) {
                cipher.updateAAD(AAD.getBytes());
            }
            cipher.doFinal(this.input.getBytes());
            result.setSuccessful(true);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failure in encrypting : " + e.getMessage(), e);
            result.setSuccessful(false);
        }
        result.sampleEnd();
        return result;
    }
}
