package com.lcsc.cs.lurkserver.Protocol;

import java.util.List;

/**
 * Created by Jake on 3/5/2015.
 */
public interface CommandListener {
    public void notify(List<Command> commands);
}
