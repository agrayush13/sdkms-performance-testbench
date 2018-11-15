/* Copyright (c) Fortanix, Inc.
 *
 * Licensed under the GNU General Public License, version 2 <LICENSE-GPL or
 * https://www.gnu.org/licenses/gpl-2.0.html> or the Apache License, Version
 * 2.0 <LICENSE-APACHE or http://www.apache.org/licenses/LICENSE-2.0>, at your
 * option. This file may not be copied, modified, or distributed except
 * according to those terms. */

package com.fortanix.sdkms.performance.helper;

public class PluginFactory {

    public static PluginHelper getPlugin(String pluginTypeValue, String pluginId) {
        PluginType pluginType = PluginType.valueOf(pluginTypeValue);
        PluginHelper pluginHelper = null;
        switch (pluginType) {
            case FPEEncrypt:
                pluginHelper = new FPEEncryptPluginHelper(pluginId);
                break;
            case FPEDecrypt:
                pluginHelper = new FPEDecryptPluginHelper(pluginId);
                break;
        }
        return pluginHelper;
    }
}