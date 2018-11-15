/* Copyright (c) Fortanix, Inc.
 *
 * Licensed under the GNU General Public License, version 2 <LICENSE-GPL or
 * https://www.gnu.org/licenses/gpl-2.0.html> or the Apache License, Version
 * 2.0 <LICENSE-APACHE or http://www.apache.org/licenses/LICENSE-2.0>, at your
 * option. This file may not be copied, modified, or distributed except
 * according to those terms. */

package com.fortanix.sdkms.performance.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FPEEncryptRequestPayload implements RequestPayload {

    @JsonProperty("key_id")
    public String keyId;

    public String tweak;

    public int radix;

    @JsonProperty("plain_data")
    public String plainData;

    @JsonProperty("plain_data_list")
    public List<String> plainDataList;

}
