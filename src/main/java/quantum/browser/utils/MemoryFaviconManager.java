package quantum.browser.utils;

import java.util.HashSet;

/**
 * A memory backed favicon manager. Unused.
 */
public class MemoryFaviconManager extends FaviconManager {
    private HashSet<String> custom = new HashSet<>();

    public MemoryFaviconManager(String store) {
        super(store);
    }

    @Override
    protected boolean isCustom(String domain) {
        return custom.contains(domain);
    }

    @Override
    protected void setCustom(String domain, boolean isCustom) {
        if (isCustom)
            custom.add(domain);
        else
            custom.remove(domain);
    }
}
