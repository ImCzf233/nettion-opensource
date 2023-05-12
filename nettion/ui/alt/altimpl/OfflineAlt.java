package nettion.ui.alt.altimpl;

import nettion.ui.alt.AccountEnum;
import nettion.ui.alt.Alt;

public final class OfflineAlt extends Alt {
    public OfflineAlt(String userName) {
        super(userName, AccountEnum.OFFLINE);
    }
}
